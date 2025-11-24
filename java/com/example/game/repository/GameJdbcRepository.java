package com.example.game.repository;

import com.example.game.entity.GameEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameJdbcRepository {

    private final JdbcTemplate jdbc;

    public GameJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 저장
    public void save(GameEntity game) {
        String sql = "INSERT INTO game (user_id, player_hp, game_day, growth_rate, " +
                "weather, daily_action, action_type, mini_result, action_score, game_grade) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbc.update(sql,
                game.getUserId(),
                game.getPlayerHp(),
                game.getGameDay(),
                game.getGrowthRate(),
                game.getWeather(),
                game.getDailyAction(),
                game.getActionType(),
                game.getMiniResult(),
                game.getActionScore(),
                game.getGameGrade()
        );
    }

    // 이어하기용: 가장 최근 기록 1개 조회
    public GameEntity findLatestByUserId(Integer userId) {
        String sql = "SELECT * FROM game WHERE user_id = ? ORDER BY session_id DESC LIMIT 1";

        List<GameEntity> list = jdbc.query(sql, new GameRowMapper(), userId);

        return list.isEmpty() ? null : list.get(0);
    }
}
