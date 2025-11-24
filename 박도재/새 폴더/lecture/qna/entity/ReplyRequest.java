package com.lms.urbangreen.urbangreenproject.lecture.qna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReplyRequest {
    private int lectureId;

    @JsonProperty("pQnaId")
    private Integer pQnaId;

    private String content;
}