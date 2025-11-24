package com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;
    // 필요한 다른 필드도 추가 가능

    @Data
    public static class Candidate {
        private Content content;
        // ...
    }

    @Data
    public static class Content {
        private List<Part> parts;
        private String role;
        // ...
    }

    @Data
    public static class Part {
        private String text; // 우리가 필요한 최종 텍스트
    }
}