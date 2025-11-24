package com.lms.urbangreen.lecture.progress.service;

import com.lms.urbangreen.lecture.progress.entity.LectureProgress;
import com.lms.urbangreen.lecture.progress.repository.LectureProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LectureProgressService {

    private final LectureProgressRepository lectureProgressRepository;
    // LectureService 등 다른 서비스가 필요할 경우 여기에 주입합니다.

    /**
     * 특정 강의와 사용자에 대한 강의 진도율 레코드를 조회하거나 새로 생성합니다.
     * 이 메서드는 LectureVideoProgressService에서 호출되어 progress_id를 제공합니다.
     */
    @Transactional
    public LectureProgress findOrCreateProgress(int lectureId, int userId) {
        // 1. 기존 레코드 조회
        return lectureProgressRepository.findByLectureIdAndUserId(lectureId, userId)
                .orElseGet(() -> {
                    // 2. 레코드가 없으면 새로 생성
                    LectureProgress newProgress = new LectureProgress();
                    newProgress.setLecture_id(lectureId);
                    newProgress.setUser_id(userId);
                    newProgress.setProgress(new BigDecimal("0.00"));
                    // valid_until 설정 (TODO: 강의 만료 정책에 따라 설정해야 함, 현재는 null)
                    newProgress.setValid_until(null);

                    // 3. DB에 INSERT 및 생성된 ID 가져오기
                    int progressId = lectureProgressRepository.insert(newProgress);
                    newProgress.setProgress_id(progressId);

                    return newProgress;
                });
    }

    /**
     * 강의 전체 진도율을 업데이트합니다. (예: 모든 비디오 시청 완료 후)
     */
    @Transactional
    public void updateOverallProgress(int progressId, BigDecimal progressValue) {
        lectureProgressRepository.updateProgress(progressId, progressValue);
    }
}