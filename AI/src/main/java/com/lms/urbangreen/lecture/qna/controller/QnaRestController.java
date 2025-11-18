package com.lms.urbangreen.lecture.qna.controller;

import com.lms.urbangreen.lecture.entity.LectureDetailResponseDto;
import com.lms.urbangreen.lecture.qna.entity.QnaResponseDto;
import com.lms.urbangreen.lecture.qna.service.QnaService;
import com.lms.urbangreen.lecture.service.LectureService;
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
import com.lms.urbangreen.user.entity.User;

import java.util.Optional;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaRestController {

    private final QnaService qnaService;
    private final LectureService lectureService; // 강사 ID 확인을 위해 사용

    @Data
    public static class LoginUserDto {
        private int userId;
        private String nickname;
        private String role; // 강사(INSTRUCTOR) 여부를 확인할 때 사용
    }

    // 요청 DTO (간결화를 위해 내부 클래스로 정의)
    @Data
    public static class QnaContentDto {
        private int lectureId;
        private String content;
    }

    @Data
    public static class QnaReplyDto {
        private int lectureId;
        private int pQnaId; // 질문 ID
        private String content;
    }

    /**
     * GET: QnA 목록 조회 (페이징 포함)
     * URL: /api/qna/list?lectureId=1&page=0
     */
    @GetMapping("/list")
    public ResponseEntity<Page<QnaResponseDto>> getQnaList(
            @RequestParam int lectureId,
            @RequestParam(defaultValue = "0") int page) {

        // 현재 로그인된 사용자 ID를 시큐리티 등에서 가져와야 하지만, 임시로 하드코딩 (currentUserId = 1로 가정)
        int currentUserId = 1;

        // 강사 ID 확인 (Optional)
        Optional<LectureDetailResponseDto> lectureDtoOpt = lectureService.getLectureDetailDtoById(lectureId);
        String instructorId = lectureDtoOpt.map(LectureDetailResponseDto::getInstructorNickname).orElse(null);

        Pageable pageable = PageRequest.of(page, 2); // 2개씩 페이징 (HTML의 ITEMS_PER_PAGE와 일치)
        Page<QnaResponseDto> qnaPage = qnaService.getQnaPageByLectureId(lectureId, currentUserId, instructorId, pageable);

        return ResponseEntity.ok(qnaPage);
    }

    /**
     * POST: 질문 등록
     * URL: /api/qna/question
     */
    @PostMapping("/question")
    public ResponseEntity<QnaResponseDto> createQuestion(@RequestBody QnaContentDto dto, HttpSession session) {

        // 1. 세션에서 로그인 사용자 정보(loginUser)를 가져옵니다.
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 여부 확인 및 userId 추출
        if (loginUser == null || loginUser.getUserId() <= 0) {
            // 로그인 상태가 아니면 401 Unauthorized 에러 반환 (또는 403 Forbidden)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        int userId = loginUser.getUserId(); // 세션에서 안전하게 userId를 가져옵니다.

        QnaResponseDto savedDto = qnaService.createQuestion(dto.getLectureId(), userId, dto.getContent());
        return ResponseEntity.ok(savedDto);
    }

    /**
     * POST: 답변 등록
     * URL: /api/qna/reply
     */
    @PostMapping("/reply")
    public ResponseEntity<QnaResponseDto> createReply(@RequestBody QnaReplyDto dto, HttpSession session) {

        // 1. 세션에서 로그인 사용자 정보(loginUser)를 가져옵니다.
        LoginUserDto loginUser = (LoginUserDto) session.getAttribute("loginUser");

        // 2. 로그인 여부 및 강사 권한 확인
        if (loginUser == null || loginUser.getUserId() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 강사 권한 확인
        if (!"INSTRUCTOR".equals(loginUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "답변 권한이 없습니다. (강사만 가능)");
        }

        int instructorId = loginUser.getUserId(); // 세션에서 안전하게 강사 ID를 가져옵니다.

        QnaResponseDto savedDto = qnaService.createReply(dto.getLectureId(), instructorId, dto.getPQnaId(), dto.getContent());
        return ResponseEntity.ok(savedDto);
    }
}