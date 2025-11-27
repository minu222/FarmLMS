package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;

import java.util.List;

public interface LectureRepository {

    // 특정 강사(user_id)의 강의 목록
    List<Lecture> findByTeacherId(int teacherId);
}