package com.lms.urbangreen.urbangreenproject.teacher.domain;

import java.time.LocalDateTime;

public class LectureProgressView {

    private int progressId;
    private int lectureId;
    private int userId;
    private String studentName;   // all_users.name 이라고 가정 (컬럼명 다르면 여기 SQL만 바꾸면 됨)
    private Double progress;      // 0.0 ~ 1.0
    private LocalDateTime updatedAt;

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
