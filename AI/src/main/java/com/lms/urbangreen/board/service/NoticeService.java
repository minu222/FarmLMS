package com.lms.urbangreen.board.service;

import com.lms.urbangreen.board.entity.NoticeDetail;
import com.lms.urbangreen.board.entity.NoticeListItem;
import com.lms.urbangreen.board.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeService {

    public record PageResult<T>(List<T> items, int total, int page, int size) {}

    private final NoticeRepository repo;

    public NoticeService(NoticeRepository repo) {
        this.repo = repo;
    }

    public PageResult<NoticeListItem> getPage(int page, int size, String q) {
        int total = repo.count(q);
        var items = repo.findPage(page, size, q);
        return new PageResult<>(items, total, page, size);
    }

    @Transactional
    public NoticeDetail getDetailAndIncreaseView(int id) {
        repo.increaseViewCount(id);
        return repo.findById(id);
    }
}
