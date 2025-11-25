package com.lms.urbangreen.urbangreenproject.mypage.service;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.repository.MypageCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageCommentsServiceImpl implements MypageCommentsService {

    private final MypageCommentsRepository commentsRepository;

    @Override
    public List<MyLectureCommentSummaryDto> getMyCommentLectures(int userId) {
        return commentsRepository.findLecturesWithMyComments(userId);
    }

    @Override
    public List<MyLectureCommentDto> getMyCommentsForLecture(int lectureId, int userId) {
        return commentsRepository.findMyCommentsByLecture(lectureId, userId);
    }

    @Override
    public void deleteMyComment(int commentId, int userId) {
        int affected = commentsRepository.deleteMyComment(commentId, userId);
        // 필요하면 여기서 affected == 0 일 때 예외 던져도 됨(권한 없음 or 존재하지 않음)
    }
}
