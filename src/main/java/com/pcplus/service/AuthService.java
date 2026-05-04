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

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        user.setPin(pin);
        user.setDisplayName(displayName != null ? displayName : normalizedEmail.split("@")[0]);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        users.save(user);
        return buildAuthResponse(user);
    }

    public Map<String, Object> login(String email, String password) {
        // Makes the email consistent before checking the database for the user account
        String normalizedEmail = normalizeEmail(email);

        // Looks for a user account with the matching email in the database
        User user = users.findByEmail(normalizedEmail).orElse(null);

        // If no user is found or the password does not match the one in the database, login is rejected
        if (user == null || !encoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "invalid_credentials");
        }

        // Inactive accounts cannot login to the system even with the correct password
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "account_inactive");
        }

        // Builds the login response object containing the user information and authentication token
        return buildAuthResponse(user);
    }

    public Map<String, Object> resetPassword(String email, String pin, String newPassword) {
        // Makes the email consistent before searching for the account
        String normalizedEmail = normalizeEmail(email);

        // Finds the user account associated with the email
        User user = users.findByEmail(normalizedEmail).orElse(null);

        // If the user account does not exist, then the password cannot be reset
        if (user == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "account_not_found");
        }

        // Checks if the pin entered matches the pin on the user account
        if (!user.getPin().equals(pin)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "pin_mismatch");
        }

        // Saves the new password as an encoded hash of the password
        user.setPasswordHash(encoder.encode(newPassword));

        // Changes the last password changed timestamp on the account
        user.setUpdatedAt(Instant.now());

        // Saves the user account back to the database
        users.save(user);

        // Sends confirmation that the password was reset successfully
        return Map.of("ok", true);
    }

    public Map<String, Object> me(User user) {
        return Map.of(
                "email", user.getEmail(),
                "role", user.getRole(),
                "avatarId", safe(user.getAvatarId()),
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
                "avatarId", safe(user.getAvatarId()),
                "displayName", safe(user.getDisplayName())
        );
    }
}