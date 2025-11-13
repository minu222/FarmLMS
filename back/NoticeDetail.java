package com.lms.urbangreen.urbangreenproject.model;

import java.time.LocalDateTime;

public record NoticeDetail(
        int id,
        int userId,
        String title,
        String content,
        int viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String authorName
) {}
