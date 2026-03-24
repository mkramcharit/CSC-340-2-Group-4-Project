package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import com.pcplus.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public Map<String, Object> signup(String email, String password, String role, String pin, String displayName) {
        String normalizedEmail = normalizeEmail(email);
        if (users.existsByEmail(normalizedEmail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "email_taken");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(encoder.encode(password))
                .role(role)
                .pin(pin)
                .displayName(displayName != null ? displayName : normalizedEmail.split("@")[0])
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        users.save(user);
        return buildAuthResponse(user);
    }

    public Map<String, Object> login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        User user = users.findByEmail(normalizedEmail).orElse(null);
        if (user == null || !encoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "invalid_credentials");
        }

        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "account_inactive");
        }

        return buildAuthResponse(user);
    }

    public Map<String, Object> resetPassword(String email, String pin, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = users.findByEmail(normalizedEmail).orElse(null);
        if (user == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "account_not_found");
        }

        if (!user.getPin().equals(pin)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "pin_mismatch");
        }

        user.setPasswordHash(encoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        users.save(user);

        return Map.of("ok", true);
    }

    public Map<String, Object> me(User user) {
        return Map.of(
                "email", user.getEmail(),
                "role", user.getRole(),
                "avatarId", user.getAvatarId(),
                "displayName", safe(user.getDisplayName())
        );
    }

    public Map<String, Object> changeAvatar(User user, String avatarId) {
        user.setAvatarId(avatarId);
        users.save(user);
        return Map.of("ok", true);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Map<String, Object> buildAuthResponse(User user) {
        String token = jwt.generate(user.getEmail(), user.getRole());
        return Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole(),
                "avatarId", user.getAvatarId(),
                "displayName", safe(user.getDisplayName())
        );
    }
}
