package com.lms.urbangreen.lecture.service;

import com.lms.urbangreen.lecture.entity.LectureDetailResponseDto;
import com.lms.urbangreen.lecture.entity.LectureListResponseDto; // DTO 임포트
import com.lms.urbangreen.lecture.entity.Lecture;
import com.lms.urbangreen.lecture.repository.LectureRepo;
import com.lms.urbangreen.user.service.UserService; // UserService 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepo lectureRepo;
    private final UserService userService;

    // 기존 메서드 (엔티티 리스트 반환)
    public List<Lecture> getAllLectures() {
        return lectureRepo.findAll();
    }

    // ⭐ 추가된 메서드: 모든 강의를 DTO 형태로 반환 (닉네임 포함)
    public List<LectureListResponseDto> getAllLectureDtos() {
        // 1. 모든 강의 엔티티를 가져옵니다.
        List<Lecture> lectureList = lectureRepo.findAll();

        // 2. 각 강의 엔티티를 DTO로 변환하면서 UserService를 통해 강사 닉네임을 조회합니다.
        return lectureList.stream()
                .map(lecture -> {
                    // Lecture의 user_id(int)를 사용해 강사의 닉네임을 조회합니다.
                    String nickname = userService.findNicknameByUserId(lecture.getUser_id())
                            .orElse("알 수 없는 강사"); // 닉네임 조회 실패 시 기본값 설정

                    return new LectureListResponseDto(lecture, nickname);
                })
                .collect(Collectors.toList());
    }

    public Optional<Lecture> findById(int id) {
        return lectureRepo.findById(id);
    }

    // 강의 상세 정보를 DTO 형태로 반환 (닉네임 포함)
    public Optional<LectureDetailResponseDto> getLectureDetailDtoById(int lectureId) {
        Optional<Lecture> lectureOpt = lectureRepo.findById(lectureId);

        if (lectureOpt.isEmpty()) {
            return Optional.empty();
        }

        Lecture lecture = lectureOpt.get();

        // 1. 강사 ID로 닉네임 조회
        String nickname = userService.findNicknameByUserId(lecture.getUser_id())
                .orElse("알 수 없는 강사");

        // 2. DTO로 변환하여 반환
        return Optional.of(LectureDetailResponseDto.from(lecture, nickname));
    }
}