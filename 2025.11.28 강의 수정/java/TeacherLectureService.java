package com.lms.urbangreen.urbangreenproject.teacher.service;

import com.lms.urbangreen.urbangreenproject.lecture.service.GcsService;
import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TeacherLectureService {

    private final LectureRepository lectureRepository;
    private final GcsService gcsService;

    public TeacherLectureService(LectureRepository lectureRepository,  GcsService gcsService) {
        this.lectureRepository = lectureRepository;
        this.gcsService = gcsService;
    }


    public Lecture getLectureById(int lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
    }


    // ✅ 강사별 강의 목록
    public List<Lecture> getLecturesByTeacher(int teacherId) {
        return lectureRepository.findByTeacherId(teacherId);
    }

    // 강의 등록 및 id 반환
    public int registLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }

    // ✅ 강의 수정
    public void updateLecture(
            int lectureId, int userId, String category1, String category2,
            String title, String content, MultipartFile thumbnailFile, String existingImageUrl
    ) throws IOException {

        // 1. 기존 강의 정보 조회 및 권한 확인
        Lecture existingLecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의 ID입니다."));

        if (existingLecture.getUserId() != userId) {
            throw new IllegalArgumentException("강의를 수정할 권한이 없습니다.");
        }

        String finalThumbnailUrl;

        // 2. 썸네일 파일 처리
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 새 파일이 있으면: 기존 파일 삭제 후 새 파일 업로드
            if (existingLecture.getImgUrl() != null && !existingLecture.getImgUrl().isEmpty()) {
                gcsService.deleteFile(existingLecture.getImgUrl());
            }
            finalThumbnailUrl = gcsService.uploadFile(thumbnailFile, "thumbnail");
        } else {
            // 새 파일이 없으면: 기존 URL 유지 (existingImageUrl이 넘어왔을 것임)
            finalThumbnailUrl = existingImageUrl;
        }

        // 3. Lecture 객체 업데이트
        existingLecture.setCategory(category1);
        existingLecture.setSubCategory(category2);
        existingLecture.setTitle(title);
        existingLecture.setContent(content);
        existingLecture.setImgUrl(finalThumbnailUrl); // 업데이트된 URL 설정

        // 4. Repository 업데이트 호출
        lectureRepository.update(existingLecture);
    }

    // ✅ 여러 강의 삭제
    public void deleteLectures(List<Integer> lectureIds, int teacherId) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return;
        }
        for (Integer lectureId : lectureIds) {
            if (lectureId != null) {
                lectureRepository.deleteByIdAndTeacherId(lectureId, teacherId);
            }
        }
    }
    
}