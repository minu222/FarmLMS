package com.lms.urbangreen.urbangreenproject.teacher.quiz.dto;

import java.time.LocalDateTime;

public class VideoQuizSummary {

    private Long videoId;
    private String videoTitle;
    private Integer videoTime;   // 초
    private Integer quizCount;   // 해당 영상에 등록된 퀴즈 문항 수

    // ✅ 강의 정보
    private String lectureTitle;     // lecture.title
    private String category;         // lecture.category
    private String subCategory;      // lecture.sub_category
    private LocalDateTime createdAt; // lecture.created_at

    // --- getter / setter ---

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public Integer getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(Integer videoTime) {
        this.videoTime = videoTime;
    }

    public Integer getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(Integer quizCount) {
        this.quizCount = quizCount;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}