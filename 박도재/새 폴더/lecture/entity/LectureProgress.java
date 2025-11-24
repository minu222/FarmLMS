package com.lms.urbangreen.urbangreenproject.lecture.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LectureProgress {
    private int progress_id;
    private int lecture_id;
    private int user_id;
    private BigDecimal progress;
    private int earned_point;
    private LocalDateTime valid_until;
    private LocalDateTime updated_at;
}
