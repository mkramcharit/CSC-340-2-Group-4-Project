package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.CartService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<Map<String, Object>> getCart(Authentication auth) {
        return cartService.getCart((User) auth.getPrincipal());
    }

    static class AddReq {
        @NotNull
        public Long gameId;
    }

    @PostMapping
    public ResponseEntity<?> addToCart(Authentication auth, @RequestBody AddReq req) {
        return ResponseEntity.ok(cartService.addToCart((User) auth.getPrincipal(), req.gameId));
    }

    @DeleteMapping("/{gameId}")
    @Transactional
    public ResponseEntity<?> removeItem(Authentication auth, @PathVariable Long gameId) {
        return ResponseEntity.ok(cartService.removeItem((User) auth.getPrincipal(), gameId));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> clearCart(Authentication auth) {
        return ResponseEntity.ok(cartService.clear((User) auth.getPrincipal()));
    }
}
