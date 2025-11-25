package com.lms.urbangreen.urbangreenproject.admin.service;

import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureSummaryDto;

import java.util.List;

public interface AdminCommentService {

    List<LectureSummaryDto> getLectureSummaries();

    List<LectureCommentDto> getCommentsWithReplies(Long lectureId);

    void addReply(Long commentId, String content);

    void deleteComment(Long commentId);

    void deleteReply(Long replyId);
}