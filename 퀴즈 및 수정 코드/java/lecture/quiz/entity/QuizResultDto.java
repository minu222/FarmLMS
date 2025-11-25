package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;

// [Response] 퀴즈 제출 결과 (사용자에게 보여줄 정보)
@Data
public class QuizResultDto {
    private int total_score;
    private String pass_status; // "PASS" or "FAIL"
    private String message;
}
