package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.teacher.quiz.service.TeacherQuizService;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.QuizSaveRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher/quizManage/api")
public class TeacherQuizApiController {

    private final TeacherQuizService teacherQuizService;

    public TeacherQuizApiController(TeacherQuizService quizService) {
        this.teacherQuizService = quizService;
    }

    // 출제/수정 공통 저장 엔드포인트
    @PostMapping("/video/{videoId}")
    public ResponseEntity<?> saveQuizzes(@PathVariable Long videoId,
                                         @RequestBody List<QuizSaveRequest> requests) {
        teacherQuizService.replaceQuizzes(videoId, requests);
        return ResponseEntity.ok().body("ok");
    }
}
