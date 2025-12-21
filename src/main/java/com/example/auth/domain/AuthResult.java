package com.example.auth.domain;

public record AuthResult(
        AuthStatus status,
        String message,
        String challengeId,
        String accessToken
) {

    public static AuthResult success(String accessToken) {
        return new AuthResult(
                AuthStatus.SUCCESS,
                "Authentication successful",
                null,
                accessToken
        );
    }

    public static AuthResult mfaRequired(String challengeId) {
        return new AuthResult(
                AuthStatus.MFA_REQUIRED,
                "MFA required",
                challengeId,
                null
        );
    }

    public static AuthResult failed(String reason) {
        return new AuthResult(
                AuthStatus.FAILED,
                reason,
                null,
                null
        );
    }
}

