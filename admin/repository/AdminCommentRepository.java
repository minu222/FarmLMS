package com.lms.urbangreen.urbangreenproject.admin.repository;

import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentReplyDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureSummaryDto;

import java.util.List;

public interface AdminCommentRepository {

    // 왼쪽 강의 목록
    List<LectureSummaryDto> findAllLectureSummaries();

    // 오른쪽 질문 목록 (질문 + 기본정보)
    List<LectureCommentDto> findCommentsByLectureId(Long lectureId);

    // 오른쪽 각 질문의 답글 목록
    List<LectureCommentReplyDto> findRepliesByLectureId(Long lectureId);

    // 질문에 대한 답글 등록
    void insertReply(Long commentId, String content);

    // 질문 삭제 (답글은 FK CASCADE로 같이 삭제됨)
    void deleteComment(Long commentId);

    // 답글만 삭제
    void deleteReply(Long replyId);
}
