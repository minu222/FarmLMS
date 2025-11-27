package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureProgressView;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherLectureProgressService {

    private final LectureProgressRepository lectureProgressRepository;

    public TeacherLectureProgressService(LectureProgressRepository lectureProgressRepository) {
        this.lectureProgressRepository = lectureProgressRepository;
    }

    public List<LectureProgressView> getProgressByLectureId(int lectureId) {
        return lectureProgressRepository.findByLectureId(lectureId);
    }
}
