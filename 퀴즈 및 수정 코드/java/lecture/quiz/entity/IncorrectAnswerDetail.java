package com.lms.urbangreen.lecture.quiz.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncorrectAnswerDetail {
    private String question;

    // model_answer(정답)이 DTO에서는 correctAnswer로 매핑되므로, JSON 이름은 correct_answer로 설정
    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("user_answer")
    private String userAnswer;
}