package com.pcplus.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents a registered user (customer or publisher).
 * Maps to the "users" table in Neon/PostgreSQL.
 *
 * No Lombok — explicit getters/setters/builder for Java 21 compatibility.
 */
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 4)
    private String pin;

    @Column(nullable = false, length = 10)
    private String avatarId = "av1";

    @Column(length = 100)
    private String displayName;

    @Column(length = 32)
    private String payoutMethod;

    @Column(length = 255)
    private String supportContact;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(nullable = false)
    private boolean active = true;

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public User() {}

    // ---- Builder ----

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String email; private String passwordHash;
        private String role; private String pin; private String avatarId = "av1";
        private String displayName; private String payoutMethod;
        private String supportContact; private Instant createdAt = Instant.now();
        private Instant updatedAt = Instant.now(); private boolean active = true;

        public Builder id(Long v)           { id = v; return this; }
        public Builder email(String v)       { email = v; return this; }
        public Builder passwordHash(String v){ passwordHash = v; return this; }
        public Builder role(String v)        { role = v; return this; }
        public Builder pin(String v)         { pin = v; return this; }
        public Builder avatarId(String v)    { avatarId = v; return this; }
        public Builder displayName(String v) { displayName = v; return this; }
        public Builder payoutMethod(String v){ payoutMethod = v; return this; }
        public Builder supportContact(String v) { supportContact = v; return this; }
        public Builder createdAt(Instant v)  { createdAt = v; return this; }
        public Builder updatedAt(Instant v)  { updatedAt = v; return this; }
        public Builder active(boolean v)     { active = v; return this; }

        public User build() {
            User u = new User();
            u.id = id; u.email = email; u.passwordHash = passwordHash;
            u.role = role; u.pin = pin;
            u.avatarId    = avatarId    != null ? avatarId    : "av1";
            u.displayName = displayName;
            u.payoutMethod = payoutMethod;
            u.supportContact = supportContact;
            u.createdAt   = createdAt   != null ? createdAt   : Instant.now();
            u.updatedAt   = updatedAt   != null ? updatedAt   : Instant.now();
            u.active = active;
            return u;
        }
    }

    // ---- Getters ----
    public Long    getId()           { return id; }
    public String  getEmail()        { return email; }
    public String  getPasswordHash() { return passwordHash; }
    public String  getRole()         { return role; }
    public String  getPin()          { return pin; }
    public String  getAvatarId()     { return avatarId; }
    public String  getDisplayName()  { return displayName; }
    public String  getPayoutMethod() { return payoutMethod; }
    public String  getSupportContact() { return supportContact; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }
    public boolean isActive()        { return active; }

    // ---- Setters ----
    public void setId(Long v)           { id = v; }
    public void setEmail(String v)       { email = v; }
    public void setPasswordHash(String v){ passwordHash = v; }
    public void setRole(String v)        { role = v; }
    public void setPin(String v)         { pin = v; }
    public void setAvatarId(String v)    { avatarId = v; }
    public void setDisplayName(String v) { displayName = v; }
    public void setPayoutMethod(String v) { payoutMethod = v; }
    public void setSupportContact(String v) { supportContact = v; }
    public void setCreatedAt(Instant v)  { createdAt = v; }
    public void setUpdatedAt(Instant v)  { updatedAt = v; }
    public void setActive(boolean v)     { active = v; }
}
