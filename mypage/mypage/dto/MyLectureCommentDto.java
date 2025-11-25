package com.lms.urbangreen.urbangreenproject.mypage.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyLectureCommentDto {

    /** lecture_qna.qna_id (질문/내 댓글) */
    private int id;

    /** 작성자 이름 (나) */
    private String authorName;

    /** 내용 */
    private String content;

    /** 작성 시간 */
    private LocalDateTime createdAt;

    /** 이 댓글에 달린 답글 목록 */
    private List<MyLectureReplyDto> replies = new ArrayList<>();
}
