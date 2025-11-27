package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureProgressView;

import java.util.List;

public interface LectureProgressRepository {

    List<LectureProgressView> findByLectureId(int lectureId);
}
