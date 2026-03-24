package com.pcplus.controller;

import com.pcplus.model.*;
import com.pcplus.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemRepository cart;
    private final GameRepository     games;

    public CartController(CartItemRepository cart, GameRepository games) {
        this.cart  = cart;
        this.games = games;
    }

    @GetMapping
    public List<Map<String, Object>> getCart(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return cart.findByUserId(user.getId()).stream()
                .map(item -> {
                    Game g = item.getGame();
                    return Map.<String, Object>of(
                            "gameId",     g.getId(),
                            "title",      g.getTitle(),
                            "coverImage", g.getCoverImage() != null ? g.getCoverImage() : "",
                            "price",      g.getPrice(),
                            "salePrice",  g.getSalePrice() != null ? g.getSalePrice() : g.getPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    static class AddReq { @NotNull public Long gameId; }

    @PostMapping
    public ResponseEntity<?> addToCart(Authentication auth, @RequestBody AddReq req) {
        User user = (User) auth.getPrincipal();
        Game game = games.findById(req.gameId).orElse(null);
        if (game == null)
            return ResponseEntity.badRequest().body(Map.of("error", "game_not_found"));
        if (cart.existsByUserIdAndGameId(user.getId(), req.gameId))
            return ResponseEntity.ok(Map.of("ok", true, "note", "already_in_cart"));
        cart.save(CartItem.builder().user(user).game(game).build());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping("/{gameId}")
    @Transactional
    public ResponseEntity<?> removeItem(Authentication auth, @PathVariable Long gameId) {
        User user = (User) auth.getPrincipal();
        cart.deleteByUserIdAndGameId(user.getId(), gameId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> clearCart(Authentication auth) {
        User user = (User) auth.getPrincipal();
        cart.deleteByUserId(user.getId());
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
