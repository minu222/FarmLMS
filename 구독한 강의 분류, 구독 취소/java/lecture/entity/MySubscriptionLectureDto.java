package com.lms.urbangreen.urbangreenproject.lecture.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // 기본 생성자 추가
import java.math.BigDecimal; // 진도율을 위해 추가

/**
 * 마이페이지 '내가 구독한 강의' 목록 전용 DTO
 * (상속 관계 없이 독립적으로 작성되었으며, 진도율(Progress) 정보를 포함합니다.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySubscriptionLectureDto {

    // 기존 LectureListResponseDto의 필드와 동일하게 구성
    private int lecture_id;
    private String category;
    private String sub_category;
    private String title;
    private String content;
    private String img_url;
    private String instructorNickname;
    private BigDecimal progress;

    /**
     * Lecture 엔티티, 강사 닉네임, 진도율을 받아 DTO를 생성하는 생성자
     * (Repository Layer에서 RowMapper를 통해 매핑할 때 유용)
     */
    public MySubscriptionLectureDto(Lecture lecture, String instructorNickname, BigDecimal progress) {
        this.lecture_id = lecture.getLecture_id();
        this.category = lecture.getCategory().name();
        this.sub_category = lecture.getSub_category().name();
        this.title = lecture.getTitle();
        this.content = lecture.getContent();
        this.img_url = lecture.getImg_url();
        this.instructorNickname = instructorNickname;
        this.progress = progress;
    }
}