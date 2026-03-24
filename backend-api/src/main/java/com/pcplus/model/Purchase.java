package com.pcplus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Records a simulated purchase — adds the game to the user's library.
 */
@Entity
@Table(name = "purchases",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private Game game;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private Instant purchasedAt = Instant.now();

    // ---- Constructors ----

    public Purchase() {}

    // ---- Builder ----

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private Game game;
        private BigDecimal pricePaid = BigDecimal.ZERO;
        private Instant purchasedAt = Instant.now();

        public Builder id(Long id)               { this.id = id; return this; }
        public Builder user(User user)           { this.user = user; return this; }
        public Builder game(Game game)           { this.game = game; return this; }
        public Builder pricePaid(BigDecimal p)   { this.pricePaid = p; return this; }
        public Builder purchasedAt(Instant t)    { this.purchasedAt = t; return this; }

        public Purchase build() {
            Purchase p = new Purchase();
            p.id = id; p.user = user; p.game = game;
            p.pricePaid = pricePaid != null ? pricePaid : BigDecimal.ZERO;
            p.purchasedAt = purchasedAt != null ? purchasedAt : Instant.now();
            return p;
        }
    }

    // ---- Getters ----

    public Long       getId()          { return id; }
    public User       getUser()        { return user; }
    public Game       getGame()        { return game; }
    public BigDecimal getPricePaid()   { return pricePaid; }
    public Instant    getPurchasedAt() { return purchasedAt; }

    // ---- Setters ----

    public void setId(Long id)               { this.id = id; }
    public void setUser(User user)           { this.user = user; }
    public void setGame(Game game)           { this.game = game; }
    public void setPricePaid(BigDecimal p)   { this.pricePaid = p; }
    public void setPurchasedAt(Instant t)    { this.purchasedAt = t; }
}
