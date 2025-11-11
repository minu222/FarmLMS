package com.lms.urbangreen.suggestAI.dto;

import lombok.Data;

@Data
public class PlannerRequest {

    // 1. 거주 형태: residenceType
    private String residenceType;

    // 2. 공간: space
    private String space;

    // 3. 일조량: sunlight
    private String sunlight;

    // 4. 텃밭 목적: purpose
    private String purpose;

    // 5. 선호 작물: preference
    private String preference;

    // 6. 예산: budget
    private String budget;

    // 7. 경험 수준: experienceLevel
    private String experienceLevel;

}
