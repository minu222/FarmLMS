package com.example.game.service;

import com.example.game.dto.GameSaveRequest;
import com.example.game.entity.GameEntity;
import com.example.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    // 저장
    public void save(GameSaveRequest req) {

        GameEntity entity = new GameEntity();

        entity.setUserId(req.getUserId());
        entity.setPlayerHp(req.getPlayerHp());
        entity.setGameDay(req.getGameDay());
        entity.setGrowthRate(req.getGrowthRate());
        entity.setWeather(req.getWeather());
        entity.setDailyAction(req.getDailyAction());
        entity.setActionType(req.getActionType());
        entity.setMiniResult(req.getMiniResult());
        entity.setActionScore(req.getActionScore());
        entity.setGameGrade(req.getGameGrade());

        gameRepository.save(entity);
    }

    // 이어하기
    public GameEntity loadLatest(int userId) {
        return gameRepository.findTopByUserIdOrderBySessionIdDesc(userId);
    }
}
