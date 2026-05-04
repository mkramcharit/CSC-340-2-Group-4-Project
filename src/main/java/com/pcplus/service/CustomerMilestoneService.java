package com.pcplus.service;

import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerMilestoneService {

    private final AuthService authService;
    private final GameService gameService;
    private final PurchaseService purchaseService;
    private final ReviewService reviewService;
    private final UserRepository users;

    public CustomerMilestoneService(
            AuthService authService,
            GameService gameService,
            PurchaseService purchaseService,
            ReviewService reviewService,
            UserRepository users) {
        this.authService = authService;
        this.gameService = gameService;
        this.purchaseService = purchaseService;
        this.reviewService = reviewService;
        this.users = users;
    }

    public Map<String, Object> createCustomerProfile(
            String email,
            String password,
            String pin,
            String displayName) {
        return authService.signup(email, password, "customer", pin, displayName);
    }

    public Map<String, Object> modifyCustomerProfile(
            User user,
            String displayName,
            String avatarId) {
        if (displayName != null) {
            user.setDisplayName(displayName.trim());
        }
        if (avatarId != null) {
            user.setAvatarId(avatarId.trim());
        }
        users.save(user);
        return authService.me(user);
    }

    public List<Game> viewAvailableServices() {
        return gameService.allLiveGames();
    }

    public Map<String, Object> subscribeToService(User user, Long gameId) {
        return purchaseService.buyNow(user, gameId);
    }

    public Map<String, Object> writeReviewForService(User user, Long gameId, int rating, String body) {
        return reviewService.submit(gameId, user, rating, body);
    }
}
