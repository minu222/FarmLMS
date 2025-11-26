package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsSummaryDto;

import java.util.List;

public interface TeacherCommentsService {

    /** 강사가 올린 강의 + 각 강의의 질문 개수 */
    List<TeacherLectureCommentsSummaryDto> getTeacherLecturesWithComments(int teacherUserId);

    /** 강사가 올린 특정 강의에 달린 질문 + 답글 */
    List<TeacherLectureCommentsDto> getCommentsForLectureOfTeacher(int lectureId, int teacherUserId);

    /** 강사의 강의에 달린 질문(댓글) 삭제 */
    void deleteCommentOnMyLecture(int commentId, int teacherUserId);

    /** 강사의 강의에 달린 답글 삭제 */
    void deleteReplyOnMyLecture(int replyId, int teacherUserId);

    /** 내 강의의 특정 질문에 답글 추가 */
    void addReplyToMyLectureComment(int commentId, int teacherUserId, String content);
}
