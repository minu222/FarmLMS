package com.lms.urbangreen.lecture.qna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QnaResponseDto {
    private int qnaId;
    private int userId;
    private String authorNickname;
    private String content;
    private LocalDateTime createdAt;

    // JSON으로 나갈 때 이름을 "isCurrentUserAuthor"로 유지하라는 명령
    @JsonProperty("isCurrentUserAuthor")
    private boolean isCurrentUserAuthor;

    @JsonProperty("isAuthorInstructor")
    private boolean isAuthorInstructor;

    private List<QnaResponseDto> replies;
}