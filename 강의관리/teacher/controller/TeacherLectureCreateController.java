// src/main/java/com/lms/urbangreen/urbangreenproject/teacher/controller/TeacherLectureCreateController.java
package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher")
public class TeacherLectureCreateController {

    private final TeacherLectureService teacherLectureService;

    public TeacherLectureCreateController(TeacherLectureService teacherLectureService) {
        this.teacherLectureService = teacherLectureService;
    }

    /**
     * 새 강의 등록 폼
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
     * 새 강의 등록 처리
     * POST /teacher/lectureManage/new
     */
    @PostMapping("/lectureManage/new")
    public String createLecture(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @RequestParam("category1") String category1,
            @RequestParam("category2") String category2,
            @RequestParam("courses_name") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            RedirectAttributes redirectAttributes
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        int teacherId = loginUser.getUserId();

        // ✅ Lecture 엔티티 생성
        Lecture lecture = new Lecture();
        lecture.setUserId(teacherId);
        lecture.setCategory(category1);
        lecture.setSubCategory(category2);
        lecture.setTitle(title);
        lecture.setContent(description);
        lecture.setImgUrl(null); // 썸네일 이미지 있으면 나중에 세팅

        // DB 저장
        int lectureId = teacherLectureService.createLecture(lecture);

            // ✅ 한 번만 띄울 알림 플래시로 전달
        redirectAttributes.addFlashAttribute("lectureCreated", true);

        // 강의 관리 메인으로 이동
        return "redirect:/teacher/lectureManage";
    }
}
