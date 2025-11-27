package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureProgressView;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureProgressService;
import com.lms.urbangreen.urbangreenproject.teacher.service.LectureVideoService;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequestMapping("/teacher/lectureManage")
public class TeacherLectureManageController {

    private final TeacherLectureService lectureService;
    private final LectureVideoService lectureVideoService;
    private final TeacherLectureProgressService lectureProgressService;

    public TeacherLectureManageController(TeacherLectureService lectureService,
                                    LectureVideoService lectureVideoService,
                                    TeacherLectureProgressService lectureProgressService) {
        this.lectureService = lectureService;
        this.lectureVideoService = lectureVideoService;
        this.lectureProgressService = lectureProgressService;
    }

    /**
     * 1단계: 강의 관리 진입 시 강의 선택 화면
     * URL: /teacher/lectureManage
     */
    @GetMapping
    public String selectLecture(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login"; // 프로젝트 로그인 URL에 맞게 조정 가능
        }

        int teacherId = loginUser.getUserId();  // 👈 user_id = teacher ID
        List<Lecture> lectures = lectureService.getLecturesByTeacher(teacherId);

        model.addAttribute("lectures", lectures);
        model.addAttribute("active", "lectureManage");

        // 위에 만든 템플릿 이름(확장자 제외)
        return "teacher/LectureChoice";
    }

    /**
     *  특정 강의 관리 페이지 (목차 리스트 포함)
     *  GET /teacher/lectureManage/{lectureId}
     */
    @GetMapping("/{lectureId}")
    public String manageLecture(
            @PathVariable int lectureId,
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        Lecture lecture = lectureService.getLectureById(lectureId);
        List<VideoQuizSummary> videoList =
                lectureVideoService.getVideoSummariesForLecture((long) lectureId);

        // ✅ 여기서 진도율 데이터까지 함께 조회
        List<LectureProgressView> progressList =
                lectureProgressService.getProgressByLectureId(lectureId);

        model.addAttribute("lecture", lecture);
        model.addAttribute("videoList", videoList);
        model.addAttribute("progressList", progressList);
        model.addAttribute("active", "lectureManage");

        return "teacher/LectureManage";
    }
}
