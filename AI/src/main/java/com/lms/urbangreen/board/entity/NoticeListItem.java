package com.lms.urbangreen.board.entity;

import java.time.LocalDateTime;

public record NoticeListItem(
        int id,
        String title,
        String authorName,
        int viewCount,
        LocalDateTime createdAt
) {}
