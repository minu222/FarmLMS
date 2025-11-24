package com.example.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSaveRequest {

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
