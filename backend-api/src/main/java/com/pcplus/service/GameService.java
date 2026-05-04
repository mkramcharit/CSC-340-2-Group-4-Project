package com.pcplus.service;

import com.pcplus.exception.ApiException;
import com.pcplus.model.Game;
import com.pcplus.repository.GameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository games;

    public GameService(GameRepository games) {
        this.games = games;
    }

    public List<Game> allLiveGames() {
        return games.findByStatusOrderByDownloadCountDesc("live");
    }

    public Game getGame(Long id) {
        return games.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "game_not_found"));
    }

    public List<Game> topSellers() {
        return games.findTopSellers(PageRequest.of(0, 6));
    }

    public List<Game> newest() {
        return games.findNewest(PageRequest.of(0, 6));
    }

    public List<Game> onSale() {
        return games.findOnSale();
    }

    public List<Game> search(String query) {
        if (query == null || query.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "empty_query");
        }

        return games.search(query.trim(), PageRequest.of(0, 20)).getContent();
    }
}
