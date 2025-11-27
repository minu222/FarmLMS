package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsSummaryDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherReplyDto;

import java.util.List;

public interface TeacherCommentsRepository {


    /**
     * 특정 강사가 올린 강의 목록 + 각 강의에 달린 질문 개수
     */
    List<TeacherLectureCommentsSummaryDto> findLecturesForTeacher(int teacherUserId);

    /**
     * 특정 강사가 올린 특정 강의에 달린
     * - 모든 질문(p_qna_id IS NULL)
     * - 각 질문에 대한 답글 목록
     */
    List<TeacherLectureCommentsDto> findCommentsForLectureOfTeacher(int lectureId, int teacherUserId);

    /**
     * 강사가 올린 강의에 달린 질문(댓글) 삭제
     * (p_qna_id IS NULL 인 row 삭제 → FK CASCADE로 답글도 같이 삭제)
     */
    int deleteCommentOnMyLecture(int qnaId, int teacherUserId);

    /**
     * 강사가 올린 강의에 달린 개별 답글 삭제
     */
    int deleteReplyOnMyLecture(int replyId, int teacherUserId);


    /**
     * 강사가 자기 강의에 달린 질문에 답글 추가
     */
    int insertReplyToMyLectureComment(int commentId, int teacherUserId, String content);
}