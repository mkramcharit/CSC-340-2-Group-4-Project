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
