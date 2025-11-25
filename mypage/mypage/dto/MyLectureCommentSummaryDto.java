package com.lms.urbangreen.urbangreenproject.mypage.dto;

import lombok.Data;

@Data
public class MyLectureCommentSummaryDto {

    /** lecture.lecture_id */
    private int id;

    /** 강의 제목 */
    private String title;

    /** 강사 이름 (all_users.name) */
    private String instructor;

    /** 메인 카테고리 (lecture.category) */
    private String category;

    /** 서브 카테고리 (lecture.sub_category) -> 프론트에서 level 비슷하게 사용 */
    private String level;

    /** 공개 상태 (필요 없으면 null 사용) */
    private String status;

    /** 강의 요약 (lecture.content) */
    private String description;

    /** 내가 작성한 댓글 수 */
    private int commentCount;
}