package com.example.auth.adapters;

import com.example.auth.application.AppExecutors;
import com.example.auth.domain.MfaChallenge;
import com.example.auth.domain.MfaVerification;
import com.example.auth.ports.EmailSender;
import com.example.auth.ports.MfaProvider;
import com.example.auth.ports.MfaTokenService;

public class EmailMagicLinkMfaProvider implements MfaProvider {

    private final EmailSender emailSender;
    private final MfaTokenService mfaTokenService;
    private final long mfaTokenTtlSeconds;

    public EmailMagicLinkMfaProvider(
            EmailSender emailSender,
            MfaTokenService mfaTokenService,
            long mfaTokenTtlSeconds
    ) {
        this.emailSender = emailSender;
        this.mfaTokenService = mfaTokenService;
        this.mfaTokenTtlSeconds = mfaTokenTtlSeconds;
    }

    @Override
    public void send(MfaChallenge challenge) {

        String token = mfaTokenService.generateToken(challenge.username(), challenge.challengeId());

        String body = """
                Hi %s,

                Please verify your login.

                Challenge ID:
                %s

                MFA Token:
                %s

                This token is valid for %d minutes.
                """.formatted(
                challenge.username(),
                challenge.challengeId(),
                token,
                mfaTokenTtlSeconds / 60
        );

        AppExecutors.NOTIFICATION_EXECUTOR.submit(() ->
                emailSender.send(
                        challenge.username(),   // or email from LDAP
                        "Login Verification",
                        body
                )
        );
    }

    @Override
    public boolean verify(MfaVerification verification) {
        return mfaTokenService.validateToken(
                verification.token(),
                verification.challengeId()
        );
    }
}

