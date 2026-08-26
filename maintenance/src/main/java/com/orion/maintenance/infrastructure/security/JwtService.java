package com.orion.maintenance.infrastructure.security;

import com.orion.maintenance.application.port.TokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements TokenIssuer {

    private static final String CLAIM_ROL = "rol";

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${orion.jwt.secret}") String secret,
            @Value("${orion.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String issue(String email, String rol) {
        return generateToken(email, rol);
    }

    public String generateToken(String email, String rol) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROL, rol)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRol(Claims claims) {
        return claims.get(CLAIM_ROL, String.class);
    }
}
