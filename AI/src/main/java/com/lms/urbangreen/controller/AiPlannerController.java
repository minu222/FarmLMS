package com.lms.urbangreen.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.urbangreen.dto.PlannerRequest;
import com.lms.urbangreen.service.AiPlannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class AiPlannerController {

    private final AiPlannerService aiPlannerService;

    public AiPlannerController(AiPlannerService aiPlannerService) {
        this.aiPlannerService = aiPlannerService;
    }

    // 텃밭 계획 입력 폼 페이지
    @GetMapping("/planner")
    public String plannerForm() {
        return "plannerForm"; // plannerForm.html을 렌더링
    }

    // 텃밭 계획 생성 요청 처리
    @PostMapping("/planner/generate")
    public String generatePlanner(PlannerRequest request, Model model) {
        // DTO를 Map으로 변환하는 로직을 Service 내에 두거나,
        // Map 대신 DTO를 Service로 전달하도록 Service 메서드를 수정해야 합니다.
        // Map<String, String> formData = ... (request 객체에서 추출)

        // (현재는 Service가 Map을 받도록 되어있으므로, 변환 로직이 필요합니다.)
        Map<String, String> formData = new ObjectMapper().convertValue(request, new TypeReference<Map<String, String>>() {
        });

        String aiResultMarkdown = aiPlannerService.generatePlanner(formData);
        model.addAttribute("aiResult", aiResultMarkdown);
        model.addAttribute("formData", request); // DTO 자체를 넘겨도 됨

        return "plannerResult";
    }
}