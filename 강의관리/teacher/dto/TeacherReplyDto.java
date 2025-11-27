package com.lms.urbangreen.urbangreenproject.teacher.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherReplyDto {


    /** lecture_qna.qna_id (답글) */
    private int id;

    /** 답글 작성자 이름 */
    private String authorName;

    /** 내용 */
    private String content;

    /** 작성 시간 */
    private LocalDateTime createdAt;
}
