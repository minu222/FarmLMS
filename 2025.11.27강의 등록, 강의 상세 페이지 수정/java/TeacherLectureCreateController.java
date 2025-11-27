// src/main/java/com/lms/urbangreen/urbangreenproject/teacher/controller/TeacherLectureCreateController.java
package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.lecture.service.GcsService;
import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.service.LectureVideoService;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherLectureCreateController {

    private final TeacherLectureService teacherLectureService;
    private final LectureVideoService lectureVideoService;
    private final GcsService gcsService;

    public TeacherLectureCreateController(TeacherLectureService teacherLectureService, GcsService gcsService,  LectureVideoService lectureVideoService) {
        this.teacherLectureService = teacherLectureService;
        this.gcsService = gcsService;
        this.lectureVideoService = lectureVideoService;
    }

    /**
     * 1단계: 새 강의 등록 폼 (썸네일 등록)
     * GET /teacher/lectureManage/new
     */
    @GetMapping("/lectureManage/new")
    public String showCreateForm(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("active", "lectureManage");
        return "teacher/uploadLecture";
    }

    /**
     * 1단계 처리: 강의 정보 DB 저장 및 썸네일 GCS 업로드
     * 성공 시 2단계(비디오 업로드) 페이지 URL을 JSON으로 반환
     */
    @PostMapping("/lectureManage/new")
    @ResponseBody // HTML이 아닌 데이터(JSON)를 반환하기 위해 사용
    public ResponseEntity<Map<String, String>> createLecture(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @RequestParam("category1") String category1,
            @RequestParam("category2") String category2,
            @RequestParam("courses_name") String title, // HTML name="courses_name" -> DB title
            @RequestParam("description") String content, // HTML name="description" -> DB content
            @RequestParam("thumbnailFile") MultipartFile thumbnailFile
    ) {
        Map<String, String> response = new HashMap<>();

        // 1. 로그인 체크
        if (loginUser == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // 2. 썸네일 이미지 GCS 업로드
            String thumbnailUrl = null;
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                // "thumbnail" 폴더에 저장
                thumbnailUrl = gcsService.uploadFile(thumbnailFile, "thumbnail");
            }

            // 3. Lecture 객체 생성 및 데이터 바인딩
            Lecture lecture = new Lecture();
            lecture.setUserId(loginUser.getUserId()); // 세션에서 사용자 ID 가져옴
            lecture.setCategory(category1);
            lecture.setSubCategory(category2);
            lecture.setTitle(title);
            lecture.setContent(content);
            lecture.setImgUrl(thumbnailUrl); // GCS URL 저장

            // 4. 서비스 호출 (DB 저장 후 PK 반환)
            int lectureId = teacherLectureService.registLecture(lecture);

            // 5. 성공 응답 (다음 페이지 URL 포함)
            // 예: /teacher/lectureManage/uploadVideo?lectureId=15
            String nextRedirectUrl = "/teacher/lectureManage/videoUpload?lectureId=" + lectureId;

            response.put("message", "강의 기본 정보가 등록되었습니다.");
            response.put("redirectUrl", nextRedirectUrl); // 클라이언트가 이동할 주소

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "강의 등록 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 2단계: 비디오 업로드 페이지 (uploadLecture2)
     * GET /teacher/lectureManage/videoUpload?lectureId=...
     */
    @GetMapping("/lectureManage/videoUpload")
    public String showVideoUploadForm(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @RequestParam("lectureId") int lectureId,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        Lecture lecture = teacherLectureService.getLectureById(lectureId);

        model.addAttribute("lecture", lecture);
        model.addAttribute("active", "lectureManage");
        model.addAttribute("lectureId", lectureId);

        // uploadLecture2.html 페이지로 이동
        return "teacher/uploadLecture2";
    }

    // 비디오 업로드
    @PostMapping("/lectureManage/video/uploadMulti")
    @ResponseBody
    public ResponseEntity<String> uploadVideos(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @RequestParam("lectureId") int lectureId,
            // ✅ required = false 추가: 파라미터가 null이거나 개수가 달라도 일단 받도록 설정
            @RequestParam(name = "videoTitle", required = false) List<String> videoTitles,
            @RequestParam(name = "videoFile", required = false) List<MultipartFile> videoFiles
    ) {
        // 1. 로그인 체크 (생략)
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. 로그 추가 (진단용): 받은 파라미터의 크기를 확인하여 서버 로그에 남김
        if (videoFiles != null) {
            System.out.println("DEBUG: Files received count: " + videoFiles.size());
        }
        if (videoTitles != null) {
            System.out.println("DEBUG: Titles received count: " + videoTitles.size());
        }

        try {
            // 3. 서비스 호출: 필터링은 서비스 레이어에서 담당
            lectureVideoService.uploadVideos(lectureId, videoTitles, videoFiles);

            return ResponseEntity.ok("모든 영상이 성공적으로 등록되었습니다.");

        } catch (IllegalArgumentException e) {
            // 유효하지 않은 데이터가 넘어온 경우 (제목-파일 개수 불일치 등)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // ... (기타 서버 오류 처리) ...
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("영상 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}