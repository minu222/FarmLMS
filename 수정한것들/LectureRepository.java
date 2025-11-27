package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;

import java.util.List;
import java.util.Optional;

public interface LectureRepository {

    List<Lecture> findByTeacherId(int teacherId);

    // 👇 단일 강의 조회용 메서드 추가
    Optional<Lecture> findById(int lectureId);

    int save(Lecture lecture);

    void update(Lecture lecture);

    void deleteByIdAndTeacherId(int lectureId, int teacherId);
}