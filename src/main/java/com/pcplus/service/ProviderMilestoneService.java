package com.pcplus.service;

import com.pcplus.model.Game;
import com.pcplus.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProviderMilestoneService {

    private final AuthService authService;
    private final PublisherService publisherService;
    private final ReviewService reviewService;

    public ProviderMilestoneService(
            AuthService authService,
            PublisherService publisherService,
            ReviewService reviewService) {
        this.authService = authService;
        this.publisherService = publisherService;
        this.reviewService = reviewService;
    }

    public Map<String, Object> createProviderProfile(
            String email,
            String password,
            String pin,
            String displayName) {
        return authService.signup(email, password, "publisher", pin, displayName);
    }

    public Map<String, Object> modifyProviderProfile(User user, Map<String, Object> request) {
        return publisherService.updateProfile(user, request);
    }

    public Game createService(User user, ProviderGameCommand command) {
        return publisherService.createGame(user, command.toPublisherCommand());
    }

    public Map<String, Object> viewCustomerStatistics(User user) {
        return publisherService.dashboard(user);
    }

    public List<Map<String, Object>> getProviderReviews(User user) {
        return publisherService.myReviews(user);
    }

    public Map<String, Object> replyToReview(User user, Long gameId, Long reviewId, String reply) {
        return reviewService.publisherReply(gameId, reviewId, user, reply);
    }

    public record ProviderGameCommand(
            String title,
            String publisher,
            String description,
            BigDecimal price,
            BigDecimal salePrice,
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
    ) {
        public PublisherService.GameCommand toPublisherCommand() {
            return new PublisherService.GameCommand(
                    title,
                    publisher,
                    description,
                    price,
                    salePrice,
                    genres,
                    coverImage,
                    screenshots,
                    reqOs,
                    reqCpu,
                    reqRam,
                    reqGpu,
                    reqStorage,
                    status,
                    playable
            );
        }
    }
}
