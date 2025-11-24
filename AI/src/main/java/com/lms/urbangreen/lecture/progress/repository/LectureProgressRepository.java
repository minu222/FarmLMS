package com.lms.urbangreen.lecture.progress.repository;

import com.lms.urbangreen.lecture.progress.entity.LectureProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureProgressRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper 구현
    private final RowMapper<LectureProgress> rowMapper = (rs, rowNum) -> {
        LectureProgress progress = new LectureProgress();
        progress.setProgress_id(rs.getInt("progress_id"));
        progress.setLecture_id(rs.getInt("lecture_id"));
        progress.setUser_id(rs.getInt("user_id"));
        // TIMESTAMP to LocalDateTime
        if (rs.getTimestamp("valid_until") != null) {
            progress.setValid_until(rs.getTimestamp("valid_until").toLocalDateTime());
        }
        progress.setProgress(rs.getBigDecimal("progress"));
        if (rs.getTimestamp("updated_at") != null) {
            progress.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return progress;
    };

    /**
     * 특정 강의(lectureId)와 사용자(userId)에 대한 진도율 레코드를 조회합니다.
     */
    public Optional<LectureProgress> findByLectureIdAndUserId(int lectureId, int userId) {
        String sql = "SELECT * FROM lecture_progress WHERE lecture_id = ? AND user_id = ?";
        List<LectureProgress> results = jdbcTemplate.query(sql, rowMapper, lectureId, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * 새로운 강의 진도율 레코드를 저장하고 생성된 progress_id를 반환합니다.
     */
    public int insert(LectureProgress progress) {
        String sql = "INSERT INTO lecture_progress (lecture_id, user_id, valid_until, progress) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // updated_at은 DB에서 CURRENT_TIMESTAMP로 자동 처리됩니다.
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, progress.getLecture_id());
            ps.setInt(2, progress.getUser_id());
            // valid_until이 null일 수 있음
            ps.setTimestamp(3, progress.getValid_until() != null ?
                    java.sql.Timestamp.valueOf(progress.getValid_until()) : null);
            ps.setBigDecimal(4, progress.getProgress() != null ?
                    progress.getProgress() : new BigDecimal("0.00")); // 기본값 0.00
            return ps;
        }, keyHolder);

        // 생성된 progress_id 반환
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    /**
     * 기존 강의 진도율 레코드를 업데이트합니다.
     * (여기서는 progress 필드만 업데이트하며, valid_until, updated_at은 DB에서 처리될 수 있습니다.)
     */
    public void updateProgress(int progressId, BigDecimal progressValue) {
        String sql = "UPDATE lecture_progress SET progress = ? WHERE progress_id = ?";
        jdbcTemplate.update(sql, progressValue, progressId);
    }
}