package com.lms.urbangreen.urbangreenproject.mypage.service;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;

import java.util.List;

public interface MypageCommentsService {

    List<MyLectureCommentSummaryDto> getMyCommentLectures(int userId);

    List<MyLectureCommentDto> getMyCommentsForLecture(int lectureId, int userId);

    void deleteMyComment(int commentId, int userId);
}
