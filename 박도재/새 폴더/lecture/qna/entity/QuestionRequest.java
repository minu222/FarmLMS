package com.lms.urbangreen.urbangreenproject.lecture.qna.entity;

import lombok.Data;

@Data
public class QuestionRequest {
    private int lectureId;
    private String content;
}
