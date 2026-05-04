package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    static class SignupReq {
        @Email
        @NotBlank
        public String email;

        @NotBlank
        public String password;

        @Pattern(regexp = "customer|publisher")
        @NotBlank
        public String role;

        @Pattern(regexp = "\\d{4}")
        @NotBlank
        public String pin;

        public String displayName;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReq req) {
        return ResponseEntity.ok(authService.signup(req.email, req.password, req.role, req.pin, req.displayName));
    }

    static class LoginReq {
        @NotBlank
        public String email;

        @NotBlank
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {
        return ResponseEntity.ok(authService.login(req.email, req.password));
    }

    static class ResetReq {
        @NotBlank
        public String email;

        @Pattern(regexp = "\\d{4}")
        @NotBlank
        public String pin;

        @NotBlank
        public String newPassword;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetReq req) {
        return ResponseEntity.ok(authService.resetPassword(req.email, req.pin, req.newPassword));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.me((User) auth.getPrincipal()));
    }

    static class AvatarReq {
        @NotBlank
        public String avatarId;
    }

    @PatchMapping("/avatar")
    public ResponseEntity<?> changeAvatar(Authentication auth, @Valid @RequestBody AvatarReq req) {
        return ResponseEntity.ok(authService.changeAvatar((User) auth.getPrincipal(), req.avatarId));
    }
}
