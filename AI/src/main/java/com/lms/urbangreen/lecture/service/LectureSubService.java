package com.lms.urbangreen.lecture.service;

import com.lms.urbangreen.lecture.entity.LectureSub;
import com.lms.urbangreen.lecture.repository.LectureSubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LectureSubService {

    private final LectureSubRepository lectureSubRepository;

    public LectureSubService(LectureSubRepository lectureSubRepository) {
        this.lectureSubRepository = lectureSubRepository;
    }

    /**
     * 강의 구독을 추가합니다. (이미 구독되어 있으면 처리 안 함)
     */
    @Transactional
    public boolean subscribeLecture(int userId, int lectureId) {
        Optional<LectureSub> existingSub = lectureSubRepository.findByUserIdAndLectureId(userId, lectureId);
        if (existingSub.isPresent()) {
            return false; // 이미 구독됨
        }

        LectureSub newSub = new LectureSub(null, userId, lectureId);
        lectureSubRepository.save(newSub);
        return true; // 구독 성공
    }

    /**
     * 구독 상태를 확인합니다.
     */
    public boolean isSubscribed(int userId, int lectureId) {
        return lectureSubRepository.findByUserIdAndLectureId(userId, lectureId).isPresent();
    }

    // 특정 사용자가 구독한 모든 강의 ID를 Set 형태로 반환
    public Set<Integer> getSubscribedLectureIds(int userId) {
        return lectureSubRepository.findSubscribedLectureIdsByUserId(userId).stream().collect(Collectors.toSet());
    }
}