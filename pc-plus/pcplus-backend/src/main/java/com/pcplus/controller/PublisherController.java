package com.pcplus.controller;

import com.pcplus.model.*;
import com.pcplus.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publisher")
@PreAuthorize("hasRole('PUBLISHER')")
public class PublisherController {

    private final GameRepository     games;
    private final ReviewRepository   reviews;
    private final PurchaseRepository purchases;
    private final UserRepository     users;

    public PublisherController(GameRepository games, ReviewRepository reviews,
                               PurchaseRepository purchases, UserRepository users) {
        this.games     = games;
        this.reviews   = reviews;
        this.purchases = purchases;
        this.users     = users;
    }

    @GetMapping("/games")
    public List<Game> myGames(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return games.findByOwner(user);
    }

    static class GameReq {
        @NotBlank public String title;
        public String publisher, description;
        @NotNull public BigDecimal price;
        public BigDecimal salePrice;
        public String genres, coverImage, screenshots;
        public String reqOs, reqCpu, reqRam, reqGpu, reqStorage;
        public String status;
        public boolean playable;
    }

    @GetMapping("/profile")
    public Map<String, Object> myProfile(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return profileResponse(user);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, Object> req) {
        User user = (User) auth.getPrincipal();

        if (req.containsKey("displayName")) {
            String displayName = normalizeOptional(asString(req.get("displayName")));
            if (displayName != null && displayName.length() > 100) {
                return ResponseEntity.badRequest().body(Map.of("error", "invalid_display_name"));
            }
            user.setDisplayName(displayName);
        }

        if (req.containsKey("payoutMethod")) {
            String payoutMethod = normalizeOptional(asString(req.get("payoutMethod")));
            if (payoutMethod != null) {
                payoutMethod = payoutMethod.toLowerCase();
                if (!"bank_transfer".equals(payoutMethod) && !"paypal".equals(payoutMethod)) {
                    return ResponseEntity.badRequest().body(Map.of("error", "invalid_payout_method"));
                }
            }
            user.setPayoutMethod(payoutMethod);
        }

        if (req.containsKey("supportContact")) {
            String supportContact = normalizeOptional(asString(req.get("supportContact")));
            if (supportContact != null) {
                supportContact = supportContact.toLowerCase();
                if (supportContact.length() > 255 ||
                    !supportContact.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "invalid_support_contact"));
                }
            }
            user.setSupportContact(supportContact);
        }

        users.save(user);

        return ResponseEntity.ok(profileResponse(user));
    }

    @PostMapping("/games")
    public ResponseEntity<Game> createGame(Authentication auth, @Valid @RequestBody GameReq req) {
        User user = (User) auth.getPrincipal();
        Game g = Game.builder()
                .owner(user)
                .title(req.title)
                .publisher(req.publisher != null ? req.publisher : user.getDisplayName())
                .description(req.description)
                .price(req.price)
                .salePrice(req.salePrice)
                .genres(req.genres)
                .coverImage(req.coverImage)
                .screenshots(req.screenshots)
                .reqOs(req.reqOs).reqCpu(req.reqCpu).reqRam(req.reqRam)
                .reqGpu(req.reqGpu).reqStorage(req.reqStorage)
                .status(req.status != null ? req.status : "live")
                .playable(req.playable)
                .build();
        return ResponseEntity.ok(games.save(g));
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<?> updateGame(Authentication auth, @PathVariable Long id,
                                        @Valid @RequestBody GameReq req) {
        User user = (User) auth.getPrincipal();
        Game g = games.findById(id).orElse(null);
        if (g == null) return ResponseEntity.notFound().build();
        if (!g.getOwner().getId().equals(user.getId()))
            return ResponseEntity.status(403).body(Map.of("error", "not_owner"));
        g.setTitle(req.title);
        if (req.publisher != null) g.setPublisher(req.publisher);
        g.setDescription(req.description);
        g.setPrice(req.price);
        g.setSalePrice(req.salePrice);
        g.setGenres(req.genres);
        g.setCoverImage(req.coverImage);
        g.setScreenshots(req.screenshots);
        g.setReqOs(req.reqOs); g.setReqCpu(req.reqCpu); g.setReqRam(req.reqRam);
        g.setReqGpu(req.reqGpu); g.setReqStorage(req.reqStorage);
        if (req.status != null) g.setStatus(req.status);
        g.setPlayable(req.playable);
        return ResponseEntity.ok(games.save(g));
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<?> deleteGame(Authentication auth, @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        Game g = games.findById(id).orElse(null);
        if (g == null) return ResponseEntity.notFound().build();
        if (!g.getOwner().getId().equals(user.getId()))
            return ResponseEntity.status(403).body(Map.of("error", "not_owner"));
        games.delete(g);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Game> myGames = games.findByOwner(user);
        int totalGames     = myGames.size();
        int totalDownloads = myGames.stream().mapToInt(Game::getDownloadCount).sum();
        int totalReviews   = myGames.stream().mapToInt(Game::getReviewCount).sum();
        double avgRating   = myGames.stream()
                .filter(g -> g.getReviewCount() > 0)
                .mapToDouble(g -> g.getAvgRating().doubleValue())
                .average().orElse(0.0);
        return Map.of(
                "totalGames",     totalGames,
                "totalDownloads", totalDownloads,
                "totalReviews",   totalReviews,
                "avgRating",      String.format("%.2f", avgRating)
        );
    }

    @GetMapping("/reviews")
    public List<Map<String, Object>> myReviews(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return reviews.findByGameOwnerIdAndRemovedFalse(user.getId()).stream()
                .map(r -> Map.<String, Object>of(
                        "id",             r.getId(),
                        "gameId",         r.getGame().getId(),
                        "gameTitle",      r.getGame().getTitle(),
                        "userEmail",      r.getUser().getEmail(),
                        "rating",         r.getRating(),
                        "body",           r.getBody(),
                        "publisherReply", r.getPublisherReply() != null ? r.getPublisherReply() : "",
                        "createdAt",      r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private String normalizeOptional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Map<String, Object> profileResponse(User user) {
        return Map.of(
                "displayName",    safe(user.getDisplayName()),
                "payoutMethod",   safe(user.getPayoutMethod()),
                "supportContact", safe(user.getSupportContact())
        );
    }
}
