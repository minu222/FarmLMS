package com.lms.urbangreen.lecture.progress.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LectureProgress {
    private int progress_id;
    private int lecture_id;
    private int user_id;
    private LocalDateTime valid_until;
    private BigDecimal progress;
    private LocalDateTime updated_at;
}
