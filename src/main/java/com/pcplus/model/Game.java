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
