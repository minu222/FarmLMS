package com.lms.urbangreen.urbangreenproject.lecture.video.controller;

import com.lms.urbangreen.urbangreenproject.lecture.progress.entity.LectureVideoProgress;
import com.lms.urbangreen.urbangreenproject.lecture.progress.entity.VideoProgressRequest;
import com.lms.urbangreen.urbangreenproject.lecture.progress.service.LectureVideoProgressService;
import com.lms.urbangreen.urbangreenproject.lecture.video.entity.VideoDetailResponse;
import com.lms.urbangreen.urbangreenproject.lecture.video.service.VideoService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoDataController {

    private final LectureVideoProgressService progressService;
    private final VideoService videoService;

    // 1. 비디오 상세 정보 및 진도율 조회 API (GET)
    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideoDetail(
            @PathVariable("videoId") int videoId,
            @SessionAttribute(name = "loginUser", required = false) User loginUser
    ) {
        // 1. VideoService를 통해 비디오 상세 정보 조회
        Optional<VideoDetailResponse> videoDataOpt = videoService.findVideoDetailById(videoId);

        if (videoDataOpt.isEmpty()) {
            return ResponseEntity.status(404).body("해당 비디오 정보를 찾을 수 없습니다.");
        }

        VideoDetailResponse videoData = videoDataOpt.get();

        // 2. 로그인 상태인 경우, 유저의 진도율 정보 조회 및 반영
        if (loginUser != null) {
            progressService.getVideoProgress(videoId, loginUser.getUserId())
                    .ifPresent(progress -> {
                        // DB에 저장된 시청 시간(watched_time)을 초기 재생 시점으로 사용
                        videoData.setUser_duration_sec(progress.getWatched_time());
                    });
        }

        // 3. 비디오 상세 정보와 유저의 시청 시간 정보가 합쳐진 DTO 반환 (JSON 응답)
        return ResponseEntity.ok(videoData);
    }

    // 2. 비디오 진도율 저장/업데이트 API (POST)
    @PostMapping("/{videoId}/progress")
    public ResponseEntity<?> updateVideoProgress(
            @PathVariable("videoId") int videoId,
            @RequestBody VideoProgressRequest request,
            @SessionAttribute(name = "loginUser", required = false) User loginUser
    ) {
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (request.getDuration_sec() == null || request.getTotal_sec() == null) {
            return ResponseEntity.badRequest().body("시청 시간 및 전체 비디오 시간이 필요합니다.");
        }

        int userId = loginUser.getUserId();
        int durationSec = request.getDuration_sec();
        int totalSec = request.getTotal_sec();

        try {
            // 진도율 서비스 호출
            LectureVideoProgress updatedProgress = progressService.saveOrUpdateProgress(
                    videoId, userId, durationSec, totalSec);

            return ResponseEntity.ok(updatedProgress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("진도율 저장 중 오류가 발생했습니다.");
        }
    }
}