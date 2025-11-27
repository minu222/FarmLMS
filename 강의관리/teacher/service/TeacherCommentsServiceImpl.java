package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.repository.MypageCommentsRepository;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsSummaryDto;
import com.lms.urbangreen.urbangreenproject.teacher.repository.TeacherCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherCommentsServiceImpl implements TeacherCommentsService {

    private final TeacherCommentsRepository teacherCommentsRepository;

    @Override
    public List<TeacherLectureCommentsSummaryDto> getTeacherLecturesWithComments(int teacherUserId) {
        return teacherCommentsRepository.findLecturesForTeacher(teacherUserId);
    }

    @Override
    public List<TeacherLectureCommentsDto> getCommentsForLectureOfTeacher(int lectureId, int teacherUserId) {
        return teacherCommentsRepository.findCommentsForLectureOfTeacher(lectureId, teacherUserId);
    }

    @Override
    public void deleteCommentOnMyLecture(int commentId, int teacherUserId) {
        teacherCommentsRepository.deleteCommentOnMyLecture(commentId, teacherUserId);
    }

    @Override
    public void deleteReplyOnMyLecture(int replyId, int teacherUserId) {
        teacherCommentsRepository.deleteReplyOnMyLecture(replyId, teacherUserId);
    }

    @Override
    public void addReplyToMyLectureComment(int commentId, int teacherUserId, String content) {
        int updated = teacherCommentsRepository.insertReplyToMyLectureComment(commentId, teacherUserId, content);
        if (updated == 0) {
            // 질문이 없거나, 이 강의의 담당 강사가 아니라는 뜻
            throw new IllegalArgumentException("해당 댓글이 없거나 이 강사의 강의가 아닙니다.");
        }
    }
}
