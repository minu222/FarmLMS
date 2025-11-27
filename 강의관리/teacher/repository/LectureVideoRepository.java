package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;

import java.util.List;
import java.util.Optional;

public interface LectureVideoRepository {

    List<VideoQuizSummary> findVideoSummariesByLectureId(Long lectureId);

    Optional<LectureVideo> findById(Long videoId);
}