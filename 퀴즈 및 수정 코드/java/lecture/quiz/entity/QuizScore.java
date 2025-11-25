package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;

@Data
public class QuizScore {

    public enum pass {
        PASS, FAIL
    }

    private int score_id;
    private int video_id;
    private int user_id;
    private int total_score;
    private pass pass;
}
