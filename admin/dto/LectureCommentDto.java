package com.lms.urbangreen.urbangreenproject.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LectureCommentDto {
    private Long id;          // 질문 qna_id
    private Long lectureId;   // lecture_id
    private String authorName;
    private String content;
    private LocalDateTime createdAt;

    private List<LectureCommentReplyDto> replies = new ArrayList<>();
}
