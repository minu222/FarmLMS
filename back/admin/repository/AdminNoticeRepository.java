package com.lms.urbangreen.urbangreenproject.admin.repository;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminNoticeListDto;

import java.util.List;

public interface AdminNoticeRepository {

    /**
     * 공지사항 목록 페이지 조회
     */
    List<AdminNoticeListDto> findPage(String keyword, int page, int size);

    /**
     * 검색 조건에 맞는 전체 개수
     */
    int count(String keyword);

    /**
     * 체크된 공지사항들 삭제
     */
    void deleteByIds(List<Long> noticeIds);

    /** 공지 신규 등록 (imgUrl 포함) */
    Long insertNotice(int userId, String title, String content, boolean isPinned, String imgUrl);

    /** 공지 수정 (imgUrl 포함) */
    void updateNotice(Long noticeId, String title, String content, boolean isPinned, String imgUrl);
}