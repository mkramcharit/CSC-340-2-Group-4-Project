package com.pcplus.controller;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import com.pcplus.service.AuthService;
import com.pcplus.service.CartService;
import com.pcplus.service.CustomerMilestoneService;
import com.pcplus.service.GameService;
import com.pcplus.service.PurchaseService;
import com.pcplus.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/pcplus")
public class CustomerPageController {

    // this key stores the signed in customer email inside the session
    private static final String SESSION_EMAIL = "pcplus.customer.email";

    // this key stores the signed in publisher email inside the session
    private static final String PUBLISHER_SESSION_EMAIL = "pcplus.publisher.email";

    private final CustomerMilestoneService customerMilestoneService;
    private final GameService gameService;
    private final PurchaseService purchaseService;
    private final ReviewService reviewService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CartService cartService;

    public CustomerPageController(
            CustomerMilestoneService customerMilestoneService,
            GameService gameService,
            PurchaseService purchaseService,
            ReviewService reviewService,
            AuthService authService,
            UserRepository userRepository,
            CartService cartService) {
        this.customerMilestoneService = customerMilestoneService;
        this.gameService = gameService;
        this.purchaseService = purchaseService;
        this.reviewService = reviewService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    // this keeps the project open on the customer catalog page
    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/pcplus/catalog";
    }

    // this page shows the create account form
    @GetMapping("/signup")
    public String signupPage(Model model, HttpSession session) {
        addSessionState(model, session);
        return "customer-signup";
    }

    // this form creates a customer or publisher account and signs them in
    @PostMapping("/signup")
    public String signup(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String pin,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false, defaultValue = "customer") String accountType,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if ("publisher".equalsIgnoreCase(accountType)) {
                Map<String, Object> authResponse = authService.signup(email, password, "publisher", pin, displayName);
                session.removeAttribute(SESSION_EMAIL);
                session.setAttribute(PUBLISHER_SESSION_EMAIL, authResponse.get("email"));
                redirectAttributes.addFlashAttribute("successMessage", "Publisher account created successfully.");
                return "redirect:/pcplus/publisher/dashboard";
            }

            Map<String, Object> authResponse = customerMilestoneService.createCustomerProfile(
                    email,
                    password,
                    pin,
                    displayName
            );

