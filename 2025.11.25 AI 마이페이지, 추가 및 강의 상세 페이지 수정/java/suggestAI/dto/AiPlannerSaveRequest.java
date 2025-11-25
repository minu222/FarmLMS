package com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto;

import lombok.Data;

@Data
public class AiPlannerSaveRequest {
    private String plannerName;    // 사용자가 입력한 플래너 이름
    private String plannerContent; // AI가 생성한 HTML 결과
}