package com.example.auth.adapters;

import com.example.auth.application.AppExecutors;
import com.example.auth.config.AppConfig;
import com.example.auth.domain.ChallengeStatus;
import com.example.auth.domain.MfaChallenge;
import com.example.auth.domain.MfaVerification;
import com.example.auth.domain.UserIdentity;
import com.example.auth.ports.EmailSender;
import com.example.auth.ports.MfaProvider;
import com.example.auth.ports.MfaTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

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
    public MfaChallenge initiate(UserIdentity user) {

        String challengeId = UUID.randomUUID().toString();
        String token = mfaTokenService.generateToken(user.username(), challengeId);

        String body = """
                Hello %s,

                Click the link below to complete authentication:
                
                token: %s
                challengeId: %s

                This link expires in 10 minutes.
                """.formatted(user.username(), token, challengeId);

        AppExecutors.NOTIFICATION_EXECUTOR.submit( () ->
            emailSender.send(
                    user.email(),
                    "Your login verification request",
                    body
            )
        );

        return new MfaChallenge(
                challengeId,
                user.username(),
                Instant.now().plusSeconds(mfaTokenTtlSeconds),
                ChallengeStatus.PENDING
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

