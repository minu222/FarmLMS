package com.lms.urbangreen.urbangreenproject.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LectureCommentReplyDto {
    private Long id;          // lecture_qna.qna_id (답글)
    private Long commentId;   // 부모 질문 qna_id
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}