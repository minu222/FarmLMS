package com.lms.urbangreen.lecture.qna.service;

import com.lms.urbangreen.lecture.qna.entity.Qna;
import com.lms.urbangreen.lecture.qna.entity.QnaResponseDto;
import com.lms.urbangreen.lecture.qna.repository.QnaRepository;
import com.lms.urbangreen.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<QnaResponseDto> getQnaPageByLectureId(int lectureId, int currentUserId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        // 1. 부모 질문 조회 (페이징)
        List<Qna> parentQnas = qnaRepository.findParentQnasByLectureId(lectureId, offset, limit);

        // 2. 전체 개수 조회 (PageImpl 생성용)
        int totalCount = qnaRepository.countParentQnasByLectureId(lectureId);

        // 3. DTO 변환
        List<QnaResponseDto> dtos = parentQnas.stream()
                .map(qna -> convertToDto(qna, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, totalCount);
    }

    private QnaResponseDto convertToDto(Qna qna, int currentUserId) {
        String nickname = userService.findNicknameByUserId(qna.getUserId()).orElse("알 수 없음");

        List<QnaResponseDto> replies = null;

        // getPQnaId()가 null인 경우 (최상위 질문) 또는 pQnaId가 0인 경우 (기존 로직 지원)
        if (qna.getPQnaId() == null || qna.getPQnaId() == 0) {
            List<Qna> replyEntities = qnaRepository.findRepliesByParentQnaId(qna.getQnaId());
            replies = replyEntities.stream()
                    .map(reply -> convertToDto(reply, currentUserId))
                    .collect(Collectors.toList());
        }

        return QnaResponseDto.builder()
                .qnaId(qna.getQnaId())
                .userId(qna.getUserId())
                .authorNickname(nickname)
                .content(qna.getContent())
                .createdAt(qna.getCreatedAt())
                .isCurrentUserAuthor(currentUserId != -1 && qna.getUserId() == currentUserId)
                .replies(replies)
                .build();
    }

    @Transactional
    public void createQuestion(int lectureId, int userId, String content) {
        Qna qna = new Qna();
        qna.setLectureId(lectureId);
        qna.setUserId(userId);
        qna.setContent(content);
        qna.setPQnaId(null); // 질문
        qnaRepository.save(qna);
    }

    @Transactional
    public void createReply(int lectureId, int userId, int pQnaId, String content) {

        System.out.println("Service: 부모 질문 ID 조회 시도 -> pQnaId: " + pQnaId);
        // 1. 답변을 달고자 하는 부모 질문이 있는지 확인 (선택 사항이지만 안전합니다.)
        qnaRepository.findById(pQnaId)
                .orElseThrow(() -> new RuntimeException("답변하려는 부모 질문이 존재하지 않습니다."));

        // 2. Qna 엔티티 생성 시 pQnaId 설정
        Qna qna = Qna.builder()
                .lectureId(lectureId)
                .userId(userId)
                // ▼▼▼ [수정 핵심] pQnaId 값을 설정합니다. ▼▼▼
                .pQnaId(pQnaId)
                .content(content)
                .build();

        qnaRepository.save(qna);
    }

    @Transactional
    public void deleteQna(int qnaId, int currentUserId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("해당 QnA를 찾을 수 없습니다."));

        // 1. 작성자 본인인지 확인
        if (qna.getUserId() != currentUserId) {
            throw new RuntimeException("삭제 권한이 없습니다. 본인의 글만 삭제할 수 있습니다.");
        }

        // 2. 부모-자식 관계 확인
        if (qna.getPQnaId() == null) {
            // 부모 질문을 삭제하려 할 때, 자식 답변이 있는지 확인 (선택 사항)
            if (qnaRepository.findRepliesByParentQnaId(qnaId).size() > 0) {
                throw new RuntimeException("답변이 달린 질문은 삭제할 수 없습니다. 답변을 먼저 삭제해주세요.");
            }
        } else {
            // 자식 답변인 경우 (답변 삭제는 부모 질문에 영향 없이 바로 가능)
        }

        // 3. QnA 삭제 실행
        qnaRepository.deleteById(qnaId);
    }

    @Transactional
    public void updateQna(int qnaId, int currentUserId, String content) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("해당 QnA를 찾을 수 없습니다."));

        // 1. 작성자 본인인지 확인
        if (qna.getUserId() != currentUserId) {
            throw new RuntimeException("수정 권한이 없습니다. 본인의 글만 수정할 수 있습니다.");
        }

        // 2. 답변인지 확인 (답변은 pQnaId가 null이 아님)
        if (qna.getPQnaId() != null) {
            // 답변인 경우, 추가적으로 강사 권한 체크를 할 수도 있지만,
            // 답변 등록 시 userId가 강사로 등록되었으므로 본인 확인만으로 충분합니다.
        }

        // 3. 내용 수정 및 저장
        qna.setContent(content);
        qnaRepository.updateContent(qnaId, content);
    }

}