package com.example.auth.ports;

public interface MfaTokenService {

    String generateToken(String username, String challengeId);

    boolean validateToken(String token, String challengeId);
}

