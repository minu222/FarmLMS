package com.lms.urbangreen.lecture.progress.repository;

import com.lms.urbangreen.lecture.progress.entity.LectureVideoProgress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class LectureVideoProgressRepository {

    private final JdbcTemplate jdbcTemplate;

    public LectureVideoProgressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 구현
    private final RowMapper<LectureVideoProgress> rowMapper = new RowMapper<LectureVideoProgress>() {
        @Override
        public LectureVideoProgress mapRow(ResultSet rs, int rowNum) throws SQLException {
            LectureVideoProgress progress = new LectureVideoProgress();
            progress.setVideo_progress_id(rs.getInt("video_progress_id"));
            progress.setProgress_id(rs.getInt("progress_id"));
            progress.setVideo_id(rs.getInt("video_id"));
            progress.setUser_id(rs.getInt("user_id"));
            progress.setWatched_time(rs.getInt("watched_time"));
            progress.setLast_position(rs.getInt("last_position"));
            // DATETIME 타입 처리
            progress.setWatched_at(rs.getTimestamp("watched_at").toLocalDateTime());
            progress.setProgress(rs.getBigDecimal("progress"));
            // completed_at이 NULL일 수 있으므로 Optional 처리
            java.sql.Timestamp completedAtTimestamp = rs.getTimestamp("completed_at");
            if (completedAtTimestamp != null) {
                progress.setCompleted_at(completedAtTimestamp.toLocalDateTime());
            }

            return progress;
        }
    };

    /**
     * 특정 유저의 특정 비디오에 대한 진도율 정보를 조회합니다.
     */
    public Optional<LectureVideoProgress> findByVideoIdAndUserId(int videoId, int userId) {
        String sql = "SELECT * FROM lecture_video_progress WHERE video_id = ? AND user_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, videoId, userId));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 비디오 진도율을 저장하거나 업데이트합니다 (UPSERT 로직).
     * @param videoProgress 저장할 비디오 진도율 엔티티
     * @return 저장된/업데이트된 엔티티
     */
    public LectureVideoProgress save(LectureVideoProgress videoProgress) {
        // 기존 데이터 조회
        Optional<LectureVideoProgress> existingProgress = findByVideoIdAndUserId(
                videoProgress.getVideo_id(), videoProgress.getUser_id());

        if (existingProgress.isPresent()) {
            // UPDATE: 기존 진도율이 있을 경우 업데이트
            return update(videoProgress, existingProgress.get().getVideo_progress_id());
        } else {
            // INSERT: 기존 진도율이 없을 경우 삽입
            return insert(videoProgress);
        }
    }

    // INSERT (진도율 신규 생성)
    private LectureVideoProgress insert(LectureVideoProgress progress) {
        String sql = "INSERT INTO lecture_video_progress " +
                "(progress_id, video_id, user_id, watched_time, last_position, watched_at, progress, completed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // completed_at 처리 (null 허용)
        java.sql.Timestamp completedAt = progress.getCompleted_at() != null ?
                java.sql.Timestamp.valueOf(progress.getCompleted_at()) : null;

        jdbcTemplate.update(sql,
                progress.getProgress_id(),
                progress.getVideo_id(),
                progress.getUser_id(),
                progress.getWatched_time(),
                progress.getLast_position(),
                java.sql.Timestamp.valueOf(progress.getWatched_at()),
                progress.getProgress(),
                completedAt);

        // 새로 삽입된 레코드의 PK를 가져오는 로직이 필요하지만, 현재는 SimpleJdbcInsert를 사용하지 않으므로 생략하고
        // findByVideoIdAndUserId를 다시 호출하는 것으로 대체합니다. (실제 운영에서는 SimpleJdbcInsert 사용 권장)
        return findByVideoIdAndUserId(progress.getVideo_id(), progress.getUser_id()).orElse(progress);
    }

    // UPDATE (진도율 갱신)
    private LectureVideoProgress update(LectureVideoProgress progress, int progressId) {
        String sql = "UPDATE lecture_video_progress SET " +
                "watched_time = ?, last_position = ?, watched_at = ?, progress = ?, completed_at = ? " +
                "WHERE video_progress_id = ?";

        // completed_at 처리 (null 허용)
        java.sql.Timestamp completedAt = progress.getCompleted_at() != null ?
                java.sql.Timestamp.valueOf(progress.getCompleted_at()) : null;

        jdbcTemplate.update(sql,
                progress.getWatched_time(),
                progress.getLast_position(),
                java.sql.Timestamp.valueOf(progress.getWatched_at()),
                progress.getProgress(),
                completedAt,
                progressId);

        // PK 설정 (업데이트된 레코드의 ID)
        progress.setVideo_progress_id(progressId);
        return progress;
    }

}