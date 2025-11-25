package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizAttempt {
    private int attempt_id;
    private int user_id;
    private int quiz_id;
    private String answer_text;
    private int earned_score;
    private LocalDateTime attempted_at;
}
