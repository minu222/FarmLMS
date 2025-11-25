package com.lms.urbangreen.urbangreenproject.mypage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyLectureReplyDto {

    /** lecture_qna.qna_id (답글) */
    private int id;

    /** 작성자 이름 (보통 강사/관리자) */
    private String authorName;

    /** 내용 */
    private String content;

    /** 작성 시간 */
    private LocalDateTime createdAt;
}
