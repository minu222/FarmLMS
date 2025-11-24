package com.lms.urbangreen.urbangreenproject.board.service;

import com.lms.urbangreen.urbangreenproject.board.dto.NoticeDto;

import java.util.List;

public interface NoticeDetailService {

    List<NoticeDto> getNoticePage(String keyword, int page, int size);

    int getTotalCount(String keyword);

    List<NoticeDto> getPinnedNotices(int limit);

    NoticeDto getNotice(Long id);

    void deleteNotice(Long id);
}