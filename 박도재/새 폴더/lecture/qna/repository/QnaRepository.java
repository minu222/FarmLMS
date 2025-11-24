package com.lms.urbangreen.urbangreenproject.lecture.qna.repository;


import com.lms.urbangreen.urbangreenproject.lecture.qna.entity.Qna;

import java.util.List;
import java.util.Optional;

public interface QnaRepository {
    // 페이징 처리된 부모 질문 조회
    List<Qna> findParentQnasByLectureId(int lectureId, int offset, int limit);

    // 전체 부모 질문 개수 조회 (Page 객체 생성용)
    int countParentQnasByLectureId(int lectureId);

    List<Qna> findRepliesByParentQnaId(int pQnaId);
    Qna save(Qna qna);
    void deleteById(int qnaId);
    Optional<Qna> findById(int qnaId);
    void updateContent(int qnaId, String content);
}