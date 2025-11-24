package com.lms.urbangreen.lecture.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureSub {
    private Integer subId; // DB에서 Auto Increment될 예정이므로 Integer 사용
    private Integer userId;
    private Integer lectureId;
}