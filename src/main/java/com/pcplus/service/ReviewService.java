package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.model.Review;
import com.pcplus.model.User;
import com.pcplus.repository.GameRepository;
import com.pcplus.repository.PurchaseRepository;
import com.pcplus.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final GameRepository games;
    private final PurchaseRepository purchases;

    private static final Map<String, String> AVATARS = Map.of(
            "av1", "fox",
            "av2", "bear",
            "av3", "frog",
            "av4", "tiger",
            "av5", "penguin"
    );

    public ReviewService(ReviewRepository reviews, GameRepository games, PurchaseRepository purchases) {
        this.reviews = reviews;
        this.games = games;
        this.purchases = purchases;
    }

    public List<Map<String, Object>> list(Long gameId) {
        return reviews.findByGameIdAndRemovedFalse(gameId).stream()
                .map(review -> Map.<String, Object>of(
                        "id", review.getId(),
                        "userEmail", displayNameFor(review.getUser()),
                        "userAvatar", AVATARS.getOrDefault(review.getUser().getAvatarId(), "fox"),
                        "rating", review.getRating(),
                        "body", review.getBody(),
                        "publisherReply", review.getPublisherReply() != null ? review.getPublisherReply() : "",
                        "createdAt", review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Object> submit(Long gameId, User user, int rating, String body) {
        if (!purchases.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "must_own_game");
        }

        if (reviews.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "already_reviewed");
        }

        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "game_not_found");
        }

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setRating(rating);
        review.setBody(body);
        reviews.save(review);
        updateGameRating(game);

        return Map.of("ok", true);
    }

    public Map<String, Object> publisherReply(Long gameId, Long reviewId, User user, String reply) {
        Review review = reviews.findById(reviewId).orElse(null);
        if (review == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "review_not_found");
        }

        Game game = review.getGame();
        if (!game.getId().equals(gameId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "review_game_mismatch");
        }

        if (game.getOwner() == null || !game.getOwner().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "not_owner");
        }

        review.setPublisherReply(reply);
        review.setRepliedAt(Instant.now());
        reviews.save(review);

        return Map.of("ok", true);
    }

    public Map<String, Object> removeReview(Long reviewId, User user) {
        Review review = reviews.findById(reviewId).orElse(null);
        if (review == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "review_not_found");
        }

        boolean isAdmin = "admin".equals(user.getRole());
        boolean isOwner = review.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new ApiException(HttpStatus.FORBIDDEN, "forbidden");
        }

        review.setRemoved(true);
        reviews.save(review);
        updateGameRating(review.getGame());

        return Map.of("ok", true);
    }

    private String displayNameFor(User user) {
        if (user == null) {
            return "Unknown customer";
        }

        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName.trim();
        }

        return user.getEmail();
    }

    private void updateGameRating(Game game) {
        List<Review> liveReviews = reviews.findByGameIdAndRemovedFalse(game.getId());
        int count = liveReviews.size();
        double avg = liveReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        game.setReviewCount(count);
        game.setAvgRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        games.save(game);
    }
}
