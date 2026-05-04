package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.ProviderMilestoneService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/providers")
public class ProviderMilestoneController {

    private final ProviderMilestoneService providerService;

    public ProviderMilestoneController(ProviderMilestoneService providerService) {
        this.providerService = providerService;
    }

    static class CreateProviderReq {
        @Email
        @NotBlank
        public String email;

        @NotBlank
        public String password;

        @NotBlank
        @Pattern(regexp = "\\d{4}")
        public String pin;

        public String displayName;
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProviderProfile(@Valid @RequestBody CreateProviderReq req) {
        return ResponseEntity.ok(
                providerService.createProviderProfile(req.email, req.password, req.pin, req.displayName)
        );
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<?> modifyProviderProfile(Authentication auth, @RequestBody Map<String, Object> req) {
        return ResponseEntity.ok(providerService.modifyProviderProfile((User) auth.getPrincipal(), req));
    }

    static class CreateServiceReq {
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

    @PostMapping("/services")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<?> createServices(Authentication auth, @Valid @RequestBody CreateServiceReq req) {
        var cmd = new ProviderMilestoneService.ProviderGameCommand(
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
        return ResponseEntity.ok(providerService.createService((User) auth.getPrincipal(), cmd));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<?> viewCustomerStatistics(Authentication auth) {
        return ResponseEntity.ok(providerService.viewCustomerStatistics((User) auth.getPrincipal()));
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<?> getReviews(Authentication auth) {
        return ResponseEntity.ok(providerService.getProviderReviews((User) auth.getPrincipal()));
    }

    static class ReplyReq {
        @NotNull
        public Long gameId;

        @NotBlank
        public String reply;
    }

    @PostMapping("/reviews/{reviewId}/reply")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<?> replyToReview(
            Authentication auth,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReplyReq req) {
        return ResponseEntity.ok(
                providerService.replyToReview((User) auth.getPrincipal(), req.gameId, reviewId, req.reply)
        );
    }
}
