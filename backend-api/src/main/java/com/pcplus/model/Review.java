package com.pcplus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * A customer review for a game.
 * A user may leave at most ONE review per game.
 */
@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private Game game;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String publisherReply;

    @Column
    private Instant repliedAt;

    @Column(nullable = false)
    private boolean removed = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    // ---- Constructors ----

    public Review() {}

    // ---- Builder ----

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private Game game;
        private int rating;
        private String body;
        private String publisherReply;
        private Instant repliedAt;
        private boolean removed = false;
        private Instant createdAt = Instant.now();
        private Instant updatedAt = Instant.now();

        public Builder id(Long id)                     { this.id = id; return this; }
        public Builder user(User user)                 { this.user = user; return this; }
        public Builder game(Game game)                 { this.game = game; return this; }
        public Builder rating(int rating)              { this.rating = rating; return this; }
        public Builder body(String body)               { this.body = body; return this; }
        public Builder publisherReply(String r)        { this.publisherReply = r; return this; }
        public Builder repliedAt(Instant t)            { this.repliedAt = t; return this; }
        public Builder removed(boolean removed)        { this.removed = removed; return this; }
        public Builder createdAt(Instant t)            { this.createdAt = t; return this; }
        public Builder updatedAt(Instant t)            { this.updatedAt = t; return this; }

        public Review build() {
            Review r = new Review();
            r.id = id; r.user = user; r.game = game; r.rating = rating;
            r.body = body; r.publisherReply = publisherReply; r.repliedAt = repliedAt;
            r.removed = removed;
            r.createdAt = createdAt != null ? createdAt : Instant.now();
            r.updatedAt = updatedAt != null ? updatedAt : Instant.now();
            return r;
        }
    }

    // ---- Getters ----

    public Long    getId()             { return id; }
    public User    getUser()           { return user; }
    public Game    getGame()           { return game; }
    public int     getRating()         { return rating; }
    public String  getBody()           { return body; }
    public String  getPublisherReply() { return publisherReply; }
    public Instant getRepliedAt()      { return repliedAt; }
    public boolean isRemoved()         { return removed; }
    public Instant getCreatedAt()      { return createdAt; }
    public Instant getUpdatedAt()      { return updatedAt; }

    // ---- Setters ----

    public void setId(Long id)                     { this.id = id; }
    public void setUser(User user)                 { this.user = user; }
    public void setGame(Game game)                 { this.game = game; }
    public void setRating(int rating)              { this.rating = rating; }
    public void setBody(String body)               { this.body = body; }
    public void setPublisherReply(String r)        { this.publisherReply = r; }
    public void setRepliedAt(Instant t)            { this.repliedAt = t; }
    public void setRemoved(boolean removed)        { this.removed = removed; }
    public void setCreatedAt(Instant t)            { this.createdAt = t; }
    public void setUpdatedAt(Instant t)            { this.updatedAt = t; }
}
