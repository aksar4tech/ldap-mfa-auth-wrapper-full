package com.example.auth.adapters;

import com.example.auth.ports.MfaTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtMfaTokenServiceAdapter implements MfaTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtMfaTokenServiceAdapter.class);

    private final Key key;
    private final long expirySeconds;

    public JwtMfaTokenServiceAdapter(byte[] secret, long expirySeconds) {
        this.key = Keys.hmacShaKeyFor(secret);
        this.expirySeconds = expirySeconds;
    }

    @Override
    public String generateToken(String username, String challengeId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("challengeId", challengeId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirySeconds)))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token, String challengeId) {
        try {
            var claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return challengeId.equals(claims.get("challengeId", String.class));
        } catch (Exception e) {
            log.error("Error while validating MFA token for challengeId: {}, message: {}", challengeId, e.getMessage());
            return false;
        }
    }
}

