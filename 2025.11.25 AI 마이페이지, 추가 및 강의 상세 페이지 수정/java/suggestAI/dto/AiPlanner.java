package com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiPlanner {
    private Long plannerId;
    private String userId;
    private String plannerName;
    private String plannerContent;
}