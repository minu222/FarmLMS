package com.example.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "game")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "player_hp")
    private Integer playerHp;

    @Column(name = "game_day")
    private Integer gameDay;

    @Column(name = "growth_rate")
    private Double growthRate;

    @Column(name = "weather")
    private String weather;

    @Column(name = "daily_action")
    private Integer dailyAction;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "mini_result")
    private String miniResult;

    @Column(name = "action_score")
    private Double actionScore;

    @Column(name = "game_grade")
    private String gameGrade;
}
