package com.example.auth.adapters;

import com.example.auth.ports.AccessTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtAccessTokenServiceAdapter implements AccessTokenService {

    private final Key key;
    private final long accessTokenTtlSeconds;

    public JwtAccessTokenServiceAdapter(byte[] secret, long accessTokenTtlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret);
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    @Override
    public String generateAccessToken(String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("scope", "USER")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
                .signWith(key)
                .compact();
    }
}
