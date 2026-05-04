package com.pcplus.model;

import jakarta.persistence.*;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A single game held in a user's shopping cart.
 */
@Entity
@Table(name = "cart_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, updatable = false)
    private Instant addedAt = Instant.now();

    // ---- Constructors ----

    public CartItem() {}

    // ---- Getters ----

    public Long    getId()      { return id; }
    public User    getUser()    { return user; }
    public Game    getGame()    { return game; }
    public Instant getAddedAt() { return addedAt; }

    // ---- Setters ----

    public void setId(Long id)         { this.id = id; }
    public void setUser(User user)     { this.user = user; }
    public void setGame(Game game)     { this.game = game; }
    public void setAddedAt(Instant t)  { this.addedAt = t; }
}
