package com.lms.urbangreen.urbangreenproject.lecture.entity;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LectureDetailResponseDto {
    private int lecture_id;
    private int user_id;
    private Lecture.category category;
    private Lecture.sub_category sub_category;
    private String img_url;
    private String title;
    private String content;
    private int subs_count;
    private LocalDateTime created_at;

    // ⭐ 핵심: 강사 닉네임 필드
    private String instructorNickname;

    public static LectureDetailResponseDto from(Lecture lecture, String instructorNickname) {
        return LectureDetailResponseDto.builder()
                .lecture_id(lecture.getLecture_id())
                .user_id(lecture.getUser_id())
                .category(lecture.getCategory())
                .sub_category(lecture.getSub_category())
                .img_url(lecture.getImg_url())
                .title(lecture.getTitle())
                .content(lecture.getContent())
                .subs_count(lecture.getSubs_count())
                .created_at(lecture.getCreated_at())
                .instructorNickname(instructorNickname)
                .build();
    }
}