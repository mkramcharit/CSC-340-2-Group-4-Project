package com.pcplus.controller;

import com.pcplus.model.Game;
import com.pcplus.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> all() {
        return gameService.allLiveGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> one(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @GetMapping("/top-sellers")
    public List<Game> topSellers() {
        return gameService.topSellers();
    }

    @GetMapping("/newest")
    public List<Game> newest() {
        return gameService.newest();
    }

    @GetMapping("/on-sale")
    public List<Game> onSale() {
        return gameService.onSale();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        return ResponseEntity.ok(gameService.search(q));
    }
}
