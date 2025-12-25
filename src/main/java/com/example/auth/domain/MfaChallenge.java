package com.example.auth.domain;

import java.time.Instant;

public record MfaChallenge(
        String challengeId,
        String username,
        String deviceId,
        Instant expiresAt,
        ChallengeStatus status
) {}

