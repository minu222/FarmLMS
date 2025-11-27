package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher/lectureManage")
public class TeacherLectureEditController {

    private final TeacherLectureService lectureService;

    public TeacherLectureEditController(TeacherLectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/{lectureId}/edit")
    public String showEditForm(@PathVariable int lectureId,
                               @SessionAttribute(name = "loginUser", required = false) User loginUser,
                               Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        Lecture lecture = lectureService.getLectureById(lectureId);

        // (선택) 본인 강의인지 체크
        if (lecture.getUserId() != loginUser.getUserId()) {
            return "redirect:/teacher/lectureManage";
        }

        model.addAttribute("lecture", lecture);
        model.addAttribute("active", "lectureManage");

        // ✅ 템플릿 경로: templates/teacher/lectureModify.html
        return "teacher/modifyLecture";
    }

    // ✅ 수정 처리
    @PostMapping("/{lectureId}/edit")
    public String updateLecture(@PathVariable int lectureId,
                                @SessionAttribute(name = "loginUser", required = false) User loginUser,
                                @RequestParam("category1") String category,
                                @RequestParam("category2") String subCategory,
                                @RequestParam("courses_name") String title,
                                @RequestParam("description") String content) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        lectureService.updateLecture(lectureId, category, subCategory, title, content);

        // 수정 후 강의 관리 리스트로
        return "redirect:/teacher/lectureManage";
    }
}
