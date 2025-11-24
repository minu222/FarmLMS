package com.lms.urbangreen.urbangreenproject.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminNoticeListDto {

    /** 체크박스용 PK (notice.notice_id) */
    private Long noticeId;

    /** 상세 페이지 링크용 ID (지금은 notice_id와 동일하게 사용) */
    private Long id;

    private String title;

    /** 작성자 이름 (지금은 공지라 '관리자' 고정) */
    private String authorName;

    private LocalDateTime createdAt;

    private int viewCount;
}