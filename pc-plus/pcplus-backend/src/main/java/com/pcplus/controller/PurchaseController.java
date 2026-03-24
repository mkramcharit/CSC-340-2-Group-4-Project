package com.pcplus.controller;

import com.pcplus.model.*;
import com.pcplus.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/library")
public class PurchaseController {

    private final PurchaseRepository purchases;
    private final CartItemRepository cart;
    private final GameRepository     games;

    public PurchaseController(PurchaseRepository purchases, CartItemRepository cart, GameRepository games) {
        this.purchases = purchases;
        this.cart      = cart;
        this.games     = games;
    }

    @GetMapping
    public List<Map<String, Object>> library(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return purchases.findByUserId(user.getId()).stream()
                .map(p -> {
                    Game g = p.getGame();
                    return Map.<String, Object>of(
                            "gameId",      g.getId(),
                            "title",       g.getTitle(),
                            "coverImage",  g.getCoverImage() != null ? g.getCoverImage() : "",
                            "pricePaid",   p.getPricePaid(),
                            "purchasedAt", p.getPurchasedAt(),
                            "playable",    g.isPlayable()
                    );
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/owns/{gameId}")
    public Map<String, Boolean> owns(Authentication auth, @PathVariable Long gameId) {
        User user = (User) auth.getPrincipal();
        return Map.of("owned", purchases.existsByUserIdAndGameId(user.getId(), gameId));
    }

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<CartItem> items = cart.findByUserId(user.getId());
        if (items.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "cart_empty"));
        for (CartItem item : items) {
            Game g = item.getGame();
            if (!purchases.existsByUserIdAndGameId(user.getId(), g.getId())) {
                BigDecimal paid = g.getSalePrice() != null ? g.getSalePrice() : g.getPrice();
                purchases.save(Purchase.builder().user(user).game(g).pricePaid(paid).build());
                g.setDownloadCount(g.getDownloadCount() + 1);
                games.save(g);
            }
        }
        cart.deleteByUserId(user.getId());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/buy/{gameId}")
    @Transactional
    public ResponseEntity<?> buyNow(Authentication auth, @PathVariable Long gameId) {
        User user = (User) auth.getPrincipal();
        Game game = games.findById(gameId).orElse(null);
        if (game == null)
            return ResponseEntity.badRequest().body(Map.of("error", "game_not_found"));
        if (purchases.existsByUserIdAndGameId(user.getId(), gameId))
            return ResponseEntity.ok(Map.of("ok", true, "note", "already_owned"));
        BigDecimal paid = game.getSalePrice() != null ? game.getSalePrice() : game.getPrice();
        purchases.save(Purchase.builder().user(user).game(game).pricePaid(paid).build());
        game.setDownloadCount(game.getDownloadCount() + 1);
        games.save(game);
        cart.deleteByUserIdAndGameId(user.getId(), gameId);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
