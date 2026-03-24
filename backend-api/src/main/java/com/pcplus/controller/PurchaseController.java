package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.PurchaseService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public List<Map<String, Object>> library(Authentication auth) {
        return purchaseService.library((User) auth.getPrincipal());
    }

    @GetMapping("/owns/{gameId}")
    public Map<String, Boolean> owns(Authentication auth, @PathVariable Long gameId) {
        return purchaseService.owns((User) auth.getPrincipal(), gameId);
    }

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(Authentication auth) {
        return ResponseEntity.ok(purchaseService.checkout((User) auth.getPrincipal()));
    }

    @PostMapping("/buy/{gameId}")
    @Transactional
    public ResponseEntity<?> buyNow(Authentication auth, @PathVariable Long gameId) {
        return ResponseEntity.ok(purchaseService.buyNow((User) auth.getPrincipal(), gameId));
    }
}
