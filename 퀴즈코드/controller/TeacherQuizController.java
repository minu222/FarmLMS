package com.lms.urbangreen.urbangreenproject.teacher.controller;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.domain.Quiz;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.service.TeacherQuizService;
import com.lms.urbangreen.urbangreenproject.teacher.service.LectureVideoService;
import com.lms.urbangreen.urbangreenproject.teacher.service.TeacherLectureService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/teacher/quizManage")
public class TeacherQuizController {

    private final TeacherLectureService lectureService;
    private final LectureVideoService lectureVideoService;
    private final TeacherQuizService quizService;


    public TeacherQuizController(TeacherLectureService lectureService,
                                 LectureVideoService lectureVideoService, TeacherQuizService quizService) {
        this.lectureService = lectureService;
        this.lectureVideoService = lectureVideoService;
        this.quizService = quizService;
    }

    @GetMapping
    public String quizManageRoot() {
        return "redirect:/teacher/quizManage/select";
    }

    /**
     * 1단계: 퀴즈 관리 진입 시 강의 선택 화면
     * URL: /teacher/quizManage/select
     */
    @GetMapping("/select")
    public String selectLecture(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            Model model
    ) {
        // 로그인 안 되어 있으면 로그인 페이지나 메인으로 보내기
        if (loginUser == null) {
            return "redirect:/login"; // 👉 너 프로젝트의 로그인 URL에 맞게 수정
        }

        // ✅ 여기서 강사 ID 꺼내기
        int teacherId = loginUser.getUserId(); // getId()면 그걸로 바꿔줘

        List<Lecture> lectures = lectureService.getLecturesByTeacher(teacherId);
        model.addAttribute("lectures", lectures);
        model.addAttribute("active", "quizManage");

        // 강의 선택 화면 템플릿
        return "teacher/QuizChoice";
    }

    /**
     * 2단계: 특정 강의에 대한 퀴즈 관리 화면
     * URL: /teacher/quizManage/{lectureId}
     */
    @GetMapping("/{lectureId}")
    public String manageQuiz(
            @PathVariable Long lectureId,
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            Model model
    ) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        // ✅ 해당 강의의 영상(목차) + 퀴즈 개수 가져오기
        List<VideoQuizSummary> videos = lectureVideoService.getVideoSummariesForLecture(lectureId);

        System.out.println("[QuizManage] lectureId=" + lectureId + ", video count=" + videos.size());

        model.addAttribute("lectureId", lectureId);
        model.addAttribute("videoList", videos); // ✅ 여기 이름 중요
        model.addAttribute("active", "quizManage");

        // 네가 이미 만들어둔 quizManage.html
        return "teacher/quizManage";
    }

    // 3단계: 특정 영상에 대한 퀴즈 출제 화면
    @GetMapping("/{lectureId}/video/{videoId}/create")
    public String showQuizCreateForm(@PathVariable Long lectureId,
                                     @PathVariable Long videoId,
                                     @SessionAttribute(name = "loginUser", required = false) User loginUser,
                                     Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        LectureVideo video = lectureVideoService.getVideo(videoId);

        model.addAttribute("lectureId", lectureId);
        model.addAttribute("video", video);
        model.addAttribute("active", "quizManage");

        return "teacher/uploadQuiz"; // 아래에서 수정할 출제 페이지
    }

    // 4단계: 특정 영상에 대한 기존 퀴즈 수정 화면
    @GetMapping("/{lectureId}/video/{videoId}/edit")
    public String showQuizEditForm(@PathVariable Long lectureId,
                                   @PathVariable Long videoId,
                                   @SessionAttribute(name = "loginUser", required = false) User loginUser,
                                   Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        LectureVideo video = lectureVideoService.getVideo(videoId);
        List<Quiz> quizzes = quizService.getQuizzesByVideoId(videoId);

        model.addAttribute("lectureId", lectureId);
        model.addAttribute("video", video);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("active", "quizManage");

        return "teacher/modifyQuiz"; // 아래에서 수정할 수정 페이지
    }

    // 퀴즈 전체 삭제 (해당 영상 기준)
    @PostMapping("/{lectureId}/video/{videoId}/delete")
    public String deleteQuiz(@PathVariable Long lectureId,
                             @PathVariable Long videoId,
                             @SessionAttribute(name = "loginUser", required = false) User loginUser) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        quizService.deleteByVideoId(videoId);
        return "redirect:/teacher/quizManage/" + lectureId;
    }
}