package com.lms.urbangreen.lecture.qna.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Qna {
    private int qna_id;
    private int lecture_id;
    private int user_id;
    private int p_qna_id;
    private String content;
    private LocalDateTime created_at;

}
