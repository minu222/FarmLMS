package com.example.game.repository;

import com.example.game.entity.GameEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameRowMapper implements RowMapper<GameEntity> {

    @Override
    public GameEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        GameEntity game = new GameEntity();

        game.setSessionId(rs.getInt("session_id"));
        game.setUserId(rs.getInt("user_id"));
        game.setPlayerHp(rs.getInt("player_hp"));
        game.setGameDay(rs.getInt("game_day"));
        game.setGrowthRate(rs.getDouble("growth_rate"));
        game.setWeather(rs.getString("weather"));
        game.setDailyAction(rs.getInt("daily_action"));
        game.setActionType(rs.getString("action_type"));
        game.setMiniResult(rs.getString("mini_result"));
        game.setActionScore(rs.getDouble("action_score"));
        game.setGameGrade(rs.getString("game_grade"));

        return game;
    }
}

