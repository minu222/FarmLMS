package com.lms.urbangreen.lecture.entity;

import com.lms.urbangreen.lecture.qna.entity.Qna;
import com.lms.urbangreen.lecture.video.entity.Video;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class LectureDetailDto {
    private int lectureId;
    private String title;
    private String content;
    private Lecture.sub_category sub_category;
    private String instructorName;
    private BigDecimal progress; // 강의 전체 진도율
    private List<Video> curriculum;
    private List<Qna> qnaList;
}