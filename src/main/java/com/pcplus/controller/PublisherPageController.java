package com.pcplus.controller;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.model.User;
import com.pcplus.repository.UserRepository;
import com.pcplus.service.AuthService;
import com.pcplus.service.PublisherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/pcplus/publisher")
public class PublisherPageController {

    private static final String SESSION_EMAIL = "pcplus.publisher.email";

    private final AuthService authService;
    private final PublisherService publisherService;
    private final UserRepository userRepository;

    public PublisherPageController(AuthService authService, PublisherService publisherService, UserRepository userRepository) {
        this.authService = authService;
        this.publisherService = publisherService;
        this.userRepository = userRepository;
    }

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/pcplus/publisher/dashboard";
    }

    @GetMapping("/signup")
    public String signupPage(Model model, HttpSession session) {
        addSessionState(model, session);
        return "publisher-signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String pin,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String supportContact,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Map<String, Object> authResponse = authService.signup(email, password, "publisher", pin, displayName);
            session.setAttribute(SESSION_EMAIL, authResponse.get("email"));

            User publisher = getCurrentUser(session);
            if (publisher != null && supportContact != null && !supportContact.isBlank()) {
                publisherService.updateProfile(publisher, Map.of("supportContact", supportContact));
            }

            redirectAttributes.addFlashAttribute("successMessage", "Publisher account created successfully.");
            return "redirect:/pcplus/publisher/dashboard";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            return "redirect:/pcplus/publisher/signup";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User publisher = requirePublisher(session, redirectAttributes);
        if (publisher == null) {
            return "redirect:/pcplus/login";
        }

        addSessionState(model, session);

        try {
            model.addAttribute("stats", publisherService.dashboard(publisher));
            model.addAttribute("games", publisherService.myGames(publisher));
            model.addAttribute("salesRows", publisherService.gameSales(publisher));
            model.addAttribute("reviews", publisherService.myReviews(publisher));
        } catch (RuntimeException ex) {
            model.addAttribute("stats", Map.of(
                    "totalGames", 0,
                    "totalSales", 0,
                    "totalRevenue", "0.00",
                    "totalReviews", 0,
                    "avgRating", "0.00"
            ));
            model.addAttribute("games", Collections.emptyList());
            model.addAttribute("salesRows", Collections.emptyList());
            model.addAttribute("reviews", Collections.emptyList());
            model.addAttribute("errorMessage", "The publisher account opened, but one dashboard section had old or incomplete database data. Try adding a new game, then refresh.");
        }

        return "publisher-dashboard";
    }

    @GetMapping("/games/new")
    public String newGame(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User publisher = requirePublisher(session, redirectAttributes);
        if (publisher == null) {
            return "redirect:/pcplus/login";
        }

        addSessionState(model, session);
        model.addAttribute("game", new Game());
        model.addAttribute("formAction", "/pcplus/publisher/games");
        model.addAttribute("pageTitle", "Add New Game");
        return "publisher-game-form";
    }

    @PostMapping("/games")
    public String createGame(
            @RequestParam String title,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) String coverImage,
            @RequestParam(required = false) String screenshots,
            @RequestParam(required = false) String reqOs,
            @RequestParam(required = false) String reqCpu,
            @RequestParam(required = false) String reqRam,
            @RequestParam(required = false) String reqGpu,
            @RequestParam(required = false) String reqStorage,
            @RequestParam(required = false, defaultValue = "live") String status,
            @RequestParam(required = false, defaultValue = "false") boolean playable,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        try {
            publisherService.createGame(user, command(title, publisher, description, price, salePrice, genres, coverImage,
                    screenshots, reqOs, reqCpu, reqRam, reqGpu, reqStorage, status, playable));
            redirectAttributes.addFlashAttribute("successMessage", "Game added to the live database.");
            return "redirect:/pcplus/publisher/dashboard";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
            return "redirect:/pcplus/publisher/games/new";
        }
    }

    @GetMapping("/games/{id}/edit")
    public String editGame(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        Game game = publisherService.myGames(user).stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (game == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "That game is not owned by this publisher.");
            return "redirect:/pcplus/publisher/dashboard";
        }

        addSessionState(model, session);
        model.addAttribute("game", game);
        model.addAttribute("formAction", "/pcplus/publisher/games/" + id);
        model.addAttribute("pageTitle", "Edit Game");
        return "publisher-game-form";
    }

    @PostMapping("/games/{id}")
    public String updateGame(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) String coverImage,
            @RequestParam(required = false) String screenshots,
            @RequestParam(required = false) String reqOs,
            @RequestParam(required = false) String reqCpu,
            @RequestParam(required = false) String reqRam,
            @RequestParam(required = false) String reqGpu,
            @RequestParam(required = false) String reqStorage,
            @RequestParam(required = false, defaultValue = "live") String status,
            @RequestParam(required = false, defaultValue = "false") boolean playable,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        try {
            publisherService.updateGame(user, id, command(title, publisher, description, price, salePrice, genres, coverImage,
                    screenshots, reqOs, reqCpu, reqRam, reqGpu, reqStorage, status, playable));
            redirectAttributes.addFlashAttribute("successMessage", "Game updated in the live database.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/publisher/dashboard";
    }

    @PostMapping("/games/{id}/delete")
    public String deleteGame(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        try {
            publisherService.deleteGame(user, id);
            redirectAttributes.addFlashAttribute("successMessage", "Game removed from the customer catalog.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/publisher/dashboard";
    }

    @PostMapping("/reviews/{id}/reply")
    public String replyToReview(@PathVariable Long id, @RequestParam String reply, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        try {
            publisherService.replyToReview(user, id, reply);
            redirectAttributes.addFlashAttribute("successMessage", "Publisher reply saved.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/publisher/dashboard#reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = requirePublisher(session, redirectAttributes);
        if (user == null) {
            return "redirect:/pcplus/login";
        }

        try {
            publisherService.removeReview(user, id);
            redirectAttributes.addFlashAttribute("successMessage", "Review removed from the customer side.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", toFriendlyMessage(ex));
        }

        return "redirect:/pcplus/publisher/dashboard#reviews";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(SESSION_EMAIL);
        redirectAttributes.addFlashAttribute("successMessage", "Publisher signed out.");
        return "redirect:/pcplus/login";
    }

    private PublisherService.GameCommand command(String title, String publisher, String description, BigDecimal price,
                                                 BigDecimal salePrice, String genres, String coverImage, String screenshots,
                                                 String reqOs, String reqCpu, String reqRam, String reqGpu, String reqStorage,
                                                 String status, boolean playable) {
        return new PublisherService.GameCommand(title, publisher, description, price, salePrice, genres, coverImage,
                screenshots, reqOs, reqCpu, reqRam, reqGpu, reqStorage, status, playable);
    }

    private void addSessionState(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isPublisherLoggedIn", currentUser != null);
        model.addAttribute("currentAvatarUrl", currentUser == null ? "" : avatarUrlFor(currentUser.getAvatarId()));
        model.addAttribute("currentAvatarId", currentUser == null ? "av1" : normalizeAvatarId(currentUser.getAvatarId()));
    }

    private User getCurrentUser(HttpSession session) {
        Object emailObject = session.getAttribute(SESSION_EMAIL);
        if (emailObject == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmail(emailObject.toString());
        return userOptional.orElse(null);
    }

    private User requirePublisher(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null || !"publisher".equalsIgnoreCase(currentUser.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in with a publisher account first.");
            return null;
        }

        return currentUser;
    }

    private String avatarUrlFor(String avatarId) {
        String safeAvatarId = normalizeAvatarId(avatarId);
        return "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=" + safeAvatarId;
    }

    private String normalizeAvatarId(String avatarId) {
        if (avatarId == null || avatarId.isBlank()) {
            return "av1";
        }

        return switch (avatarId.trim()) {
            case "av1", "av2", "av3", "av4", "av5" -> avatarId.trim();
            default -> "av1";
        };
    }

    private String toFriendlyMessage(ApiException ex) {
        return switch (ex.getErrorCode()) {
            case "email_taken" -> "That email is already being used.";
            case "invalid_credentials" -> "Your email or password was incorrect.";
            case "game_not_found" -> "That game could not be found.";
            case "not_owner" -> "You can only edit games and reviews that belong to your publisher account.";
            case "review_not_found" -> "That review could not be found.";
            case "invalid_support_contact" -> "Support contact must be a valid email.";
            default -> "Something went wrong. Please try again.";
        };
    }
}