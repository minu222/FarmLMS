package com.lms.urbangreen.lecture.quiz.entitiy;

import lombok.Data;

@Data
public class QuizAttempt {

    public enum pass {
        PASS, FAIL
    }

    private int attempt_id;
    private int user_id;
    private int quiz_id;
    private String answer_text;
    private int total_score;
    private pass pass;
}
