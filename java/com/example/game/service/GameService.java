package com.example.game.service;

import com.example.game.entity.GameEntity;
import com.example.game.repository.GameJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameJdbcRepository repository;

    public GameService(GameJdbcRepository repository) {
        this.repository = repository;
    }

    public void save(GameEntity entity) {
        repository.save(entity);
    }

    public GameEntity loadLatestGame(int userId) {
        return repository.findLatestByUserId(userId);
    }
}
