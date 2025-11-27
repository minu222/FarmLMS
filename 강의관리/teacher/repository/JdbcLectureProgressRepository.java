package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureProgressView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class JdbcLectureProgressRepository implements LectureProgressRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLectureProgressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<LectureProgressView> rowMapper() {
        return new RowMapper<LectureProgressView>() {
            @Override
            public LectureProgressView mapRow(ResultSet rs, int rowNum) throws SQLException {
                LectureProgressView v = new LectureProgressView();
                v.setProgressId(rs.getInt("progress_id"));
                v.setLectureId(rs.getInt("lecture_id"));
                v.setUserId(rs.getInt("user_id"));
                v.setStudentName(rs.getString("student_name"));
                v.setProgress(rs.getDouble("progress"));   // 0.00 ~ 1.00

                Timestamp ts = rs.getTimestamp("updated_at");
                if (ts != null) {
                    v.setUpdatedAt(ts.toLocalDateTime());
                }
                return v;
            }
        };
    }

    @Override
    public List<LectureProgressView> findByLectureId(int lectureId) {
        String sql =
                "SELECT p.progress_id, p.lecture_id, p.user_id, " +
                        "       p.progress, p.updated_at, " +
                        "       u.name AS student_name " +    // ✅ all_users 의 이름 컬럼명에 맞게 수정 필요 (name / user_name 등)
                        "FROM lecture_progress p " +
                        "JOIN all_users u ON p.user_id = u.user_id " +
                        "WHERE p.lecture_id = ? " +
                        "ORDER BY u.name ASC";

        return jdbcTemplate.query(sql, rowMapper(), lectureId);
    }
}
