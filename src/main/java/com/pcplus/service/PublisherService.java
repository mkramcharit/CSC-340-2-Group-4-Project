package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.model.Review;
import com.pcplus.model.User;
import com.pcplus.repository.GameRepository;
import com.pcplus.repository.PurchaseRepository;
import com.pcplus.repository.ReviewRepository;
import com.pcplus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PublisherService {

    private final GameRepository games;
    private final ReviewRepository reviews;
    private final UserRepository users;
    private final PurchaseRepository purchases;

    public PublisherService(GameRepository games, ReviewRepository reviews, UserRepository users, PurchaseRepository purchases) {
        this.games = games;
        this.reviews = reviews;
        this.users = users;
        this.purchases = purchases;
    }

    public List<Game> myGames(User user) {
        return games.findByOwner(user);
    }

    public Map<String, Object> myProfile(User user) {
        return profileResponse(user);
    }

    @Transactional
    public Map<String, Object> updateProfile(User user, Map<String, Object> request) {
        if (request.containsKey("displayName")) {
            String displayName = normalizeOptional(asString(request.get("displayName")));
            if (displayName != null && displayName.length() > 100) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_display_name");
            }
            user.setDisplayName(displayName);
        }

        if (request.containsKey("payoutMethod")) {
            String payoutMethod = normalizeOptional(asString(request.get("payoutMethod")));
            if (payoutMethod != null) {
                payoutMethod = payoutMethod.toLowerCase();
                if (!"bank_transfer".equals(payoutMethod) && !"paypal".equals(payoutMethod)) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_payout_method");
                }
            }
            user.setPayoutMethod(payoutMethod);
        }

        if (request.containsKey("supportContact")) {
            String supportContact = normalizeOptional(asString(request.get("supportContact")));
            if (supportContact != null) {
                supportContact = supportContact.toLowerCase();
                if (supportContact.length() > 255 ||
                        !supportContact.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_support_contact");
                }
            }
            user.setSupportContact(supportContact);
        }

        users.save(user);
        return profileResponse(user);
    }

    @Transactional
    public Game createGame(User user, GameCommand command) {
        Game game = new Game();
        game.setOwner(user);
        game.setTitle(command.title());
        game.setPublisher(command.publisher() != null && !command.publisher().isBlank() ? command.publisher() : user.getDisplayName());
        game.setDescription(command.description());
        game.setPrice(command.price() != null ? command.price() : BigDecimal.ZERO);
        game.setSalePrice(command.salePrice());
        game.setGenres(command.genres());
        game.setCoverImage(command.coverImage());
        game.setScreenshots(command.screenshots());
        game.setReqOs(command.reqOs());
        game.setReqCpu(command.reqCpu());
        game.setReqRam(command.reqRam());
        game.setReqGpu(command.reqGpu());
        game.setReqStorage(command.reqStorage());
        game.setStatus(command.status() != null && !command.status().isBlank() ? command.status() : "live");
        game.setPlayable(command.playable());

        return games.save(game);
    }

    @Transactional
    public Game updateGame(User user, Long gameId, GameCommand command) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "game_not_found");
        }

        validateOwner(user, game);

        game.setTitle(command.title());
        game.setPublisher(command.publisher() != null && !command.publisher().isBlank() ? command.publisher() : user.getDisplayName());
        game.setDescription(command.description());
        game.setPrice(command.price() != null ? command.price() : BigDecimal.ZERO);
        game.setSalePrice(command.salePrice());
        game.setGenres(command.genres());
        game.setCoverImage(command.coverImage());
        game.setScreenshots(command.screenshots());
        game.setReqOs(command.reqOs());
        game.setReqCpu(command.reqCpu());
        game.setReqRam(command.reqRam());
        game.setReqGpu(command.reqGpu());
        game.setReqStorage(command.reqStorage());
        game.setStatus(command.status() != null && !command.status().isBlank() ? command.status() : "live");
        game.setPlayable(command.playable());

        return games.save(game);
    }

    @Transactional
    public Map<String, Object> deleteGame(User user, Long gameId) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "game_not_found");
        }

        validateOwner(user, game);
        game.setStatus("removed");
        games.save(game);
        return Map.of("ok", true);
    }

    public Map<String, Object> dashboard(User user) {
        List<Game> myGames = games.findByOwner(user);
        int totalGames = myGames.size();
        long totalSales = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Game game : myGames) {
            GameSalesTotals totals = calculateSalesForGame(game);
            totalSales += totals.count();
            totalRevenue = totalRevenue.add(totals.revenue());
        }

        int totalReviews = myGames.stream().mapToInt(this::reviewCount).sum();
        double avgRating = myGames.stream()
                .filter(game -> reviewCount(game) > 0 && game.getAvgRating() != null)
                .mapToDouble(game -> game.getAvgRating().doubleValue())
                .average()
                .orElse(0.0);

        return Map.of(
                "totalGames", totalGames,
                "totalSales", totalSales,
                "totalRevenue", money(totalRevenue),
                "totalReviews", totalReviews,
                "avgRating", String.format("%.2f", avgRating)
        );
    }

    public List<Map<String, Object>> gameSales(User user) {
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Game game : games.findByOwner(user)) {
            GameSalesTotals totals = calculateSalesForGame(game);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("gameId", game.getId());
            row.put("gameTitle", safe(game.getTitle()));
            row.put("salesCount", totals.count());
            row.put("revenue", money(totals.revenue()));
            rows.add(row);
        }

        return rows;
    }

    public List<Map<String, Object>> myReviews(User user) {
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Review review : reviews.findByGameOwnerIdAndRemovedFalse(user.getId())) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", review.getId());
            row.put("gameId", review.getGame() == null ? 0L : review.getGame().getId());
            row.put("gameTitle", review.getGame() == null ? "Unknown game" : safe(review.getGame().getTitle()));
            row.put("userEmail", displayNameFor(review.getUser()));
            row.put("rating", review.getRating());
            row.put("body", safe(review.getBody()));
            row.put("publisherReply", safe(review.getPublisherReply()));
            row.put("createdAt", review.getCreatedAt() == null ? "" : review.getCreatedAt().toString());
            rows.add(row);
        }

        return rows;
    }

    @Transactional
    public Map<String, Object> removeReview(User user, Long reviewId) {
        Review review = reviews.findById(reviewId).orElse(null);
        if (review == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "review_not_found");
        }

        validateOwner(user, review.getGame());
        review.setRemoved(true);
        reviews.save(review);
        return Map.of("ok", true);
    }

    @Transactional
    public Map<String, Object> replyToReview(User user, Long reviewId, String body) {
        Review review = reviews.findById(reviewId).orElse(null);
        if (review == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "review_not_found");
        }

        validateOwner(user, review.getGame());
        review.setPublisherReply(normalizeOptional(body));
        review.setRepliedAt(Instant.now());
        reviews.save(review);
        return Map.of("ok", true);
    }

    private void validateOwner(User user, Game game) {
        if (game.getOwner() == null || !game.getOwner().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "not_owner");
        }
    }

    private int reviewCount(Game game) {
        return game.getReviewCount();
    }

    private GameSalesTotals calculateSalesForGame(Game game) {
        long count = purchases.findAll().stream()
                .filter(purchase -> purchase.getGame() != null && purchase.getGame().getId().equals(game.getId()))
                .count();

        BigDecimal revenue = purchases.findAll().stream()
                .filter(purchase -> purchase.getGame() != null && purchase.getGame().getId().equals(game.getId()))
                .map(purchase -> purchase.getPricePaid() != null ? purchase.getPricePaid() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GameSalesTotals(count, revenue);
    }

    private String displayNameFor(User user) {
        if (user == null) {
            return "Unknown customer";
        }

        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName.trim();
        }

        return safe(user.getEmail());
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
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
                "displayName", safe(user.getDisplayName()),
                "payoutMethod", safe(user.getPayoutMethod()),
                "supportContact", safe(user.getSupportContact())
        );
    }

    private record GameSalesTotals(long count, BigDecimal revenue) {}

    public record GameCommand(
            String title,
            String publisher,
            String description,
            java.math.BigDecimal price,
            java.math.BigDecimal salePrice,
            String genres,
            String coverImage,
            String screenshots,
            String reqOs,
            String reqCpu,
            String reqRam,
            String reqGpu,
            String reqStorage,
            String status,
            boolean playable
    ) {}
}
