package com.lms.urbangreen.urbangreenproject.mypage.service;

import com.lms.urbangreen.urbangreenproject.lecture.entity.MySubscriptionLectureDto;
import com.lms.urbangreen.urbangreenproject.lecture.repository.LectureSubRepository; // LectureSubRepository 사용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageLectureService {

    // LectureSubRepository를 주입하여 구독 강의 목록 조회 메서드를 사용합니다.
    private final LectureSubRepository lectureSubRepository;

    /**
     * 특정 사용자의 구독 강의 목록과 각 강의의 진도율을 조회합니다.
     *
     * @param userId 강의 목록을 조회할 사용자 ID
     * @return MySubscriptionLectureDto 리스트 (강의 정보 + 진도율 포함)
     */
    public List<MySubscriptionLectureDto> getSubscribedLecturesWithProgress(int userId) {
        // ⭐ 이 메서드는 LectureSubRepository에 추가된 'findAllSubscribedLecturesWithProgress'를 호출해야 합니다.
        return lectureSubRepository.findAllSubscribedLecturesWithProgress(userId);
    }
}