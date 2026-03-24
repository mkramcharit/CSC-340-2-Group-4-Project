package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import com.pcplus.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users   = users;
        this.encoder = encoder;
        this.jwt     = jwt;
    }

    // ---------- Signup ----------

    static class SignupReq {
        @Email @NotBlank  public String email;
        @NotBlank         public String password;
        @Pattern(regexp = "customer|publisher") @NotBlank public String role;
        @Pattern(regexp = "\\d{4}")             @NotBlank public String pin;
        public String displayName;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReq req) {
        String email = req.email.trim().toLowerCase();
        if (users.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_taken"));
        }
        User user = User.builder()
                .email(email)
                .passwordHash(encoder.encode(req.password))
                .role(req.role)
                .pin(req.pin)
                .displayName(req.displayName != null ? req.displayName : email.split("@")[0])
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        users.save(user);
        return ResponseEntity.ok(buildAuthResponse(user));
    }

    // ---------- Login ----------

    static class LoginReq {
        @NotBlank public String email;
        @NotBlank public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {
        String email = req.email.trim().toLowerCase();
        User user = users.findByEmail(email).orElse(null);
        if (user == null || !encoder.matches(req.password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }
        if (!user.isActive()) {
            return ResponseEntity.status(403).body(Map.of("error", "account_inactive"));
        }
        return ResponseEntity.ok(buildAuthResponse(user));
    }

    // ---------- Reset password ----------

    static class ResetReq {
        @NotBlank public String email;
        @Pattern(regexp = "\\d{4}") @NotBlank public String pin;
        @NotBlank public String newPassword;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetReq req) {
        String email = req.email.trim().toLowerCase();
        User user = users.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("error", "account_not_found"));
        if (!user.getPin().equals(req.pin)) return ResponseEntity.badRequest().body(Map.of("error", "pin_mismatch"));
        user.setPasswordHash(encoder.encode(req.newPassword));
        user.setUpdatedAt(Instant.now());
        users.save(user);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // ---------- Me ----------

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "email",       user.getEmail(),
                "role",        user.getRole(),
                "avatarId",    user.getAvatarId(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : ""
        ));
    }

    // ---------- Change avatar ----------

    static class AvatarReq { @NotBlank public String avatarId; }

    @PatchMapping("/avatar")
    public ResponseEntity<?> changeAvatar(
            org.springframework.security.core.Authentication auth,
            @Valid @RequestBody AvatarReq req) {
        User user = (User) auth.getPrincipal();
        user.setAvatarId(req.avatarId);
        users.save(user);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // ---------- Helpers ----------

    private Map<String, Object> buildAuthResponse(User user) {
        String token = jwt.generate(user.getEmail(), user.getRole());
        return Map.of(
                "token",       token,
                "email",       user.getEmail(),
                "role",        user.getRole(),
                "avatarId",    user.getAvatarId(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : ""
        );
    }
}
