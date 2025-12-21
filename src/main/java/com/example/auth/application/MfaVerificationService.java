
package com.example.auth.application;

import com.example.auth.domain.*;
import com.example.auth.ports.AccessTokenService;
import com.example.auth.ports.AuditLogger;
import com.example.auth.ports.ChallengeStore;
import com.example.auth.ports.MfaProvider;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class MfaVerificationService {

    private final MfaProvider mfaProvider;
    private final ChallengeStore challengeStore;
    private final AuditLogger auditLogger;
    private final AccessTokenService accessTokenService;

    public MfaVerificationService(
            MfaProvider mfaProvider,
            ChallengeStore challengeStore,
            AuditLogger auditLogger,
            AccessTokenService accessTokenService
    ) {
        this.mfaProvider = mfaProvider;
        this.challengeStore = challengeStore;
        this.auditLogger = auditLogger;
        this.accessTokenService = accessTokenService;
    }

    public AuthResult verify(MfaVerification verification) {

        return CompletableFuture
                .supplyAsync(() -> {

                    var challengeOpt = challengeStore.findById(verification.challengeId());

                    if (challengeOpt.isEmpty()) {
                        return AuthResult.failed("Invalid challenge");
                    }

                    MfaChallenge challenge = challengeOpt.get();

                    if (challenge.expiresAt().isBefore(Instant.now())) {
                        return AuthResult.failed("Challenge expired");
                    }

                    // Verify MFA token
                    boolean valid = mfaProvider.verify(verification);

                    if (!valid) {
                        auditLogger.log(new AuditEvent(
                                Instant.now(),
                                challenge.username(),
                                AuditEventType.MFA_FAILURE,
                                "Invalid MFA token"
                        ));
                        return AuthResult.failed("Invalid MFA token");
                    }

                    // MFA successful → issue access token
                    String accessToken = accessTokenService.generateAccessToken(challenge.username());

                    auditLogger.log(new AuditEvent(
                            Instant.now(),
                            challenge.username(),
                            AuditEventType.MFA_SUCCESS,
                            "MFA verification successful"
                    ));

                    challengeStore.delete(challenge.challengeId());

                    return AuthResult.success(accessToken);
                }, AppExecutors.VERIFY_REQUEST_EXECUTOR).join(); // acceptable at CLI boundary
    }
}
