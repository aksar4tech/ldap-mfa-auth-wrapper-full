package com.example.auth.domain;

public record AuthRequest(
        String username,
        char[] password,
        String deviceId
) {}

