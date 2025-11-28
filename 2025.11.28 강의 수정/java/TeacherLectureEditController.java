package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
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
@RequestMapping("/teacher/lectureManage")
public class TeacherLectureEditController {

    private final TeacherLectureService lectureService;
    private final LectureVideoService videoService;

    public TeacherLectureEditController(TeacherLectureService lectureService, LectureVideoService videoService) {
        this.lectureService = lectureService;
        this.videoService = videoService;
    }

    @GetMapping("/{lectureId}/edit")
    public String showEditForm(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @PathVariable("lectureId") int lectureId,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 1. 강의 ID로 기존 강의 정보 조회
        Lecture lecture = lectureService.getLectureById(lectureId);

        // 2. 권한 확인 (강의의 소유주인지)
        if (lecture.getUserId() != loginUser.getUserId()) {
            // 권한이 없으면 403 페이지 또는 강의 관리 페이지로 리다이렉트
            return "redirect:/teacher/lectureManage";
        }

        model.addAttribute("lecture", lecture);
        model.addAttribute("active", "lectureManage");

        // 수정 폼으로 이동
        return "teacher/modifyLecture";
    }

    // ✅ 수정 처리
    @PostMapping("/{lectureId}/edit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editLecture(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @PathVariable("lectureId") int lectureId, // 수정하려는 강의 ID
            @RequestParam("category1") String category1,
            @RequestParam("category2") String category2,
            @RequestParam("courses_name") String title,
            @RequestParam("description") String content,
            @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(name = "existingImageUrl", required = false) String existingImageUrl // 기존 URL
    ) {
        Map<String, String> response = new HashMap<>();

        if (loginUser == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // 1. 서비스 호출: 강의 정보 업데이트 및 파일 처리
            // thumbnailFile이 없으면 기존 URL(existingImageUrl)을 사용하도록 서비스에서 처리
            lectureService.updateLecture(
                    lectureId, loginUser.getUserId(), category1, category2, title, content, thumbnailFile, existingImageUrl
            );

            // 2. 성공 응답 (다음 페이지 URL 포함: 비디오 수정 페이지)
            String nextRedirectUrl = "/teacher/lectureManage/" + lectureId + "/videoEdit";

            response.put("message", "강의 기본 정보가 수정되었습니다.");
            response.put("redirectUrl", nextRedirectUrl);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "강의 수정 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{lectureId}/videoEdit")
    public String showVideoEditForm(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @PathVariable("lectureId") int lectureId,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 1. 해당 강의 ID의 모든 비디오 정보 조회
        List<Video> existingVideos = videoService.getVideosByLectureId(lectureId);

        model.addAttribute("lectureId", lectureId);
        model.addAttribute("existingVideos", existingVideos); // ✅ 기존 비디오 목록
        model.addAttribute("active", "lectureManage");

        // 비디오 수정 템플릿으로 이동
        return "teacher/modifyLecture2";
    }

    // 비디오 목록 수정 및 삭제/추가
    @PostMapping("/video/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateVideos(
            @RequestParam("lectureId") int lectureId,
            // Hidden 필드로 넘어오는 기존 비디오 ID (새 파일일 경우 null/0)
            @RequestParam(name = "videoId", required = false) List<Long> videoIds,
            @RequestParam(name = "videoTitle", required = false) List<String> videoTitles,
            @RequestParam(name = "videoFile", required = false) List<MultipartFile> videoFiles,
            // Hidden 필드로 넘어오는 기존 비디오 URL (파일 교체 없을 때 사용)
            @RequestParam(name = "existingUrl", required = false) List<String> existingUrls
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // Service 통합 로직 호출: 수정, 추가, 삭제 모두 처리
            videoService.updateVideos(lectureId, videoIds, videoTitles, videoFiles, existingUrls);

            response.put("message", "강의 영상이 성공적으로 수정 및 업데이트되었습니다.");
            response.put("redirectUrl", "/teacher/lectureManage"); // 최종 완료 후 강의 관리 페이지로 이동

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            response.put("message", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "비디오 수정 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
