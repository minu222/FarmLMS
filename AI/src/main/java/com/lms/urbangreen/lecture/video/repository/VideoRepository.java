package com.lms.urbangreen.lecture.video.repository;

import com.lms.urbangreen.lecture.video.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper: 데이터베이스 결과를 Video 객체로 매핑
    private final RowMapper<Video> videoRowMapper = (rs, rowNum) -> new Video(
            rs.getInt("video_id"),
            rs.getInt("lecture_id"),
            rs.getString("video_title"),
            rs.getString("video_url"),
            rs.getInt("video_time")
    );

    public List<Video> findByLectureId(int lectureId) {
        String sql = "SELECT video_id, lecture_id, video_title, video_url, video_time " +
                "FROM lecture_video WHERE lecture_id = ? ORDER BY video_id ASC";

        // query 메소드를 사용하여 목록 조회
        return jdbcTemplate.query(sql, videoRowMapper, lectureId);
    }

    public List<Video> findById(int videoId) {
        String sql = "SELECT video_id, lecture_id, video_title, video_url, video_time " +
                "FROM lecture_video WHERE video_id = ?";
        return jdbcTemplate.query(sql, videoRowMapper, videoId);
    }
}
