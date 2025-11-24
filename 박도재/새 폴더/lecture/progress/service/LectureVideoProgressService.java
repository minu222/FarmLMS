package com.lms.urbangreen.urbangreenproject.lecture.progress.service;



import com.lms.urbangreen.urbangreenproject.lecture.progress.entity.LectureProgress;
import com.lms.urbangreen.urbangreenproject.lecture.progress.entity.LectureVideoProgress;
import com.lms.urbangreen.urbangreenproject.lecture.progress.repository.LectureVideoProgressRepository;
import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
import com.lms.urbangreen.urbangreenproject.lecture.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureVideoProgressService {

    private final LectureVideoProgressRepository progressRepository;
    private final VideoService videoService;
    private final LectureProgressService lectureProgressService;

    /**
     * 특정 비디오와 유저에 대한 진도율 정보를 조회합니다. (Controller에서 사용)
     */
    public Optional<LectureVideoProgress> getVideoProgress(int videoId, int userId) {
        return progressRepository.findByVideoIdAndUserId(videoId, userId);
    }

    /**
     * 비디오 시청 시간을 기반으로 진도율을 계산하고 저장/업데이트합니다.
     */
    @Transactional
    public LectureVideoProgress saveOrUpdateProgress(int videoId, int userId, int durationSec, int totalSec) {

        // 1. lectureId 및 progress_id 확보
        Optional<Video> videoOpt = videoService.findById(videoId);
        if (videoOpt.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 비디오 ID입니다: " + videoId);
        }
        int lectureId = videoOpt.get().getLecture_id();

        // 강의 진도율 레코드 조회/생성 및 유효한 progress_id 확보 (FK 오류 해결)
        LectureProgress lectureProgress = lectureProgressService.findOrCreateProgress(lectureId, userId);
        int progressId = lectureProgress.getProgress_id();

        // 2. 기존 비디오 진도율 레코드 조회
        Optional<LectureVideoProgress> existingProgressOpt = progressRepository.findByVideoIdAndUserId(videoId, userId);

        // 3. 진도율 계산 및 완료 여부 판단
        double rawProgress = (double) durationSec / totalSec;
        // BigDecimal.ROUND_HALF_UP (반올림) 사용하여 소수점 셋째 자리에서 반올림
        BigDecimal progressValue = BigDecimal.valueOf(Math.min(rawProgress, 1.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
        LocalDateTime completedAt = (rawProgress >= 1.0) ? LocalDateTime.now() : null;

        LectureVideoProgress progress;

        if (existingProgressOpt.isPresent()) {
            progress = existingProgressOpt.get();
        } else {
            progress = new LectureVideoProgress();
            progress.setVideo_id(videoId);
            progress.setUser_id(userId);
            progress.setWatched_at(LocalDateTime.now());
        }

        // 4. 공통 속성 설정
        progress.setProgress_id(progressId); // 유효한 progress_id 설정
        progress.setWatched_time(durationSec);
        progress.setLast_position(durationSec);
        progress.setProgress(progressValue);
        progress.setCompleted_at(completedAt);

        // 5. DB 저장/업데이트를 Repository에 위임 (UPSERT 로직)
        LectureVideoProgress updatedProgress = progressRepository.save(progress);

        return updatedProgress;
    }
}