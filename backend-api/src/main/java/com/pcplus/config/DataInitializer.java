package com.pcplus.config;

import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Runs once on every app startup.
 * Ensures the two demo accounts always exist in the database,
 * so the SQL schema file's INSERT is optional (but still provided
 * as a backup in schema.sql).
 *
 * Demo accounts:
 *   email: customer   password: customer   role: customer   PIN: 0000
 *   email: publisher  password: publisher  role: publisher  PIN: 0000
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository users, PasswordEncoder encoder) {
        this.users   = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        seedDemo("customer",  "customer",  "customer",  "0000", "Demo Customer");
        seedDemo("publisher", "publisher", "publisher", "0000", "Demo Publisher");
    }

    private void seedDemo(String email, String password, String role,
                          String pin, String displayName) {
        if (users.existsByEmail(email)) {
            return; // Already exists — leave it alone (don't overwrite user edits)
        }
        User u = User.builder()
                .email(email)
                .passwordHash(encoder.encode(password))
                .role(role)
                .pin(pin)
                .avatarId("av1")
                .displayName(displayName)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        users.save(u);
        System.out.println("[PC+] Seeded demo account: " + email + " (" + role + ")");
    }
}
