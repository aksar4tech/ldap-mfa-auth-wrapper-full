package com.example.auth.application;

import com.example.auth.domain.*;
import com.example.auth.ports.*;

import java.util.concurrent.CompletableFuture;

public class AuthenticationService {

    private final LdapAuthenticationPort ldapPort;
    private final MfaProvider mfaProvider;
    private final ChallengeStore challengeStore;
    private final AuditLogger auditLogger;
    private final RateLimiter rateLimiter;

    public AuthenticationService(
            LdapAuthenticationPort ldapPort,
            MfaProvider mfaProvider,
            ChallengeStore challengeStore,
            AuditLogger auditLogger,
            RateLimiter rateLimiter
    ) {
        this.ldapPort = ldapPort;
        this.mfaProvider = mfaProvider;
        this.challengeStore = challengeStore;
        this.auditLogger = auditLogger;
        this.rateLimiter = rateLimiter;
    }

    public AuthResult authenticateAsync(AuthRequest request) {

        return CompletableFuture
                .supplyAsync(() -> {
                    if (!rateLimiter.isAllowed(request.username())) {
                        auditLogger.log(new AuditEvent(
                                java.time.Instant.now(),
                                request.username(),
                                AuditEventType.RATE_LIMIT_EXCEEDED,
                                "Too many attempts"
                        ));
                        return AuthResult.failed("Rate limit exceeded");
                    }

                    if (ldapPort.isAccountLocked(request.username())) {
                        return new AuthResult(AuthStatus.LOCKED, "Account locked", null, null);
                    }

                    return ldapPort.authenticate(request.username(), request.password())
                            .map(user -> {
                                auditLogger.log(new AuditEvent(
                                        java.time.Instant.now(),
                                        user.username(),
                                        AuditEventType.LOGIN_SUCCESS,
                                        "Primary authentication successful"
                                ));

                                // Create the challenge
                                MfaChallenge challenge = challengeStore.create(request.username(),  request.deviceId());

                                // Save the challenge to the inmemory store
                                challengeStore.save(challenge);

                                // Send the challenge to the email for MFA
                                mfaProvider.send(challenge);

                                auditLogger.log(new AuditEvent(
                                        java.time.Instant.now(),
                                        user.username(),
                                        AuditEventType.MFA_TRIGGERED,
                                        "MFA challenge initiated"
                                ));

                                return AuthResult.mfaRequired(challenge.challengeId());
                            })
                            .orElseGet(() -> {
                                rateLimiter.recordFailure(request.username());
                                auditLogger.log(new AuditEvent(
                                        java.time.Instant.now(),
                                        request.username(),
                                        AuditEventType.LOGIN_FAILURE,
                                        "Invalid credentials"
                                ));
                                return AuthResult.failed("Invalid credentials");
                            });

                }, AppExecutors.AUTH_REQUEST_EXECUTOR).join(); // acceptable at CLI boundary
    }

}

