package com.lms.urbangreen.urbangreenproject.lecture.repository;


import com.lms.urbangreen.urbangreenproject.lecture.entity.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRepo {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Lecture> lectureRowMapper() {
        return (rs, rowNum) -> {
            Lecture lecture = new Lecture();
            lecture.setLecture_id(rs.getInt("lecture_id"));
            lecture.setUser_id(rs.getInt("user_id"));
            // Enum 타입 매핑
            String categoryString = rs.getString("category");

            if (categoryString == null) {
                categoryString = "ETC";
            }

            Lecture.category category = Lecture.category.valueOf(categoryString.toUpperCase());
            lecture.setCategory(category);
            String sub_categoryString = rs.getString("sub_category");

            if (sub_categoryString == null) {
                sub_categoryString = "ETC";
            }

            Lecture.sub_category sub_category = Lecture.sub_category.valueOf(sub_categoryString.toUpperCase());
            lecture.setSub_category(sub_category);
            lecture.setImg_url(rs.getString("img_url"));
            lecture.setTitle(rs.getString("title"));
            lecture.setContent(rs.getString("content"));
            lecture.setSubs_count(rs.getInt("subs_count"));
            lecture.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
            return lecture;
        };
    }

    public List<Lecture> findAll() {
        String sql = "SELECT * FROM lecture ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, lectureRowMapper());
    }

    public Optional<Lecture> findById(int id) {
        String sql = "SELECT * FROM lecture WHERE lecture_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lectureRowMapper(), id));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 사용자가 구독한 강의 표시
    public List<Lecture> findSubscribedLecturesByUserId(int userId) {
        String sql = "SELECT l.*, " +
                "(SELECT COUNT(*) FROM lecture_sub ls2 WHERE ls2.lecture_id = l.lecture_id) AS subs_count " +
                "FROM lecture l " +
                "JOIN lecture_sub ls ON l.lecture_id = ls.lecture_id " +
                "WHERE ls.user_id = ? " +
                "ORDER BY ls.sub_id DESC"; // 최근 구독한 강의 순으로 정렬

        // 주의: 위 SQL의 `l.*`는 `Lecture` 엔티티의 모든 필드를 DB에서 올바른 이름으로 매핑한다고 가정합니다.

        return jdbcTemplate.query(sql, lectureRowMapper(), userId);
    }
}
