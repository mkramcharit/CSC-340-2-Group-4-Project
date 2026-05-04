package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.CartItem;
import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.CartItemRepository;
import com.pcplus.repository.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cart;
    private final GameRepository games;

    public CartService(CartItemRepository cart, GameRepository games) {
        this.cart = cart;
        this.games = games;
    }

    public List<Map<String, Object>> getCart(User user) {
        return cart.findByUserId(user.getId()).stream()
                .map(item -> {
                    Game game = item.getGame();
                    return Map.<String, Object>of(
                            "gameId", game.getId(),
                            "title", game.getTitle(),
                            "coverImage", game.getCoverImage() != null ? game.getCoverImage() : "",
                            "price", game.getPrice(),
                            "salePrice", game.getSalePrice() != null ? game.getSalePrice() : game.getPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> addToCart(User user, Long gameId) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "game_not_found");
        }

        if (cart.existsByUserIdAndGameId(user.getId(), gameId)) {
            return Map.of("ok", true, "note", "already_in_cart");
        }

        cart.save(CartItem.builder().user(user).game(game).build());
        return Map.of("ok", true);
    }

    public Map<String, Object> removeItem(User user, Long gameId) {
        cart.deleteByUserIdAndGameId(user.getId(), gameId);
        return Map.of("ok", true);
    }

    public Map<String, Object> clear(User user) {
        cart.deleteByUserId(user.getId());
        return Map.of("ok", true);
    }
}
