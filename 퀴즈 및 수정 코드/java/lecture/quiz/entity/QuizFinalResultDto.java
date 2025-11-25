package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

// 기존 QuizResultDto를 확장하거나 필요한 필드를 모두 포함
@Data
@EqualsAndHashCode(callSuper = true) // 부모 필드도 비교에 포함 (선택적)
public class QuizFinalResultDto extends QuizResultDto {

    // QuizResultDto의 필드 (total_score, pass_status, message)는 이미 상속받거나 포함됨.

    private int total_count; // 전체 문제 수
    private int correct_count; // 맞춘 문제 수
    private List<IncorrectAnswerDetail> incorrect_details; // 오답 상세 목록
}