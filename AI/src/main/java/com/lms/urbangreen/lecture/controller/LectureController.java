package com.lms.urbangreen.lecture.controller;

import com.lms.urbangreen.lecture.entity.LectureDetailResponseDto;
import com.lms.urbangreen.lecture.entity.LectureListResponseDto;
import com.lms.urbangreen.lecture.service.LectureService;
import com.lms.urbangreen.lecture.service.LectureSubService;
import com.lms.urbangreen.lecture.video.entity.Video;
import com.lms.urbangreen.lecture.video.service.VideoService;
import com.lms.urbangreen.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final VideoService videoService;
    private final LectureSubService lectureSubService;

    // 1. 강의 목록 페이지
    @GetMapping("/all")
    public String getLectureList(Model model, HttpSession session) {
        List<LectureListResponseDto> lectureDtoList = lectureService.getAllLectureDtos();
        model.addAttribute("lectures", lectureDtoList);

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser != null) {
            int userId = loginUser.getUserId();
            // 사용자가 구독한 모든 강의 ID Set을 조회하여 Thymeleaf에 전달
            Set<Integer> subscribedLectureIds = lectureSubService.getSubscribedLectureIds(userId);

            model.addAttribute("subscribedLectureIds", subscribedLectureIds);
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("subscribedLectureIds", Collections.emptySet());
            model.addAttribute("isLoggedIn", false);
        }

        return "lecture/lecture";
    }

    // 2. 강의 상세 페이지 (로그인 및 구독 체크)
    @GetMapping("/lectureDetail")
    public String getLectureDetail(@RequestParam("lectureId") int lectureId, Model model, HttpSession session) {

        // 1단계 체크: 로그인 여부 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 비로그인 시, 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        int userId = loginUser.getUserId();

        // 2단계 체크: 구독 여부 확인
        boolean isSubscribed = lectureSubService.isSubscribed(userId, lectureId);
        if (!isSubscribed) {
            // 구독하지 않은 경우, 목록으로 리다이렉트 (JS에서 알림 처리)
            return "redirect:/lecture/all?sub_required=true";
        }

        // 3. DTO 및 비디오 정보 조회 (구독 완료 상태에서만 실행)
        Optional<LectureDetailResponseDto> lectureDtoOpt = lectureService.getLectureDetailDtoById(lectureId);

        if (lectureDtoOpt.isEmpty()) {
            return "redirect:/lecture/all";
        }

        LectureDetailResponseDto lectureDto = lectureDtoOpt.get();
        model.addAttribute("lecture", lectureDto);

        List<Video> videoList = videoService.findByLectureId(lectureId);
        model.addAttribute("videoList", videoList);

        //  비디오 목록의 첫 번째 비디오를 currentVideo로 설정
        if (!videoList.isEmpty()) {
            Video currentVideo = videoList.get(0);
            model.addAttribute("currentVideo", currentVideo);
        } else {
            model.addAttribute("currentVideo", null);
        }

        //  구독 및 로그인 상태 전달
        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("isLoggedIn", true);

        return "lecture/lectureDetail";
    }
}