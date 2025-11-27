package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureVideoService {

    private final LectureVideoRepository lectureVideoRepository;

    public LectureVideoService(LectureVideoRepository lectureVideoRepository) {
        this.lectureVideoRepository = lectureVideoRepository;
    }

    public List<VideoQuizSummary> getVideoSummariesForLecture(Long lectureId) {
        return lectureVideoRepository.findVideoSummariesByLectureId(lectureId);
    }

    public LectureVideo getVideo(Long videoId) {
        return lectureVideoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("영상 정보를 찾을 수 없습니다. videoId=" + videoId));
    }
}