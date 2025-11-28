package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.teacher.domain.Lecture;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Lecture> findById(int lectureId) {
        String sql =
                "SELECT lecture_id, user_id, category, sub_category, img_url, " +
                        "       title, content, subs_count, created_at " +
                        "FROM lecture " +
                        "WHERE lecture_id = ?";

        List<Lecture> result = jdbcTemplate.query(sql, lectureRowMapper(), lectureId);
        return result.stream().findFirst();
    }


    @Override
    public int save(Lecture lecture) {
        String sql =
                "INSERT INTO lecture (user_id, category, sub_category, img_url, title, content, subs_count) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, lecture.getUserId());
            ps.setString(2, lecture.getCategory());
            ps.setString(3, lecture.getSubCategory());
            ps.setString(4, lecture.getImgUrl());
            ps.setString(5, lecture.getTitle());
            ps.setString(6, lecture.getContent());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        int generatedId = (key != null) ? key.intValue() : 0;
        lecture.setLectureId(generatedId);
        return generatedId;
    }

    // ✅ 수정용
    @Override
    public void update(Lecture lecture) {
        String sql = "UPDATE lecture SET category = ?, sub_category = ?, title = ?, content = ?, img_url = ? WHERE lecture_id = ?";

        jdbcTemplate.update(sql,
                lecture.getCategory(),
                lecture.getSubCategory(),
                lecture.getTitle(),
                lecture.getContent(),
                lecture.getImgUrl(),
                lecture.getLectureId()
        );
    }

    @Override
    public void deleteByIdAndTeacherId(int lectureId, int teacherId) {
        String sql = "DELETE FROM lecture WHERE lecture_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, lectureId, teacherId);
    }
}