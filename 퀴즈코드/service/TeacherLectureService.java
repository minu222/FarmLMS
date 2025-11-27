package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherLectureService {

    private final LectureRepository lectureRepository;

    public TeacherLectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<Lecture> getLecturesByTeacher(int teacherId) {
        return lectureRepository.findByTeacherId(teacherId);
    }
}
