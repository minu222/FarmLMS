package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;
import java.util.List;

// [Request] 퀴즈 제출 요청
@Data
public class QuizSubmissionRequest {
    private int video_id;
    private List<QuizAnswerDto> answers;

    @Data
    public static class QuizAnswerDto {
        private int quiz_id;
        private String answer_text;
    }
}

