package com.pcplus.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

// =========================================================
//  All DTO (Data Transfer Object) classes for PC+ API
//  Plain Java — no Lombok dependency.
// =========================================================

// ---- Auth -----------------------------------------------

/** POST /api/auth/login */
class LoginRequest {
    @NotBlank public String email;
    @NotBlank public String password;
}

/** POST /api/auth/signup */
class SignupRequest {
    @Email @NotBlank  public String email;
    @NotBlank         public String password;
    @Pattern(regexp="customer|publisher") public String role;
    @Pattern(regexp="\\d{4}") public String pin;
    public String displayName;
}

/** Response for login & signup */
class AuthResponse {
    public String token;
    public String email;
    public String role;
    public String avatarId;
    public String displayName;
}

// ---- Games ----------------------------------------------

class GameDto {
    public Long id;
    public String title, publisher, description;
    public BigDecimal price, salePrice;
    public String genres, coverImage, screenshots;
    public String reqOs, reqCpu, reqRam, reqGpu, reqStorage;
    public String status;
    public int downloadCount, reviewCount;
    public BigDecimal avgRating;
    public boolean playable;
    public Instant createdAt;
}

class GameRequest {
    @NotBlank public String title;
    public String publisher, description;
    @NotNull public BigDecimal price;
    public BigDecimal salePrice;
    public String genres, coverImage, screenshots;
    public String reqOs, reqCpu, reqRam, reqGpu, reqStorage;
    public String status;
    public boolean playable;
}

// ---- Reviews --------------------------------------------

class ReviewRequest {
    @Min(1) @Max(5) public int rating;
    @NotBlank public String body;
}

class ReviewDto {
    public Long id;
    public String userEmail, userAvatar;
    public int rating;
    public String body, publisherReply;
    public Instant createdAt;
}

class ReplyRequest {
    @NotBlank public String reply;
}

// ---- Cart -----------------------------------------------

class CartItemDto {
    public Long gameId;
    public String title, coverImage;
    public BigDecimal price, salePrice;
}

// ---- Library --------------------------------------------

class LibraryItemDto {
    public Long gameId;
    public String title, coverImage;
    public BigDecimal pricePaid;
    public Instant purchasedAt;
    public boolean playable;
}

// ---- Publisher Dashboard --------------------------------

class DashboardDto {
    public int totalGames, totalDownloads, totalReviews;
    public BigDecimal avgRating;
}
