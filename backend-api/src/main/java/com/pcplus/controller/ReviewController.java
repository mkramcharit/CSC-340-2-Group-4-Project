package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games/{gameId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable Long gameId) {
        return reviewService.list(gameId);
    }

    static class ReviewReq {
        @Min(1)
        @Max(5)
        public int rating;

        @NotBlank
        public String body;
    }

    @PostMapping
    public ResponseEntity<?> submit(@PathVariable Long gameId, Authentication auth, @Valid @RequestBody ReviewReq req) {
        return ResponseEntity.ok(reviewService.submit(gameId, (User) auth.getPrincipal(), req.rating, req.body));
    }

    static class ReplyReq {
        @NotBlank
        public String reply;
    }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<?> publisherReply(
            @PathVariable Long gameId,
            @PathVariable Long reviewId,
            Authentication auth,
            @Valid @RequestBody ReplyReq req) {
        return ResponseEntity.ok(
                reviewService.publisherReply(gameId, reviewId, (User) auth.getPrincipal(), req.reply)
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> removeReview(@PathVariable Long reviewId, Authentication auth) {
        return ResponseEntity.ok(reviewService.removeReview(reviewId, (User) auth.getPrincipal()));
    }
}
