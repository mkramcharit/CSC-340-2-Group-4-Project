package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.CartItem;
import com.pcplus.model.Game;
import com.pcplus.model.Purchase;
import com.pcplus.model.User;
import com.pcplus.repository.CartItemRepository;
import com.pcplus.repository.GameRepository;
import com.pcplus.repository.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchases;
    private final CartItemRepository cart;
    private final GameRepository games;

    public PurchaseService(PurchaseRepository purchases, CartItemRepository cart, GameRepository games) {
        this.purchases = purchases;
        this.cart = cart;
        this.games = games;
    }

    public List<Map<String, Object>> library(User user) {
        return purchases.findByUserId(user.getId()).stream()
                .map(purchase -> {
                    Game game = purchase.getGame();
                    return Map.<String, Object>of(
                            "gameId", game.getId(),
                            "title", game.getTitle(),
                            "coverImage", game.getCoverImage() != null ? game.getCoverImage() : "",
                            "pricePaid", purchase.getPricePaid(),
                            "purchasedAt", purchase.getPurchasedAt(),
                            "playable", game.isPlayable()
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<String, Boolean> owns(User user, Long gameId) {
        return Map.of("owned", purchases.existsByUserIdAndGameId(user.getId(), gameId));
    }

    public Map<String, Object> checkout(User user) {
        List<CartItem> items = cart.findByUserId(user.getId());
        if (items.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "cart_empty");
        }

        for (CartItem item : items) {
            Game game = item.getGame();
            if (!purchases.existsByUserIdAndGameId(user.getId(), game.getId())) {
                BigDecimal paid = game.getSalePrice() != null ? game.getSalePrice() : game.getPrice();
                purchases.save(Purchase.builder().user(user).game(game).pricePaid(paid).build());
                game.setDownloadCount(game.getDownloadCount() + 1);
                games.save(game);
            }
        }

        cart.deleteByUserId(user.getId());
        return Map.of("ok", true);
    }

    public Map<String, Object> buyNow(User user, Long gameId) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "game_not_found");
        }

        if (purchases.existsByUserIdAndGameId(user.getId(), gameId)) {
            return Map.of("ok", true, "note", "already_owned");
        }

        BigDecimal paid = game.getSalePrice() != null ? game.getSalePrice() : game.getPrice();
        purchases.save(Purchase.builder().user(user).game(game).pricePaid(paid).build());
        game.setDownloadCount(game.getDownloadCount() + 1);
        games.save(game);
        cart.deleteByUserIdAndGameId(user.getId(), gameId);

        return Map.of("ok", true);
    }
}
