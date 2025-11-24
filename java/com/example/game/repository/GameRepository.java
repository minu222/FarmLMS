package com.example.game.repository;

import com.example.game.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Integer> {

    // 가장 최근 게임 기록 (이어하기용)
    GameEntity findTopByUserIdOrderBySessionIdDesc(Integer userId);
}
