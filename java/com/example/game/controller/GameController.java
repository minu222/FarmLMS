package com.example.game.controller;

import com.example.game.entity.GameEntity;
import com.example.game.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public void saveGame(@RequestBody GameEntity entity) {
        entity.setUserId(1);
        service.save(entity);
    }

    @GetMapping("/latest/{userId}")
    public GameEntity getLatestGame(@PathVariable int userId) {
        return service.loadLatestGame(userId);
    }
}
