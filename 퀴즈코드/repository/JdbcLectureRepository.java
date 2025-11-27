package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcLectureRepository implements LectureRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLectureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Lecture> lectureRowMapper() {
        return new RowMapper<Lecture>() {
            @Override
            public Lecture mapRow(ResultSet rs, int rowNum) throws SQLException {
                Lecture lecture = new Lecture();
                lecture.setLectureId(rs.getInt("lecture_id"));
                lecture.setUserId(rs.getInt("user_id"));
                lecture.setCategory(rs.getString("category"));
                lecture.setSubCategory(rs.getString("sub_category"));
                lecture.setImgUrl(rs.getString("img_url"));
                lecture.setTitle(rs.getString("title"));
                lecture.setContent(rs.getString("content"));
                lecture.setSubsCount(rs.getInt("subs_count"));
                if (rs.getTimestamp("created_at") != null) {
                    lecture.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                return lecture;
            }
        };
    }

    @Override
    public List<Lecture> findByTeacherId(int teacherId) {
        String sql =
                "SELECT lecture_id, user_id, category, sub_category, img_url, " +
                        "       title, content, subs_count, created_at " +
                        "FROM lecture " +
                        "WHERE user_id = ? " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, lectureRowMapper(), teacherId);
    }
}