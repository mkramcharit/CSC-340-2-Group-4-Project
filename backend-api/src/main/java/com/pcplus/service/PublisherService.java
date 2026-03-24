package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.GameRepository;
import com.pcplus.repository.ReviewRepository;
import com.pcplus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private final GameRepository games;
    private final ReviewRepository reviews;
    private final UserRepository users;

    public PublisherService(GameRepository games, ReviewRepository reviews, UserRepository users) {
        this.games = games;
        this.reviews = reviews;
        this.users = users;
    }

    public List<Game> myGames(User user) {
        return games.findByOwner(user);
    }

    public Map<String, Object> myProfile(User user) {
        return profileResponse(user);
    }

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

    public Game createGame(User user, GameCommand command) {
        Game game = Game.builder()
                .owner(user)
                .title(command.title())
                .publisher(command.publisher() != null ? command.publisher() : user.getDisplayName())
                .description(command.description())
                .price(command.price())
                .salePrice(command.salePrice())
                .genres(command.genres())
                .coverImage(command.coverImage())
                .screenshots(command.screenshots())
                .reqOs(command.reqOs())
                .reqCpu(command.reqCpu())
                .reqRam(command.reqRam())
                .reqGpu(command.reqGpu())
                .reqStorage(command.reqStorage())
                .status(command.status() != null ? command.status() : "live")
                .playable(command.playable())
                .build();

        return games.save(game);
    }

    public Game updateGame(User user, Long gameId, GameCommand command) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "game_not_found");
        }

        validateOwner(user, game);

        game.setTitle(command.title());
        if (command.publisher() != null) {
            game.setPublisher(command.publisher());
        }
        game.setDescription(command.description());
        game.setPrice(command.price());
        game.setSalePrice(command.salePrice());
        game.setGenres(command.genres());
        game.setCoverImage(command.coverImage());
        game.setScreenshots(command.screenshots());
        game.setReqOs(command.reqOs());
        game.setReqCpu(command.reqCpu());
        game.setReqRam(command.reqRam());
        game.setReqGpu(command.reqGpu());
        game.setReqStorage(command.reqStorage());
        if (command.status() != null) {
            game.setStatus(command.status());
        }
        game.setPlayable(command.playable());

        return games.save(game);
    }

    public Map<String, Object> deleteGame(User user, Long gameId) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "game_not_found");
        }

        validateOwner(user, game);
        games.delete(game);
        return Map.of("ok", true);
    }

    public Map<String, Object> dashboard(User user) {
        List<Game> myGames = games.findByOwner(user);
        int totalGames = myGames.size();
        int totalDownloads = myGames.stream().mapToInt(Game::getDownloadCount).sum();
        int totalReviews = myGames.stream().mapToInt(Game::getReviewCount).sum();
        double avgRating = myGames.stream()
                .filter(game -> game.getReviewCount() > 0)
                .mapToDouble(game -> game.getAvgRating().doubleValue())
                .average()
                .orElse(0.0);

        return Map.of(
                "totalGames", totalGames,
                "totalDownloads", totalDownloads,
                "totalReviews", totalReviews,
                "avgRating", String.format("%.2f", avgRating)
        );
    }

    public List<Map<String, Object>> myReviews(User user) {
        return reviews.findByGameOwnerIdAndRemovedFalse(user.getId()).stream()
                .map(review -> Map.<String, Object>of(
                        "id", review.getId(),
                        "gameId", review.getGame().getId(),
                        "gameTitle", review.getGame().getTitle(),
                        "userEmail", review.getUser().getEmail(),
                        "rating", review.getRating(),
                        "body", review.getBody(),
                        "publisherReply", review.getPublisherReply() != null ? review.getPublisherReply() : "",
                        "createdAt", review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private void validateOwner(User user, Game game) {
        if (game.getOwner() == null || !game.getOwner().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "not_owner");
        }
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
