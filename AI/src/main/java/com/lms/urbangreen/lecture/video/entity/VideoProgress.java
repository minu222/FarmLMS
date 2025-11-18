package com.lms.urbangreen.lecture.video.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VideoProgress {
    private int video_progress_id;
    private int progress_id;
    private int video_id;
    private BigDecimal progress;
    private LocalDateTime completed_at;
}
