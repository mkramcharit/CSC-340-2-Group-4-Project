package com.pcplus.controller;

import com.pcplus.model.User;
import com.pcplus.service.CustomerMilestoneService;
import com.pcplus.service.PurchaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerMilestoneController {

    private final CustomerMilestoneService customerService;
    private final PurchaseService purchaseService;

    public CustomerMilestoneController(CustomerMilestoneService customerService, PurchaseService purchaseService) {
        this.customerService = customerService;
        this.purchaseService = purchaseService;
    }

    static class CreateCustomerReq {
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

    @PostMapping
    public ResponseEntity<?> createCustomerProfile(@Valid @RequestBody CreateCustomerReq req) {
        return ResponseEntity.ok(
                customerService.createCustomerProfile(req.email, req.password, req.pin, req.displayName)
        );
    }

    static class UpdateCustomerReq {
        public String displayName;
        public String avatarId;
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> modifyCustomerProfile(Authentication auth, @RequestBody UpdateCustomerReq req) {
        return ResponseEntity.ok(
                customerService.modifyCustomerProfile(
                        (User) auth.getPrincipal(),
                        req.displayName,
                        req.avatarId
                )
        );
    }

    @GetMapping("/services")
    public ResponseEntity<?> viewAvailableServices() {
        return ResponseEntity.ok(customerService.viewAvailableServices());
    }

    static class SubscribeReq {
        @NotNull
        public Long gameId;
    }

    @PostMapping("/subscriptions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> subscribeToAvailableServices(Authentication auth, @Valid @RequestBody SubscribeReq req) {
        return ResponseEntity.ok(
                customerService.subscribeToService((User) auth.getPrincipal(), req.gameId)
        );
    }

    static class ReviewReq {
        @Min(1)
        @Max(5)
        public int rating;

        @NotBlank
        public String body;
    }

    @PostMapping("/services/{gameId}/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> writeReviewForSubscribedService(
            Authentication auth,
            @PathVariable Long gameId,
            @Valid @RequestBody ReviewReq req) {
        return ResponseEntity.ok(
                customerService.writeReviewForService((User) auth.getPrincipal(), gameId, req.rating, req.body)
        );
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getSubscriptions(Authentication auth) {
        return ResponseEntity.ok(purchaseService.library((User) auth.getPrincipal()));
    }
}
