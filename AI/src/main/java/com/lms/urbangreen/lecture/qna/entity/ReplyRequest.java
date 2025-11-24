package com.lms.urbangreen.lecture.qna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReplyRequest {
    private int lectureId;

    @JsonProperty("pQnaId")
    private Integer pQnaId;

    private String content;
}