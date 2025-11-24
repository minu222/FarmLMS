package com.lms.urbangreen.urbangreenproject.board.service;

import com.lms.urbangreen.urbangreenproject.board.dto.NoticeDto;
import com.lms.urbangreen.urbangreenproject.board.repository.NoticeDetailRepository;
import com.lms.urbangreen.urbangreenproject.board.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeDetailService {

    private final NoticeDetailRepository noticeDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> getNoticePage(String keyword, int page, int size) {
        return noticeDetailRepository.findPage(keyword, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCount(String keyword) {
        return noticeDetailRepository.count(keyword);
    }

    @Override
    public List<NoticeDto> getPinnedNotices(int limit) {
        return noticeDetailRepository.findPinned(limit);
    }

    @Override
    @Transactional
    public NoticeDto getNotice(Long id) {
        try {
            // 조회수 1 증가
            noticeDetailRepository.increaseViewCount(id);
            return noticeDetailRepository.findById(id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteNotice(Long id) {
        noticeDetailRepository.deleteById(id);
    }
}