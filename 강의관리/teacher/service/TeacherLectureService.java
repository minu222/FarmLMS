package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherLectureService {

    private final LectureRepository lectureRepository;

    public TeacherLectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }


    public Lecture getLectureById(int lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
    }


    // ✅ 강사별 강의 목록
    public List<Lecture> getLecturesByTeacher(int teacherId) {
        return lectureRepository.findByTeacherId(teacherId);
    }

    // ✅ 새 강의 등록
    @Transactional
    public int createLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }


    // ✅ 강의 수정
    @Transactional
    public void updateLecture(int lectureId,
                              String category,
                              String subCategory,
                              String title,
                              String content) {
        Lecture lecture = getLectureById(lectureId);
        lecture.setCategory(category);
        lecture.setSubCategory(subCategory);
        lecture.setTitle(title);
        lecture.setContent(content);

        lectureRepository.update(lecture);
    }
}