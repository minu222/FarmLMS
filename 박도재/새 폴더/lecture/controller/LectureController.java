package com.lms.urbangreen.urbangreenproject.lecture.controller;




import com.lms.urbangreen.urbangreenproject.lecture.entity.LectureDetailResponseDto;
import com.lms.urbangreen.urbangreenproject.lecture.entity.LectureListResponseDto;
import com.lms.urbangreen.urbangreenproject.lecture.service.LectureService;
import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
import com.lms.urbangreen.urbangreenproject.lecture.video.service.VideoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final VideoService videoService;

    @GetMapping("/all")
    public String getLectureList(Model model) {
        //DTO 목록을 가져오는 새로운 서비스 메서드를 호출합니다.
        List<LectureListResponseDto> lectureDtoList = lectureService.getAllLectureDtos();

        // 이 데이터를 'lectures'라는 이름으로 Thymeleaf 템플릿에 전달합니다.
        model.addAttribute("lectures", lectureDtoList);

        return "board/lecture";
    }

    // 강의 상세 페이지
    @GetMapping("/lectureDetail")
    public String getLectureDetail(@RequestParam("lectureId") int lectureId, Model model, HttpSession session) {

        // 로그인 여부 판단
        if (session.getAttribute("loginUser") == null) {
            // 세션에 'loginUser' 정보가 없으면, 로그인 페이지로 리다이렉트합니다.
            return "redirect:/login";
        }

        // DTO를 반환하는 서비스 메서드를 사용합니다.
        Optional<LectureDetailResponseDto> lectureDtoOpt = lectureService.getLectureDetailDtoById(lectureId);

        if (lectureDtoOpt.isEmpty()) {
            // 강의 정보가 없으면 목록으로 리다이렉트
            return "redirect:/lecture/all";
        }

        LectureDetailResponseDto lectureDto = lectureDtoOpt.get();
        // DTO 객체를 "lecture" 이름으로 Thymeleaf에 전달합니다.
        model.addAttribute("lecture", lectureDto);

        // 1. 해당 강의의 비디오 목록 조회
        // (Video 엔티티는 닉네임을 포함하지 않으므로 그대로 사용)
        List<Video> videoList = videoService.findByLectureId(lectureId);
        model.addAttribute("videoList", videoList);

        // 2. 현재 재생할 비디오 설정 (목록이 있다면 첫 번째 비디오를 기본으로 설정)
        if (!videoList.isEmpty()) {
            Video currentVideo = videoList.get(0);
            model.addAttribute("currentVideo", currentVideo);
        } else {
            model.addAttribute("currentVideo", null);
        }

        return "board/lectureDetail";
    }


}