package com.lms.urbangreen.lecture.qna.entity;

import lombok.Data;

@Data
public class QuestionRequest {
    private int lectureId;
    private String content;
}
