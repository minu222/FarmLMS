package com.example.game.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameEntity {

    private Integer sessionId;
    private Integer userId;
    private Integer playerHp;
    private Integer gameDay;
    private Double growthRate;
    private String weather;
    private Integer dailyAction;
    private String actionType;
    private String miniResult;
    private Double actionScore;
    private String gameGrade;
}
