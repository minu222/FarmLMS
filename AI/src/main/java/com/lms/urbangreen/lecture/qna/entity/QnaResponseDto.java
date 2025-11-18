package com.lms.urbangreen.lecture.qna.entity;

import com.lms.urbangreen.lecture.qna.entity.Qna;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QnA 응답 구조 (질문 + 답변 리스트 + 작성자 닉네임)
 */
@Data
@Builder
public class QnaResponseDto {
    private int qnaId;
    private int userId;
    private String authorNickname; // 작성자 닉네임
    private String content;
    private LocalDateTime createdAt;

    // 답변 리스트 (질문일 경우에만 사용)
    private List<QnaResponseDto> replies;

    // 이 QnA가 현재 로그인한 유저의 것인지 판별하는 필드 (프론트엔드 수정/삭제 버튼 노출용)
    private boolean isCurrentUserAuthor;

    // 이 QnA가 강사인지 판별하는 필드 (프론트엔드 답글 입력창 노출용)
    private boolean isAuthorInstructor;
}