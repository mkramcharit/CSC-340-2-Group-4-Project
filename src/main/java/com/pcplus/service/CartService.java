package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.CartItem;
import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.CartItemRepository;
import com.pcplus.repository.GameRepository;
import com.pcplus.repository.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final CartItemRepository cart;
    private final GameRepository games;
    private final PurchaseRepository purchases;

    public CartService(CartItemRepository cart, GameRepository games, PurchaseRepository purchases) {
        this.cart = cart;
        this.games = games;
        this.purchases = purchases;
    }

    // this will read the current cart and keeps the database session open and running while the game data 
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCart(User user) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (CartItem item : cart.findByUserId(user.getId())) {
            Game game = item.getGame();

            if (game == null) {
                continue;
            }

            BigDecimal safePrice = game.getPrice() != null ? game.getPrice() : BigDecimal.ZERO;
            BigDecimal safeSalePrice = game.getSalePrice() != null ? game.getSalePrice() : safePrice;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("gameId", game.getId());
            row.put("title", game.getTitle() != null ? game.getTitle() : "Unknown Game");
            row.put("coverImage", game.getCoverImage() != null ? game.getCoverImage() : "");
            row.put("price", safePrice);
            row.put("salePrice", safeSalePrice);
            results.add(row);
        }

        return results;
    }

    // this adds a game to the cart but not if the user already owns it or if its already in the cart
    @Transactional
    public Map<String, Object> addToCart(User user, Long gameId) {
        Game game = games.findById(gameId).orElse(null);
        if (game == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "game_not_found");
        }

        if (purchases.existsByUserIdAndGameId(user.getId(), gameId)) {
            return Map.of("ok", true, "note", "already_owned");
        }

        if (cart.existsByUserIdAndGameId(user.getId(), gameId)) {
            return Map.of("ok", true, "note", "already_in_cart");
        }

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setGame(game);
        cart.save(cartItem);
        return Map.of("ok", true);
    }

    // this removes one game from the cart
    @Transactional
    public Map<String, Object> removeItem(User user, Long gameId) {
        cart.deleteByUserIdAndGameId(user.getId(), gameId);
        return Map.of("ok", true);
    }

    // this clears the entire cart
    @Transactional
    public Map<String, Object> clear(User user) {
        cart.deleteByUserId(user.getId());
        return Map.of("ok", true);
    }
}