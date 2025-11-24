package com.lms.urbangreen.lecture.video.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoDetailResponse {
    private int video_id;
    private int lecture_id;
    private String video_title;
    private String video_url;
    private int video_time; // 비디오 전체 길이 (초) - 핵심 필드
    private int user_duration_sec; // 유저 시청 진도율 (Controller에서 주입)
}