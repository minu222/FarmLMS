package com.lms.urbangreen.lecture.quiz.controller;

import com.lms.urbangreen.lecture.quiz.entity.QuizFinalResultDto;
import com.lms.urbangreen.lecture.quiz.entity.QuizResponseDto;
import com.lms.urbangreen.lecture.quiz.entity.QuizResultDto;
import com.lms.urbangreen.lecture.quiz.entity.QuizSubmissionRequest;
import com.lms.urbangreen.lecture.quiz.service.QuizService;
import com.lms.urbangreen.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // @ResponseBody를 사용하여 REST API 기능 유지

import java.util.List;

@Controller // 뷰를 반환하기 위해 @Controller로 변경
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // --- 1. 뷰 반환 (팝업/새 탭에서 HTML 파일 로드) ---
    @GetMapping("/view")
    public String showQuizView(@RequestParam("videoId") int videoId, Model model) {
        // JavaScript에서 사용할 videoId를 Thymeleaf 템플릿에 전달
        model.addAttribute("videoId", videoId);

        // src/main/resources/templates/quiz/quiz.html 템플릿을 반환
        return "quiz/quiz";
    }

    // --- 2. REST API 엔드포인트 (기존 로직 유지) ---
    @ResponseBody // JSON 데이터를 반환함을 명시
    @GetMapping("/api/data/{videoId}")
    public ResponseEntity<List<QuizResponseDto>> getQuizData(@PathVariable int videoId) {
        return ResponseEntity.ok(quizService.getQuizList(videoId));
    }

    // URL: POST /quiz/api/submit (기존 /api/quiz/submit에서 변경)
    @ResponseBody
    @PostMapping("/api/submit")
    public ResponseEntity<QuizFinalResultDto> submitQuiz(@RequestBody QuizSubmissionRequest request, // 반환 타입 변경
                                                         HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 적절한 예외 처리 또는 HTTP 401 반환 로직을 여기에 추가해야 합니다.
            // 임시로 런타임 예외를 던지거나, 퀴즈를 제출할 수 없는 DTO를 반환할 수 있습니다.
            // 현재 요구사항에 따라 이 부분은 건드리지 않습니다.
        }
        int userId = loginUser.getUserId();

        // Service의 반환 타입 변경에 맞춰 호출
        QuizFinalResultDto result = quizService.submitQuiz(userId, request);

        return ResponseEntity.ok(result);
    }
}