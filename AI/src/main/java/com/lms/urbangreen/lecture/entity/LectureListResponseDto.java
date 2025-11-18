package com.lms.urbangreen.lecture.entity;

import com.lms.urbangreen.lecture.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 강의 목록 화면에 필요한 정보(Lecture + 강사 닉네임)를 담는 DTO
 */
@Data
@AllArgsConstructor
public class LectureListResponseDto {
    private int lecture_id;
    private String category;   // ENUM 이름을 문자열로 저장
    private String sub_category; // ENUM 이름을 문자열로 저장
    private String title;
    private String content;
    private String img_url;
    private String instructorNickname; // 강사 닉네임

    /**
     * Lecture 엔티티와 닉네임을 받아 DTO를 생성하는 생성자
     */
    public LectureListResponseDto(Lecture lecture, String instructorNickname) {
        this.lecture_id = lecture.getLecture_id();
        // ENUM을 문자열 이름으로 변환합니다. (예: BEGINNER -> "BEGINNER")
        this.category = lecture.getCategory().name();
        this.sub_category = lecture.getSub_category().name();
        this.title = lecture.getTitle();
        this.content = lecture.getContent();
        this.img_url = lecture.getImg_url();
        this.instructorNickname = instructorNickname;
    }
}