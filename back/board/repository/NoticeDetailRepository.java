package com.lms.urbangreen.urbangreenproject.board.repository;

import com.lms.urbangreen.urbangreenproject.board.dto.NoticeDto;

import java.util.List;

public interface NoticeDetailRepository {

    /** 검색 + 페이징 조회 */
    List<NoticeDto> findPage(String keyword, int page, int size);

    /** 검색 조건에 맞는 전체 개수 */
    int count(String keyword);

    /** 필독 공지 목록 (is_pinned = 1 인 것들) */
    List<NoticeDto> findPinned(int limit);

    /** 단건 조회 */
    NoticeDto findById(Long id);

    /** 조회수 +1 */
    void increaseViewCount(Long id);

    /** 삭제 */
    void deleteById(Long id);
}