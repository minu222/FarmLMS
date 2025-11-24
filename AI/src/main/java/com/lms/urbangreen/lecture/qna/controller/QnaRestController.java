package com.lms.urbangreen.lecture.qna.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lms.urbangreen.lecture.qna.entity.QnaResponseDto;
import com.lms.urbangreen.lecture.qna.service.QnaService;
import com.lms.urbangreen.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaRestController {

    private final QnaService qnaService;

    // --- 요청 DTO 분리 ---

    // 1. 질문 등록 및 수정용 DTO (pQnaId 필드 제외)
    @Data
    public static class QuestionRequest {
        private int lectureId;
        private String content;
        // userId는 세션에서 가져오므로 DTO에 포함하지 않습니다. (보안 및 데이터 정합성 강화)
    }

    // 2. 답변 등록용 DTO (pQnaId 포함)
    @Data
    public static class ReplyRequest {
        private int lectureId;

        @JsonProperty("pQnaId")
        private Integer pQnaId; // null을 받을 수 있도록 Integer로 변경

        private String content;
    }

    // 3. 수정 요청용 DTO (Content만 필요)
    @Data
    public static class UpdateRequest {
        private String content;
    }

    // ----------------------

    /**
     * GET: QnA 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<Page<QnaResponseDto>> getQnaList(
            @RequestParam int lectureId,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserId = (loginUser != null) ? loginUser.getUserId() : -1;

        Pageable pageable = PageRequest.of(page, 2);
        Page<QnaResponseDto> result = qnaService.getQnaPageByLectureId(lectureId, currentUserId, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * POST: 질문 등록 (QuestionRequest 사용)
     */
    @PostMapping("/question")
    public ResponseEntity<Void> createQuestion(@RequestBody QuestionRequest request, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // pQnaId가 없는 순수 질문 등록
        qnaService.createQuestion(request.getLectureId(), loginUser.getUserId(), request.getContent());
        return ResponseEntity.ok().build();
    }

    /**
     * POST: 답변 등록 (강사 전용, ReplyRequest 사용)
     */
    @PostMapping("/reply")
    public ResponseEntity<String> createReply(@RequestBody ReplyRequest request, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 강사 권한 체크 (TEACHER)
        boolean isInstructor = loginUser.getUserType() != null && "TEACHER".equalsIgnoreCase(String.valueOf(loginUser.getUserType()));

        if (!isInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "강사만 답변을 등록할 수 있습니다.");
        }

        // ReplyRequest의 pQnaId (부모 ID) 사용
        qnaService.createReply(request.getLectureId(), loginUser.getUserId(), request.getPQnaId(), request.getContent());
        return ResponseEntity.ok("{}");
    }

    /**
     * PUT: 질문/답변 수정
     */
    @PutMapping("/{qnaId}")
    public ResponseEntity<Void> updateQna(@PathVariable int qnaId, @RequestBody UpdateRequest request, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            qnaService.updateQna(qnaId, loginUser.getUserId(), request.getContent());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * DELETE: 질문/답변 삭제
     */
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> deleteQna(@PathVariable int qnaId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            qnaService.deleteQna(qnaId, loginUser.getUserId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}