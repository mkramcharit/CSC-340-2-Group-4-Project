package com.pcplus.controller;

import com.pcplus.model.*;
import com.pcplus.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games/{gameId}/reviews")
public class ReviewController {

    private final ReviewRepository   reviews;
    private final GameRepository     games;
    private final PurchaseRepository purchases;

    private static final Map<String, String> AVATARS = Map.of(
            "av1","🦊","av2","🐼","av3","🐸","av4","🐯","av5","🐧");

    public ReviewController(ReviewRepository reviews, GameRepository games, PurchaseRepository purchases) {
        this.reviews   = reviews;
        this.games     = games;
        this.purchases = purchases;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable Long gameId) {
        return reviews.findByGameIdAndRemovedFalse(gameId).stream()
                .map(r -> Map.<String, Object>of(
                        "id",             r.getId(),
                        "userEmail",      r.getUser().getEmail(),
                        "userAvatar",     AVATARS.getOrDefault(r.getUser().getAvatarId(), "🦊"),
                        "rating",         r.getRating(),
                        "body",           r.getBody(),
                        "publisherReply", r.getPublisherReply() != null ? r.getPublisherReply() : "",
                        "createdAt",      r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    static class ReviewReq {
        @Min(1) @Max(5) public int rating;
        @NotBlank public String body;
    }

    @PostMapping
    public ResponseEntity<?> submit(
            @PathVariable Long gameId,
            Authentication auth,
            @Valid @RequestBody ReviewReq req) {
        User user = (User) auth.getPrincipal();
        if (!purchases.existsByUserIdAndGameId(user.getId(), gameId))
            return ResponseEntity.status(403).body(Map.of("error", "must_own_game"));
        if (reviews.existsByUserIdAndGameId(user.getId(), gameId))
            return ResponseEntity.badRequest().body(Map.of("error", "already_reviewed"));
        Game game = games.findById(gameId).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();
        reviews.save(Review.builder().user(user).game(game).rating(req.rating).body(req.body).build());
        updateGameRating(game);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    static class ReplyReq { @NotBlank public String reply; }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<?> publisherReply(
            @PathVariable Long gameId,
            @PathVariable Long reviewId,
            Authentication auth,
            @Valid @RequestBody ReplyReq req) {
        User user  = (User) auth.getPrincipal();
        Review rev = reviews.findById(reviewId).orElse(null);
        if (rev == null) return ResponseEntity.notFound().build();
        Game game = rev.getGame();
        if (!game.getId().equals(gameId)) return ResponseEntity.badRequest().build();
        if (game.getOwner() == null || !game.getOwner().getId().equals(user.getId()))
            return ResponseEntity.status(403).body(Map.of("error", "not_owner"));
        rev.setPublisherReply(req.reply);
        rev.setRepliedAt(Instant.now());
        reviews.save(rev);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> removeReview(@PathVariable Long reviewId, Authentication auth) {
        User user  = (User) auth.getPrincipal();
        Review rev = reviews.findById(reviewId).orElse(null);
        if (rev == null) return ResponseEntity.notFound().build();
        boolean isAdmin = "admin".equals(user.getRole());
        boolean isOwner = rev.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner)
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        rev.setRemoved(true);
        reviews.save(rev);
        updateGameRating(rev.getGame());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    private void updateGameRating(Game game) {
        List<Review> live = reviews.findByGameIdAndRemovedFalse(game.getId());
        int count  = live.size();
        double avg = live.stream().mapToInt(Review::getRating).average().orElse(0.0);
        game.setReviewCount(count);
        game.setAvgRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        games.save(game);
    }
}
