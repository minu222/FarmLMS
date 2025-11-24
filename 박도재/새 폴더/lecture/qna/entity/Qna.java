package com.lms.urbangreen.urbangreenproject.lecture.qna.entity;

// Lombok Import
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Qna {
    private int qnaId;
    private int lectureId;
    private int userId;
    private Integer pQnaId; // null 처리를 위해 Integer로 변경
    private String content;
    private LocalDateTime createdAt;
}