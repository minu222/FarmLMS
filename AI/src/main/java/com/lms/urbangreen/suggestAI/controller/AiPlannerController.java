package com.lms.urbangreen.suggestAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.urbangreen.suggestAI.dto.PlannerRequest;
import com.lms.urbangreen.suggestAI.service.AiPlannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class AiPlannerController {

    private final AiPlannerService aiPlannerService;
    private final ObjectMapper objectMapper;


    public AiPlannerController(AiPlannerService aiPlannerService, ObjectMapper objectMapper) {
        this.aiPlannerService = aiPlannerService;
        this.objectMapper = objectMapper;
    }

    // 텃밭 계획 입력 폼 페이지
    @GetMapping("/planner")
    public String plannerForm() {
        return "plannerForm"; // plannerForm.html을 렌더링
    }

    // 텃밭 계획 생성 요청 처리
    @PostMapping("/planner/generate")
    public String generatePlanner(@RequestBody PlannerRequest request, Model model) {

        // ... DTO를 Map으로 변환하는 기존 로직
        @SuppressWarnings("unchecked")
        Map<String, String> formData = objectMapper.convertValue(request, Map.class);

        // 1. AI 서비스 호출
        String aiResultMarkdown = aiPlannerService.generatePlanner(formData);

        // 2. Model에 데이터 담기 (View 렌더링에 사용됨)
        model.addAttribute("aiResult", aiResultMarkdown);
        model.addAttribute("formData", request);

        // 3. 템플릿 이름과 Fragment 셀렉터를 반환 (Spring View Resolver에게 렌더링을 위임)
        // @ResponseBody가 붙어있어도, 반환된 String은 View 이름이 아닌
        // 렌더링된 HTML Fragment 문자열로 간주되어 클라이언트에게 전달됩니다.
        return "plannerResult :: result-content";
    }
}