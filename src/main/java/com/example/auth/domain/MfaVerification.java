package com.example.auth.domain;

public record MfaVerification(
        String challengeId,
        String token,
        String deviceId
) {}

