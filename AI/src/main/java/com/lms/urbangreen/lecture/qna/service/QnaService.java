package com.lms.urbangreen.lecture.qna.service;

import com.lms.urbangreen.lecture.qna.entity.QnaResponseDto;
import com.lms.urbangreen.lecture.qna.entity.Qna;
import com.lms.urbangreen.lecture.qna.repository.QnaRepository;
import com.lms.urbangreen.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserService userService; // 닉네임/유저 타입 조회를 위해 주입

    /**
     * 특정 강의의 QnA 목록을 페이징 처리하여 DTO 형태로 반환합니다.
     */
    public Page<QnaResponseDto> getQnaPageByLectureId(int lectureId, int currentUserId, String instructorId, Pageable pageable) {
        // 1. 모든 질문(부모) QnA를 가져옵니다. (페이징은 메모리에서 처리)
        List<Qna> allParentQnas = qnaRepository.findParentQnasByLectureId(lectureId);

        // 2. 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allParentQnas.size());
        List<Qna> pageQnas = allParentQnas.subList(start, end);

        // 3. DTO 변환 및 답변(Replies) 붙이기
        List<QnaResponseDto> dtos = pageQnas.stream()
                .map(qna -> convertToDto(qna, currentUserId, instructorId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, allParentQnas.size());
    }

    private QnaResponseDto convertToDto(Qna qna, int currentUserId, String instructorId) {
        // 작성자 닉네임 조회
        String nickname = userService.findNicknameByUserId(qna.getUser_id()).orElse("탈퇴 유저");

        List<QnaResponseDto> replies = List.of();
        // 질문(부모)일 경우에만 답변을 조회하고 DTO로 변환
        if (qna.getP_qna_id() == 0) { // p_qna_id가 null이거나 0으로 설정되어 있다고 가정
            replies = qnaRepository.findRepliesByParentQnaId(qna.getQna_id()).stream()
                    .map(reply -> convertToDto(reply, currentUserId, instructorId)) // 재귀 호출 (깊이 2 레벨까지만)
                    .collect(Collectors.toList());
        }

        return QnaResponseDto.builder()
                .qnaId(qna.getQna_id())
                .userId(qna.getUser_id())
                .authorNickname(nickname)
                .content(qna.getContent())
                .createdAt(qna.getCreated_at())
                .replies(replies)
                // 권한 체크 로직
                .isCurrentUserAuthor(qna.getUser_id() == currentUserId)
                .isAuthorInstructor(userService.isUserInstructor(qna.getUser_id()))
                .build();
    }

    // 질문 등록 (p_qna_id = null)
    public QnaResponseDto createQuestion(int lectureId, int userId, String content) {
        Qna qna = new Qna();
        qna.setLecture_id(lectureId);
        qna.setUser_id(userId);
        qna.setContent(content);
        // p_qna_id는 null (질문)
        Qna saved = qnaRepository.save(qna);

        // 저장이 완료된 후 DTO로 변환하여 반환 (간소화)
        return QnaResponseDto.builder().qnaId(saved.getQna_id()).content(saved.getContent()).authorNickname(userService.findNicknameByUserId(userId).orElse("")).build();
    }

    // 답변 등록 (p_qna_id = 질문 ID)
    public QnaResponseDto createReply(int lectureId, int userId, int pQnaId, String content) {
        // ... (질문 등록과 유사)
        return QnaResponseDto.builder().build(); // 임시 반환
    }

    // QnA 수정/삭제 로직 (생략)
}