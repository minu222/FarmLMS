package com.lms.urbangreen.urbangreenproject.mypage.repository;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;

import java.util.List;

public interface MypageCommentsRepository {

    /**
     * 특정 유저가 댓글을 단 강의 목록 (강의당 댓글 수 포함)
     */
    List<MyLectureCommentSummaryDto> findLecturesWithMyComments(int userId);

    /**
     * 특정 강의에서 이 유저가 작성한 댓글 + 그 댓글에 달린 답글 목록
     */
    List<MyLectureCommentDto> findMyCommentsByLecture(int lectureId, int userId);

    /**
     * 내가 쓴 댓글만 삭제 (FK CASCADE 로 답글까지 같이 삭제됨)
     */
    int deleteMyComment(int qnaId, int userId);
}
