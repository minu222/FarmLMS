package com.lms.urbangreen.urbangreenproject.admin.dto;

import lombok.Data;

@Data
public class LectureSummaryDto {
    private Long id;            // lecture.lecture_id
    private String title;       // lecture.title
    private String instructor;  // 강사 이름 (all_users.name)
    private String category;    // gardening/field/house
    private String level;       // seed/grow/ship (레벨 대신 사용)
    private String status;      // 공개 상태 (일단 "공개"로 고정)
    private String description; // lecture.content
    private int commentCount;   // 해당 강의 QnA 질문 수
}