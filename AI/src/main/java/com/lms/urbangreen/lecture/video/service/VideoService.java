package com.lms.urbangreen.lecture.video.service;

import com.lms.urbangreen.lecture.video.entity.Video;
import com.lms.urbangreen.lecture.video.entity.VideoDetailResponse;
import com.lms.urbangreen.lecture.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    public List<Video> findByLectureId(int lectureId) {
        return videoRepository.findByLectureId(lectureId);
    }

    public Optional<Video> findById(int videoId) {
        // findById는 List를 반환하므로, 첫 번째 요소를 Optional로 감싸 반환
        List<Video> results = videoRepository.findById(videoId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<VideoDetailResponse> findVideoDetailById(int videoId) {
        return findById(videoId)
                .map(video -> VideoDetailResponse.builder()
                        .video_id(video.getVideo_id())
                        .lecture_id(video.getLecture_id())
                        .video_title(video.getVideo_title())
                        .video_url(video.getVideo_url())
                        .video_time(video.getVideo_time()) // 비디오 전체 시간
                        .user_duration_sec(0) // 초기값 0, Controller에서 실제 진도율로 덮어씀
                        .build());
    }
}
