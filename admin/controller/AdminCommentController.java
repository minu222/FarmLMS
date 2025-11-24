package com.lms.urbangreen.urbangreenproject.admin.controller;

import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureSummaryDto;
import com.lms.urbangreen.urbangreenproject.admin.service.AdminCommentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    // 댓글관리 페이지 (템플릿)
    @GetMapping
    public String commentsPage() {
        return "admin/adminComments";   // 방금 JS 붙여둔 템플릿
    }

    // 강의 목록 + 질문 개수
    @GetMapping("/api/lectures")
    @ResponseBody
    public List<LectureSummaryDto> lectures() {
        return adminCommentService.getLectureSummaries();
    }

    // 특정 강의의 질문 + 답글
    @GetMapping("/api/lectures/{lectureId}/comments")
    @ResponseBody
    public List<LectureCommentDto> comments(@PathVariable("lectureId") Long lectureId) {
        return adminCommentService.getCommentsWithReplies(lectureId);
    }

    // 댓글에 답글 추가
    @PostMapping("/api/comments/{commentId}/replies")
    @ResponseBody
    public ResponseEntity<Void> addReply(@PathVariable("commentId") Long commentId,
                                         @RequestBody AddReplyRequest request) {
        adminCommentService.addReply(commentId, request.getContent());
        return ResponseEntity.ok().build();
    }

    // 댓글(질문) 삭제
    @DeleteMapping("/api/comments/{commentId}")
    @ResponseBody
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        adminCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 답글 삭제
    @DeleteMapping("/api/replies/{replyId}")
    @ResponseBody
    public ResponseEntity<Void> deleteReply(@PathVariable("replyId") Long replyId) {
        adminCommentService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class AddReplyRequest {
        private String content;
    }
}
