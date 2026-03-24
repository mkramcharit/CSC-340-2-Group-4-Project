package com.pcplus.controller;

import com.pcplus.model.Game;
import com.pcplus.repository.GameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Public game catalog endpoints – no auth required.
 *
 * GET /api/games              – all live games
 * GET /api/games/{id}         – single game detail
 * GET /api/games/top-sellers  – top 6 by downloads
 * GET /api/games/newest       – 6 newest
 * GET /api/games/on-sale      – games with salePrice set
 * GET /api/games/search?q=    – search title/genre
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository games;

    public GameController(GameRepository games) {
        this.games = games;
    }

    @GetMapping
    public List<Game> all() {
        return games.findByStatusOrderByDownloadCountDesc("live");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> one(@PathVariable Long id) {
        return games.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top-sellers")
    public List<Game> topSellers() {
        return games.findTopSellers(PageRequest.of(0, 6));
    }

    @GetMapping("/newest")
    public List<Game> newest() {
        return games.findNewest(PageRequest.of(0, 6));
    }

    @GetMapping("/on-sale")
    public List<Game> onSale() {
        return games.findOnSale();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty_query"));
        }
        var results = games.search(q.trim(), PageRequest.of(0, 20));
        return ResponseEntity.ok(results.getContent());
    }
}
