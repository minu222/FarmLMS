package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;
import java.util.List;

// 퀴즈 문제 조회용 DTO (정답은 보안상 프론트로 보내지 않거나, 필요시 포함)
@Data
public class QuizResponseDto {
    private int quiz_id;
    private String question;
    private String img_url;
    // JS 로직 유지를 위해 model_answer를 포함하지만,
    // 실제 운영시에는 서버에서만 정답을 알고 있는 것이 보안에 좋습니다.
    // 현재는 기존 JS 로직(클라이언트 채점)과 호환성을 위해 포함합니다.
    private String answer;
}

