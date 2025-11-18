package com.lms.urbangreen.lecture.video.controller;

import com.lms.urbangreen.lecture.video.entity.Video;
import com.lms.urbangreen.lecture.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {
    private final VideoService videoService;

    @GetMapping("/{videoId}")
    @ResponseBody // 이 어노테이션을 사용하여 반환 객체를 HTTP 응답 본문(JSON)으로 변환합니다.
    public Video getVideoData(@PathVariable("videoId") int videoId) {
    // Optional<Video>에서 Video 객체를 가져옵니다.
    // 비디오가 없을 경우 (isEmpty), null을 반환하여 클라이언트에서 처리하게 합니다.
        Optional<Video> videoOpt = videoService.findById(videoId);
        return videoOpt.orElse(null);
    }

}
