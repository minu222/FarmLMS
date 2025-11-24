package com.lms.urbangreen.urbangreenproject.board.dto;


import java.time.LocalDateTime;

public class NoticeDto {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private int viewCount;

    // 🔥 DB is_pinned 컬럼과 대응
    private boolean isPinned;
    private String imgUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    // 🔥 Thymeleaf에서 ${notice.isPinned} 로 접근하게 하려고 getIsPinned() 사용
    public boolean getIsPinned() { return isPinned; }
    public void setIsPinned(boolean isPinned) { this.isPinned = isPinned; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
}
