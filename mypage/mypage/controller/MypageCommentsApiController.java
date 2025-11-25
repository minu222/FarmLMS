package com.lms.urbangreen.urbangreenproject.mypage.controller;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.service.MypageCommentsService;
import com.lms.urbangreen.urbangreenproject.user.entity.User; // 프로젝트 실제 User 타입에 맞게 수정
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage/comments/api")
@RequiredArgsConstructor
public class MypageCommentsApiController {

    private final MypageCommentsService commentsService;

    /**
     * 로그인 유저가 댓글을 남긴 강의 목록
     * GET /mypage/comments/api/lectures
     */
    @GetMapping("/lectures")
    public ResponseEntity<List<MyLectureCommentSummaryDto>> getLectures(HttpSession session) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userId = loginUser.getUserId();   // int 타입 기준
        List<MyLectureCommentSummaryDto> result = commentsService.getMyCommentLectures(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 강의에서 내가 작성한 댓글 + 그에 대한 답글
     * GET /mypage/comments/api/lectures/{lectureId}/comments
     */
    @GetMapping("/lectures/{lectureId}/comments")
    public ResponseEntity<List<MyLectureCommentDto>> getComments(
            @PathVariable("lectureId") int lectureId,
            HttpSession session
    ) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userId = loginUser.getUserId();
        List<MyLectureCommentDto> result = commentsService.getMyCommentsForLecture(lectureId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 내 댓글 삭제 (연결된 답글도 FK CASCADE 로 함께 삭제)
     * DELETE /mypage/comments/api/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId") int commentId,
            HttpSession session
    ) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userId = loginUser.getUserId();
        commentsService.deleteMyComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // === 공통: 세션에서 로그인 유저 꺼내기 ===
    private User getLoginUser(HttpSession session) {
        Object attribute = session.getAttribute("loginUser");
        if (attribute == null) {
            return null;
        }
        return (User) attribute;  // 실제 로그인 객체 타입에 맞게 캐스팅
    }
}
