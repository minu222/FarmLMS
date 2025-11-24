package com.lms.urbangreen.urbangreenproject.lecture.progress.entity;

import lombok.Data;

// 비디오 진도율 저장 요청에 사용되는 DTO
@Data
public class VideoProgressRequest {
    private Integer duration_sec; // 현재 시청 위치 (last_position)
    private Integer total_sec; // 비디오 전체 길이 (video_time)
}