            session.removeAttribute(PUBLISHER_SESSION_EMAIL);
            session.setAttribute(SESSION_EMAIL, authResponse.get("email"));
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully.");
            return "redirect:/pcplus/catalog";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            return "redirect:/pcplus/signup";
        }
    }

    // this page shows the login form
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        addSessionState(model, session);
        return "customer-login";
    }

    // this form signs in an existing account and makes the dropdown matter
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "customer") String accountType,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Map<String, Object> authResponse = authService.login(email, password);
            Object roleObject = authResponse.get("role");
            String role = roleObject == null ? "" : roleObject.toString();

            if ("publisher".equalsIgnoreCase(accountType) && !"publisher".equalsIgnoreCase(role)) {
                redirectAttributes.addFlashAttribute("errorMessage", "That email belongs to a customer account, not a publisher account.");
                return "redirect:/pcplus/login";
            }

            if ("customer".equalsIgnoreCase(accountType) && !"customer".equalsIgnoreCase(role)) {
                redirectAttributes.addFlashAttribute("errorMessage", "That email belongs to a publisher account, not a customer account.");
                return "redirect:/pcplus/login";
            }

            if ("publisher".equalsIgnoreCase(role)) {
                session.removeAttribute(SESSION_EMAIL);
                session.setAttribute(PUBLISHER_SESSION_EMAIL, authResponse.get("email"));
                redirectAttributes.addFlashAttribute("successMessage", "Signed in as publisher.");
                return "redirect:/pcplus/publisher/dashboard";
            }

            if (!"customer".equalsIgnoreCase(role)) {
                redirectAttributes.addFlashAttribute("errorMessage", "This account type cannot use the customer storefront.");
                return "redirect:/pcplus/login";
            }

            session.removeAttribute(PUBLISHER_SESSION_EMAIL);
            session.setAttribute(SESSION_EMAIL, authResponse.get("email"));
            redirectAttributes.addFlashAttribute("successMessage", "Signed in successfully.");
            return "redirect:/pcplus/catalog";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            return "redirect:/pcplus/login";
        }
    }


    // this page lets a customer or publisher reset a forgotten password with their pin
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model, HttpSession session) {
        addSessionState(model, session);
        return "customer-forgot-password";
    }

    // this form resets the password for either account type using the saved account pin
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            @RequestParam String pin,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @RequestParam(required = false, defaultValue = "customer") String accountType,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "The new passwords do not match.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("accountType", accountType);
            return "redirect:/pcplus/forgot-password";
        }

        Optional<User> userOptional = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if ("publisher".equalsIgnoreCase(accountType) && !"publisher".equalsIgnoreCase(user.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "That email belongs to a customer account, not a publisher account.");
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("accountType", accountType);
                return "redirect:/pcplus/forgot-password";
            }

            if ("customer".equalsIgnoreCase(accountType) && !"customer".equalsIgnoreCase(user.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "That email belongs to a publisher account, not a customer account.");
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("accountType", accountType);
                return "redirect:/pcplus/forgot-password";
            }
        }

        try {
            authService.resetPassword(email, pin, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully. You can sign in with your new password.");
            return "redirect:/pcplus/login";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("accountType", accountType);
            return "redirect:/pcplus/forgot-password";
        }
    }

    // this route signs the current user out
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(SESSION_EMAIL);
        session.removeAttribute(PUBLISHER_SESSION_EMAIL);
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out.");
        return "redirect:/pcplus/login";
    }

    // this page shows the original style home catalog using live database data
    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String q,
            Model model,
            HttpSession session) {

        List<Game> games = (q == null || q.isBlank())
                ? customerMilestoneService.viewAvailableServices()
                : gameService.search(q);

        List<Game> topSellers = gameService.topSellers();
        List<Game> newest = gameService.newest();
        List<Game> onSale = gameService.onSale();

        Game featuredGame = !topSellers.isEmpty()
                ? topSellers.get(0)
                : (!games.isEmpty() ? games.get(0) : null);

        addSessionState(model, session);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("games", games);
        model.addAttribute("topSellers", topSellers);
        model.addAttribute("newest", newest);
        model.addAttribute("onSale", onSale);
        model.addAttribute("featuredGame", featuredGame);
        model.addAttribute("featuredImage", featuredGame == null ? defaultImage() : resolveHeroImage(featuredGame));
        return "customer-catalog";
    }

    // this page shows a single game in the original pc plus details style
    @GetMapping("/games/{id}")
    public String gameDetails(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            Model model,
            HttpSession session) {
        Game game = gameService.getGame(id);
        User currentUser = getCurrentCustomer(session);

        boolean owned = currentUser != null && Boolean.TRUE.equals(purchaseService.owns(currentUser, id).get("owned"));
        boolean inCart = currentUser != null && isGameInCart(currentUser, id);
        boolean fromLibrary = currentUser != null && owned && "library".equalsIgnoreCase(from);

        addSessionState(model, session);
        model.addAttribute("game", game);
        model.addAttribute("owned", owned);
        model.addAttribute("inCart", inCart);
        model.addAttribute("reviews", reviewService.list(id));
        model.addAttribute("heroImage", resolveHeroImage(game));
        model.addAttribute("galleryImages", buildGalleryImages(game));
        model.addAttribute("from", fromLibrary ? "library" : "catalog");
        model.addAttribute("backTarget", fromLibrary ? "/pcplus/library" : "/pcplus/catalog");
        model.addAttribute("backLabel", fromLibrary ? "Back to Library" : "Back to Catalog");
        return "customer-game-details";
    }

    // this page shows the current customer cart using live cart item data
    @GetMapping("/cart")
    public String cart(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        List<Map<String, Object>> cartItems = cartService.getCart(currentUser);

        addSessionState(model, session);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", calculateCartTotal(cartItems));
        return "customer-cart";
    }

    // this form adds a game to the customer cart
    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        try {
            Map<String, Object> result = cartService.addToCart(currentUser, id);
            Object note = result.get("note");

            if (note != null && "already_owned".equals(note.toString())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You already own this game.");
            } else if (note != null && "already_in_cart".equals(note.toString())) {
                redirectAttributes.addFlashAttribute("successMessage", "That game is already in your cart.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Game added to cart.");
            }
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "The cart action failed. Please try again.");
        }

        return "redirect:/pcplus/games/" + id;
    }

    // this form removes one game from the customer cart
    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        try {
            cartService.removeItem(currentUser, id);
            redirectAttributes.addFlashAttribute("successMessage", "Game removed from cart.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "The remove action failed. Please try again.");
        }

        return "redirect:/pcplus/cart";
    }

    // this form completes checkout for all items currently in the cart
    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        try {
            purchaseService.checkout(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Checkout complete. Your games are now in your library.");
            return "redirect:/pcplus/library";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            return "redirect:/pcplus/cart";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Checkout failed. Please try again.");
            return "redirect:/pcplus/cart";
        }
    }

    // this form completes the direct purchase flow if you still choose to use it
    @PostMapping("/games/{id}/buy")
    public String buyGame(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        try {
            purchaseService.buyNow(currentUser, id);
            redirectAttributes.addFlashAttribute("successMessage", "Purchase complete. The game is now in your library.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/games/" + id;
    }

    // this form creates a review for a game the customer owns
    @PostMapping("/games/{id}/reviews")
    public String createReview(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam String body,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        try {
            customerMilestoneService.writeReviewForService(currentUser, id, rating, body);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/games/" + id;
    }

    // this page shows the current customer or publisher profile
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in first.");
            return "redirect:/pcplus/login";
        }

        addSessionState(model, session);
        model.addAttribute("profile", currentUser);
        model.addAttribute("avatarChoices", buildAvatarChoices());
        model.addAttribute("profileAvatarUrl", avatarUrlFor(currentUser.getAvatarId()));
        return "customer-profile";
    }

    // this form updates the current customer or publisher profile
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String avatarId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in first.");
            return "redirect:/pcplus/login";
        }

        customerMilestoneService.modifyCustomerProfile(currentUser, displayName, avatarId);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/pcplus/profile";
    }

    // this page shows purchased games using the original library style
    @GetMapping("/library")
    public String library(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = requireCurrentCustomer(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/pcplus/login";
        }

        addSessionState(model, session);
        model.addAttribute("libraryGames", purchaseService.library(currentUser));
        return "customer-library";
    }

    // this helper adds the current login state and cart count to every page
    private void addSessionState(Model model, HttpSession session) {
        User currentCustomer = getCurrentCustomer(session);
        User currentPublisher = getCurrentPublisher(session);
        User currentUser = currentCustomer != null ? currentCustomer : currentPublisher;

        int cartCount = currentCustomer == null ? 0 : cartService.getCart(currentCustomer).size();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPublisher", currentPublisher);
        model.addAttribute("isLoggedIn", currentUser != null);
        model.addAttribute("isPublisherLoggedIn", currentPublisher != null);
        model.addAttribute("isCustomerLoggedIn", currentCustomer != null);
        model.addAttribute("currentAvatarUrl", currentUser == null ? "" : avatarUrlFor(currentUser.getAvatarId()));
        model.addAttribute("currentAvatarId", currentUser == null ? "av1" : normalizeAvatarId(currentUser.getAvatarId()));
        model.addAttribute("currentCartCount", cartCount);
    }

    // this helper fetches whichever user is currently signed in
    private User getCurrentUser(HttpSession session) {
        User currentCustomer = getCurrentCustomer(session);
        if (currentCustomer != null) {
            return currentCustomer;
        }

        return getCurrentPublisher(session);
    }

    // this helper fetches the current customer from the saved session email
    private User getCurrentCustomer(HttpSession session) {
        Object emailObject = session.getAttribute(SESSION_EMAIL);
        if (emailObject == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmail(emailObject.toString());
        return userOptional.orElse(null);
    }

    // this helper fetches the current publisher from the saved session email
    private User getCurrentPublisher(HttpSession session) {
        Object emailObject = session.getAttribute(PUBLISHER_SESSION_EMAIL);
        if (emailObject == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmail(emailObject.toString());
        return userOptional.orElse(null);
    }

    // this helper protects customer only routes
    private User requireCurrentCustomer(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentCustomer(session);
        if (currentUser == null) {
            if (getCurrentPublisher(session) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Publisher accounts can browse the catalog, but cannot purchase games or use customer-only pages.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Please sign in first.");
            }
        }
        return currentUser;
    }

    // this helper picks a safe hero image for each game
    private String resolveHeroImage(Game game) {
        if (game == null) {
            return defaultImage();
        }

        if (game.getCoverImage() != null && !game.getCoverImage().isBlank()) {
            return game.getCoverImage().trim();
        }

        List<String> galleryImages = buildGalleryImages(game);
        return galleryImages.isEmpty() ? defaultImage() : galleryImages.get(0);
    }

    // this helper turns the screenshots field into a clean list of images
    private List<String> buildGalleryImages(Game game) {
        List<String> images = new ArrayList<>();

        if (game == null) {
            images.add(defaultImage());
            return images;
        }

        if (game.getCoverImage() != null && !game.getCoverImage().isBlank()) {
            images.add(game.getCoverImage().trim());
        }

        if (game.getScreenshots() != null && !game.getScreenshots().isBlank()) {
            String[] parts = game.getScreenshots().split(",");
            for (String part : parts) {
                String cleaned = part == null ? "" : part.trim();
                if (!cleaned.isBlank() && !images.contains(cleaned)) {
                    images.add(cleaned);
                }
            }
        }

        if (images.isEmpty()) {
            images.add(defaultImage());
        }

        return images;
    }

    // this helper checks whether a game is already inside the customer cart
    private boolean isGameInCart(User user, Long gameId) {
        List<Map<String, Object>> cartItems = cartService.getCart(user);

        for (Map<String, Object> item : cartItems) {
            Object itemGameId = item.get("gameId");
            if (itemGameId != null && Long.valueOf(itemGameId.toString()).equals(gameId)) {
                return true;
            }
        }

        return false;
    }

    // this helper calculates the cart total using sale price when available
    private String calculateCartTotal(List<Map<String, Object>> cartItems) {
        double total = 0.0;

        for (Map<String, Object> item : cartItems) {
            Object salePrice = item.get("salePrice");
            if (salePrice != null) {
                total += Double.parseDouble(salePrice.toString());
            }
        }

        return String.format("%.2f", total);
    }

    // this helper builds the five visual avatar choices used by the profile page
    private List<Map<String, String>> buildAvatarChoices() {
        List<Map<String, String>> avatarChoices = new ArrayList<>();

        for (int index = 1; index <= 5; index++) {
            String avatarId = "av" + index;
            Map<String, String> avatar = new LinkedHashMap<>();
            avatar.put("id", avatarId);
            avatar.put("name", "Avatar " + index);
            avatar.put("imageUrl", avatarUrlFor(avatarId));
            avatarChoices.add(avatar);
        }

        return avatarChoices;
    }

    // this helper returns a stable image for each avatar id
    private String avatarUrlFor(String avatarId) {
        String safeAvatarId = normalizeAvatarId(avatarId);
        return "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=" + safeAvatarId;
    }

    // this helper prevents blank or invalid avatar ids from breaking the UI
    private String normalizeAvatarId(String avatarId) {
        if (avatarId == null || avatarId.isBlank()) {
            return "av1";
        }

        return switch (avatarId.trim()) {
            case "av1", "av2", "av3", "av4", "av5" -> avatarId.trim();
            default -> "av1";
        };
    }

    // this helper provides a fallback image when a game has no artwork yet
    private String defaultImage() {
        return "https://placehold.co/900x1200/0b1324/e5edf7?text=PC%2B";
    }

    // this helper turns backend codes into user friendly page messages
    private String toFriendlyMessage(ApiException ex) {
        return switch (ex.getErrorCode()) {
            case "email_taken" -> "That email is already being used.";
            case "invalid_credentials" -> "Your email or password was incorrect.";
            case "must_own_game" -> "You need to own the game before writing a review.";
            case "already_reviewed" -> "You already reviewed this game.";
            case "game_not_found" -> "That game could not be found.";
            case "account_inactive" -> "This account is inactive.";
            case "account_not_found" -> "No account was found for that email.";
            case "pin_mismatch" -> "That reset pin was incorrect.";
            case "cart_empty" -> "Your cart is empty.";
            default -> "Something went wrong. Please try again.";
        };
    }
}