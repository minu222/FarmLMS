package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;

import java.util.List;
import java.util.Optional;

public interface LectureVideoRepository {

    List<VideoQuizSummary> findVideoSummariesByLectureId(Long lectureId);

    Optional<LectureVideo> findById(Long videoId);

    void save(Video video);
    List<Video> findByLectureId(int lectureId);

    //  비디오 수정
    void update(Video video);

    // 비디오 삭제
    void deleteById(Long videoId);
}