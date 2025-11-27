package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.service.MypageCommentsService;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsSummaryDto;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherCommentsService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher/comments/api")
@RequiredArgsConstructor
public class TeacherCommentsApiController {

    private final TeacherCommentsService teacherCommentsService;


    /**
     * [강사] 내가 올린 강의 목록 + 각 강의의 질문 개수
     * GET /teacher/comments/api/lectures
     */
    @GetMapping("/lectures")
    public ResponseEntity<List<TeacherLectureCommentsSummaryDto>> getLectures(HttpSession session) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int teacherId = loginUser.getUserId();  // user_id (int)
        List<TeacherLectureCommentsSummaryDto> result =
                teacherCommentsService.getTeacherLecturesWithComments(teacherId);

        return ResponseEntity.ok(result);
    }

    /**
     * [강사] 특정 강의(내가 올린 강의)에 달린 질문 + 답글 목록
     * GET /teacher/comments/api/lectures/{lectureId}/comments
     */
    @GetMapping("/lectures/{lectureId}/comments")
    public ResponseEntity<List<TeacherLectureCommentsDto>> getComments(
            @PathVariable("lectureId") int lectureId,
            HttpSession session
    ) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int teacherId = loginUser.getUserId();
        List<TeacherLectureCommentsDto> result =
                teacherCommentsService.getCommentsForLectureOfTeacher(lectureId, teacherId);

        return ResponseEntity.ok(result);
    }

    /**
     * [강사] 내 강의에 달린 질문에 답글 등록
     * POST /mypage/comments/api/comments/{commentId}/replies
     * Body: { "content": "답글 내용" }
     */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<Void> addReply(
            @PathVariable("commentId") int commentId,
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int teacherId = loginUser.getUserId();   // int 타입

        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        teacherCommentsService.addReplyToMyLectureComment(commentId, teacherId, content.trim());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * [강사] 내 강의에 달린 질문(댓글) 삭제
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

        int teacherId = loginUser.getUserId();
        teacherCommentsService.deleteCommentOnMyLecture(commentId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [강사] 내 강의에 달린 개별 답글 삭제
     * DELETE /mypage/comments/api/replies/{replyId}
     */
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable("replyId") int replyId,
            HttpSession session
    ) {
        User loginUser = getLoginUser(session);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int teacherId = loginUser.getUserId();
        teacherCommentsService.deleteReplyOnMyLecture(replyId, teacherId);
        return ResponseEntity.noContent().build();
    }

    // 세션에서 로그인 유저 꺼내는 공통 메서드
    private User getLoginUser(HttpSession session) {
        Object attr = session.getAttribute("loginUser");
        if (attr == null) return null;
        return (User) attr;   // 프로젝트의 User 타입에 맞게 캐스팅
    }
}