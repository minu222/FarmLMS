package com.lms.urbangreen.urbangreenproject.teacher.dto;

import lombok.Data;

@Data
public class TeacherLectureCommentsSummaryDto {

    /** lecture.lecture_id */
    private int id;

    /** 강의 제목 */
    private String title;

    /** 강사 이름 (all_users.name) */
    private String instructor;

    /** 메인 카테고리 (lecture.category) */
    private String category;

    /** 서브 카테고리 (lecture.sub_category) -> 레벨 비슷하게 사용 */
    private String level;

    /** 공개 상태 (DB에 없으니 임의로 "공개" 등 사용) */
    private String status;

    /** 강의 요약 (lecture.content) */
    private String description;

    /** 이 강의에 달린 질문 개수 (p_qna_id IS NULL) */
    private int commentCount;
}