package com.lms.urbangreen.urbangreenproject.ai.suggestAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.AiPlanner;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.AiPlannerSaveRequest;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.PlannerRequest;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.service.AiPlannerService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AiPlannerController {

    private final AiPlannerService aiPlannerService;
    private final ObjectMapper objectMapper;

    // 텃밭 계획 입력 폼 페이지
    @GetMapping("/planner")
    public String plannerForm(Model model, HttpSession session) {
        // 1. 세션에서 로그인 사용자 객체를 가져옵니다.
        Object loginUser = session.getAttribute("loginUser");

        // 2. Thymeleaf가 사용할 수 있도록 'isLoggedIn' 플래그를 모델에 추가합니다.
        // 세션에 'loginUser' 객체가 있으면 true, 아니면 false
        model.addAttribute("isLoggedIn", loginUser != null);

        return "/ai/plannerForm";
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
        return "ai/plannerResult :: result-content";
    }

    // 텃밭 계획 저장 요청 처리
    @PostMapping("/planner/save")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<String> savePlanner(@RequestBody AiPlannerSaveRequest request, HttpSession session) {

        // 1. 세션에서 로그인 유저 확인
        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        User user = (User) loginUserObj;
        String userId = String.valueOf(user.getUserId());

        try {
            // 2. 서비스 호출하여 저장
            aiPlannerService.savePlannerResult(userId, request.getPlannerName(), request.getPlannerContent());
            return ResponseEntity.ok("성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 중 오류가 발생했습니다.");
        }
    }

    // 마이페이지 AI 플래너 화면
    @GetMapping("/mypage/aiplanners")
    public String myPlanners(Model model) {
        // 사이드바 활성화를 위해 'active' 모델 속성 추가
        model.addAttribute("active", "aiplanners");
        return "/mypage/myAiPlanners"; // 새로운 Thymeleaf 템플릿 이름
    }

    // AI 플래너 목록 제공
    @GetMapping("/mypage/aiplanners/api")
    @ResponseBody
    public ResponseEntity<?> getMyPlannersApi(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            // 비로그인 시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 유저 ID 추출 (String.valueOf() 사용)
        User user = (User) loginUserObj;
        String userId = String.valueOf(user.getUserId());

        try {
            List<AiPlanner> planners = aiPlannerService.getSavedPlanners(userId);
            return ResponseEntity.ok(planners);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("플래너 목록 조회 중 오류 발생");
        }
    }

    // 텃밭 계획 삭제 요청 처리
    @DeleteMapping("/mypage/aiplanners/{plannerId}")
    @ResponseBody
    public ResponseEntity<String> deletePlanner(@PathVariable Long plannerId, HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) loginUserObj;
        String userId = String.valueOf(user.getUserId());

        try {
            boolean deleted = aiPlannerService.deletePlanner(plannerId, userId);

            if (deleted) {
                return ResponseEntity.ok("플래너가 성공적으로 삭제되었습니다.");
            } else {
                // 삭제된 행이 0개인 경우 (플래너가 없거나, 다른 사용자의 플래너인 경우)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없거나 플래너를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("플래너 삭제 중 오류가 발생했습니다.");
        }
    }
}