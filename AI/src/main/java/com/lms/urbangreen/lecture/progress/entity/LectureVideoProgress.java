package com.lms.urbangreen.lecture.progress.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LectureVideoProgress {
    @Id
    private int video_progress_id; // 비디오 진도율 id
    private int progress_id; // 강의 진도율 id
    private int video_id; // 강의 영상 id
    private int user_id; // 유저 id
    private int watched_time; // 시청시간(초)
    private int last_position; // 마지막 시청위치(초)
    private LocalDateTime watched_at; // 마지막 시청 시간
    private BigDecimal progress; // 진도율(0.00~1.00)
    private LocalDateTime completed_at; // 진도율 90% 달성 시간
}
