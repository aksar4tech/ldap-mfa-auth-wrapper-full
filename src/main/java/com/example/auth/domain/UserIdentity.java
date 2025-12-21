package com.example.auth.domain;

public record UserIdentity(
        String username,
        String dn,
        String email
) {}
