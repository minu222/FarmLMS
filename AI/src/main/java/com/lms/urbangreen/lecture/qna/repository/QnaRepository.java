package com.lms.urbangreen.lecture.qna.repository;

import com.lms.urbangreen.lecture.qna.entity.Qna;
import java.util.List;
import java.util.Optional;

public interface QnaRepository {
    // 강의 ID를 기반으로 부모 QnA(질문) 리스트 조회 (페이징은 Service에서 처리)
    List<Qna> findParentQnasByLectureId(int lectureId);

    // 특정 질문 ID를 기반으로 답변 리스트 조회
    List<Qna> findRepliesByParentQnaId(int pQnaId);

    // QnA 저장 (질문 또는 답변)
    Qna save(Qna qna);

    // QnA 수정
    void updateContent(int qnaId, String content);

    // QnA 삭제
    void deleteById(int qnaId);

    // 단일 QnA 조회 (수정/삭제 권한 확인용)
    Optional<Qna> findById(int qnaId);
}