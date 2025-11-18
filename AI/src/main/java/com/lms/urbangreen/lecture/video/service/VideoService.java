package com.lms.urbangreen.lecture.video.service;

import com.lms.urbangreen.lecture.video.entity.Video;
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
}
