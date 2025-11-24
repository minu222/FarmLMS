package com.lms.urbangreen.urbangreenproject.admin.service;


import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentReplyDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureSummaryDto;
import com.lms.urbangreen.urbangreenproject.admin.repository.AdminCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final AdminCommentRepository repository;

    @Override
    public List<LectureSummaryDto> getLectureSummaries() {
        return repository.findAllLectureSummaries();
    }

    @Override
    public List<LectureCommentDto> getCommentsWithReplies(Long lectureId) {
        List<LectureCommentDto> comments = repository.findCommentsByLectureId(lectureId);
        List<LectureCommentReplyDto> replies = repository.findRepliesByLectureId(lectureId);

        // commentId 기준으로 답글 붙이기
        Map<Long, LectureCommentDto> commentMap = new LinkedHashMap<>();
        for (LectureCommentDto c : comments) {
            commentMap.put(c.getId(), c);
        }

        for (LectureCommentReplyDto r : replies) {
            LectureCommentDto parent = commentMap.get(r.getCommentId());
            if (parent != null) {
                parent.getReplies().add(r);
            }
        }

        return new ArrayList<>(commentMap.values());
    }

    @Override
    public void addReply(Long commentId, String content) {
        // teacher ID는 repository에서 lecture.user_id로 자동 채우도록 설계
        repository.insertReply(commentId, content);
    }

    @Override
    public void deleteComment(Long commentId) {
        repository.deleteComment(commentId);
    }

    @Override
    public void deleteReply(Long replyId) {
        repository.deleteReply(replyId);
    }
}