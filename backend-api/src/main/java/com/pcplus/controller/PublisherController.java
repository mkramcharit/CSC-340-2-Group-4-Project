package com.pcplus.controller;

import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.service.PublisherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publisher")
@PreAuthorize("hasRole('PUBLISHER')")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("/games")
    public List<Game> myGames(Authentication auth) {
        return publisherService.myGames((User) auth.getPrincipal());
    }

    static class GameReq {
        @NotBlank
        public String title;
        public String publisher;
        public String description;

        @NotNull
        public BigDecimal price;

        public BigDecimal salePrice;
        public String genres;
        public String coverImage;
        public String screenshots;
        public String reqOs;
        public String reqCpu;
        public String reqRam;
        public String reqGpu;
        public String reqStorage;
        public String status;
        public boolean playable;
    }

    @GetMapping("/profile")
    public Map<String, Object> myProfile(Authentication auth) {
        return publisherService.myProfile((User) auth.getPrincipal());
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, Object> req) {
        return ResponseEntity.ok(publisherService.updateProfile((User) auth.getPrincipal(), req));
    }

    @PostMapping("/games")
    public ResponseEntity<Game> createGame(Authentication auth, @Valid @RequestBody GameReq req) {
        return ResponseEntity.ok(publisherService.createGame((User) auth.getPrincipal(), toCommand(req)));
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<?> updateGame(Authentication auth, @PathVariable Long id, @Valid @RequestBody GameReq req) {
        return ResponseEntity.ok(publisherService.updateGame((User) auth.getPrincipal(), id, toCommand(req)));
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<?> deleteGame(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(publisherService.deleteGame((User) auth.getPrincipal(), id));
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication auth) {
        return publisherService.dashboard((User) auth.getPrincipal());
    }

    @GetMapping("/reviews")
    public List<Map<String, Object>> myReviews(Authentication auth) {
        return publisherService.myReviews((User) auth.getPrincipal());
    }

    private PublisherService.GameCommand toCommand(GameReq req) {
        return new PublisherService.GameCommand(
                req.title,
                req.publisher,
                req.description,
                req.price,
                req.salePrice,
                req.genres,
                req.coverImage,
                req.screenshots,
                req.reqOs,
                req.reqCpu,
                req.reqRam,
                req.reqGpu,
                req.reqStorage,
                req.status,
                req.playable
        );
    }
}
