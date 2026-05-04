package com.pcplus.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Generates and validates compact JWT tokens used as Bearer credentials.
 * Tokens encode the user's email and role so the backend can authorise
 * requests without hitting the database on every call.
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${pcplus.jwt.secret}") String secret,
            @Value("${pcplus.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** Create a signed JWT for the given user. */
    public String generate(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Parse + validate token; returns Claims on success. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Extract email (subject) without full validation – use after parse(). */
    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    /** Extract role claim. */
    public String extractRole(String token) {
        return parse(token).get("role", String.class);
    }

    /** True if the token is valid and not expired. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
