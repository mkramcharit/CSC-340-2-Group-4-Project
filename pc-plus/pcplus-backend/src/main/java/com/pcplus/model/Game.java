package com.pcplus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A game listing in the PC+ catalog.
 * Maps to the "games" table.
 *
 * No Lombok — explicit getters/setters/builder for Java 21 compatibility.
 */
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 150)
    private String publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(length = 300)
    private String genres;

    @Column(length = 255)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String screenshots;

    @Column(length = 100) private String reqOs;
    @Column(length = 100) private String reqCpu;
    @Column(length = 100) private String reqRam;
    @Column(length = 100) private String reqGpu;
    @Column(length = 100) private String reqStorage;

    @Column(nullable = false, length = 20)
    private String status = "live";

    @Column(nullable = false)
    private int downloadCount = 0;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false)
    private boolean playable = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public Game() {}

    // ---- Builder ----

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String title; private String publisher;
        private User owner; private String description;
        private BigDecimal price = BigDecimal.ZERO; private BigDecimal salePrice;
        private String genres; private String coverImage; private String screenshots;
        private String reqOs; private String reqCpu; private String reqRam;
        private String reqGpu; private String reqStorage;
        private String status = "live"; private int downloadCount = 0;
        private BigDecimal avgRating = BigDecimal.ZERO; private int reviewCount = 0;
        private boolean playable = false;
        private Instant createdAt = Instant.now(); private Instant updatedAt = Instant.now();

        public Builder id(Long v)               { id = v; return this; }
        public Builder title(String v)           { title = v; return this; }
        public Builder publisher(String v)       { publisher = v; return this; }
        public Builder owner(User v)             { owner = v; return this; }
        public Builder description(String v)     { description = v; return this; }
        public Builder price(BigDecimal v)       { price = v; return this; }
        public Builder salePrice(BigDecimal v)   { salePrice = v; return this; }
        public Builder genres(String v)          { genres = v; return this; }
        public Builder coverImage(String v)      { coverImage = v; return this; }
        public Builder screenshots(String v)     { screenshots = v; return this; }
        public Builder reqOs(String v)           { reqOs = v; return this; }
        public Builder reqCpu(String v)          { reqCpu = v; return this; }
        public Builder reqRam(String v)          { reqRam = v; return this; }
        public Builder reqGpu(String v)          { reqGpu = v; return this; }
        public Builder reqStorage(String v)      { reqStorage = v; return this; }
        public Builder status(String v)          { status = v; return this; }
        public Builder downloadCount(int v)      { downloadCount = v; return this; }
        public Builder avgRating(BigDecimal v)   { avgRating = v; return this; }
        public Builder reviewCount(int v)        { reviewCount = v; return this; }
        public Builder playable(boolean v)       { playable = v; return this; }
        public Builder createdAt(Instant v)      { createdAt = v; return this; }
        public Builder updatedAt(Instant v)      { updatedAt = v; return this; }

        public Game build() {
            Game g = new Game();
            g.id = id; g.title = title; g.publisher = publisher; g.owner = owner;
            g.description = description;
            g.price       = price       != null ? price       : BigDecimal.ZERO;
            g.salePrice   = salePrice;
            g.genres = genres; g.coverImage = coverImage; g.screenshots = screenshots;
            g.reqOs = reqOs; g.reqCpu = reqCpu; g.reqRam = reqRam;
            g.reqGpu = reqGpu; g.reqStorage = reqStorage;
            g.status       = status       != null ? status       : "live";
            g.downloadCount = downloadCount;
            g.avgRating    = avgRating    != null ? avgRating    : BigDecimal.ZERO;
            g.reviewCount  = reviewCount;
            g.playable     = playable;
            g.createdAt    = createdAt    != null ? createdAt    : Instant.now();
            g.updatedAt    = updatedAt    != null ? updatedAt    : Instant.now();
            return g;
        }
    }

    // ---- Getters ----
    public Long       getId()            { return id; }
    public String     getTitle()         { return title; }
    public String     getPublisher()     { return publisher; }
    public User       getOwner()         { return owner; }
    public String     getDescription()   { return description; }
    public BigDecimal getPrice()         { return price; }
    public BigDecimal getSalePrice()     { return salePrice; }
    public String     getGenres()        { return genres; }
    public String     getCoverImage()    { return coverImage; }
    public String     getScreenshots()   { return screenshots; }
    public String     getReqOs()         { return reqOs; }
    public String     getReqCpu()        { return reqCpu; }
    public String     getReqRam()        { return reqRam; }
    public String     getReqGpu()        { return reqGpu; }
    public String     getReqStorage()    { return reqStorage; }
    public String     getStatus()        { return status; }
    public int        getDownloadCount() { return downloadCount; }
    public BigDecimal getAvgRating()     { return avgRating; }
    public int        getReviewCount()   { return reviewCount; }
    public boolean    isPlayable()       { return playable; }
    public Instant    getCreatedAt()     { return createdAt; }
    public Instant    getUpdatedAt()     { return updatedAt; }

    // ---- Setters ----
    public void setId(Long v)               { id = v; }
    public void setTitle(String v)           { title = v; }
    public void setPublisher(String v)       { publisher = v; }
    public void setOwner(User v)             { owner = v; }
    public void setDescription(String v)     { description = v; }
    public void setPrice(BigDecimal v)       { price = v; }
    public void setSalePrice(BigDecimal v)   { salePrice = v; }
    public void setGenres(String v)          { genres = v; }
    public void setCoverImage(String v)      { coverImage = v; }
    public void setScreenshots(String v)     { screenshots = v; }
    public void setReqOs(String v)           { reqOs = v; }
    public void setReqCpu(String v)          { reqCpu = v; }
    public void setReqRam(String v)          { reqRam = v; }
    public void setReqGpu(String v)          { reqGpu = v; }
    public void setReqStorage(String v)      { reqStorage = v; }
    public void setStatus(String v)          { status = v; }
    public void setDownloadCount(int v)      { downloadCount = v; }
    public void setAvgRating(BigDecimal v)   { avgRating = v; }
    public void setReviewCount(int v)        { reviewCount = v; }
    public void setPlayable(boolean v)       { playable = v; }
    public void setCreatedAt(Instant v)      { createdAt = v; }
    public void setUpdatedAt(Instant v)      { updatedAt = v; }
}
