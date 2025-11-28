package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLectureVideoRepository implements LectureVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLectureVideoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<VideoQuizSummary> videoSummaryRowMapper() {
        return (rs, rowNum) -> {
            VideoQuizSummary dto = new VideoQuizSummary();

            dto.setVideoId(rs.getLong("video_id"));
            dto.setVideoTitle(rs.getString("video_title"));

            int time = rs.getInt("video_time");
            dto.setVideoTime(rs.wasNull() ? null : time);

            dto.setQuizCount(rs.getInt("quiz_count"));

            // ✅ 강의 정보 매핑
            dto.setLectureTitle(rs.getString("lecture_title")); // alias 꼭 일치
            dto.setCategory(rs.getString("category"));
            dto.setSubCategory(rs.getString("sub_category"));

            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            dto.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

            return dto;
        };
    }

    private RowMapper<LectureVideo> lectureVideoRowMapper() {
        return new RowMapper<LectureVideo>() {
            @Override
            public LectureVideo mapRow(ResultSet rs, int rowNum) throws SQLException {
                LectureVideo v = new LectureVideo();
                v.setVideoId(rs.getLong("video_id"));
                v.setLectureId(rs.getLong("lecture_id"));
                v.setVideoTitle(rs.getString("video_title"));
                v.setVideoUrl(rs.getString("video_url"));
                int time = rs.getInt("video_time");
                if (rs.wasNull()) {
                    v.setVideoTime(null);
                } else {
                    v.setVideoTime(time);
                }
                return v;
            }
        };
    }

    @Override
    public List<VideoQuizSummary> findVideoSummariesByLectureId(Long lectureId) {
        String sql =
                "SELECT v.video_id, v.video_title, v.video_time, " +
                        "       (SELECT COUNT(*) FROM quiz q WHERE q.video_id = v.video_id) AS quiz_count, " +
                        "       l.title AS lecture_title, " +
                        "       l.category, l.sub_category, l.created_at " +
                        "FROM lecture_video v " +
                        "JOIN lecture l ON v.lecture_id = l.lecture_id " +
                        "WHERE v.lecture_id = ? " +
                        "ORDER BY v.video_id";

        return jdbcTemplate.query(sql, videoSummaryRowMapper(), lectureId);
    }

    @Override
    public Optional<LectureVideo> findById(Long videoId) {
        String sql =
                "SELECT video_id, lecture_id, video_title, video_url, video_time " +
                        "FROM lecture_video WHERE video_id = ?";

        List<LectureVideo> result = jdbcTemplate.query(sql, lectureVideoRowMapper(), videoId);
        return result.stream().findFirst();
    }

    private RowMapper<Video> videoRowMapper() {
        return (rs, rowNum) -> new Video(
                rs.getInt("video_id"),
                rs.getInt("lecture_id"),
                rs.getString("video_title"),
                rs.getString("video_url"),
                rs.getInt("video_time")
        );
    }

    @Override
    public void save(Video video) {
        // video_time은 현재 클라이언트에서 보내주지 않으므로 기본값 0 또는 별도 로직 필요
        String sql = "INSERT INTO lecture_video (lecture_id, video_title, video_url, video_time) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                video.getLecture_id(),
                video.getVideo_title(),
                video.getVideo_url(),
                video.getVideo_time() // 현재 0으로 저장됨
        );
    }

    @Override
    public List<Video> findByLectureId(int lectureId) {
        String sql = "SELECT * FROM lecture_video WHERE lecture_id = ? ORDER BY video_id ASC";
        return jdbcTemplate.query(sql, videoRowMapper(), lectureId);
    }

    // 비디오 수정
    @Override
    public void update(Video video) {
        String sql = "UPDATE lecture_video SET video_title = ?, video_url = ?, video_time = ? WHERE video_id = ?";

        // video_id를 Long 타입으로 가정하고 업데이트합니다.
        jdbcTemplate.update(sql,
                video.getVideo_title(),
                video.getVideo_url(),
                video.getVideo_time(),
                video.getVideo_id()
        );
    }

    // 비디오 삭제
    @Override
    public void deleteById(Long videoId) {
        String sql = "DELETE FROM lecture_video WHERE video_id = ?";
        jdbcTemplate.update(sql, videoId);
    }


}