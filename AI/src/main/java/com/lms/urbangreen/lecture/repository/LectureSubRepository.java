package com.lms.urbangreen.lecture.repository;

import com.lms.urbangreen.lecture.entity.LectureSub;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LectureSubRepository {

    private final JdbcTemplate jdbcTemplate;

    public LectureSubRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 정의
    private final RowMapper<LectureSub> rowMapper = (rs, rowNum) -> {
        LectureSub sub = new LectureSub();
        sub.setSubId(rs.getInt("sub_id"));
        sub.setUserId(rs.getInt("user_id"));
        sub.setLectureId(rs.getInt("lecture_id"));
        return sub;
    };

    /**
     * 강의 구독을 추가합니다.
     */
    public void save(LectureSub lectureSub) {
        String sql = "INSERT INTO lecture_sub (user_id, lecture_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, lectureSub.getUserId(), lectureSub.getLectureId());
    }

    /**
     * 특정 사용자의 특정 강의 구독 여부를 확인합니다.
     * 구독 정보가 있다면 LectureSub 객체를 반환합니다.
     */
    public Optional<LectureSub> findByUserIdAndLectureId(int userId, int lectureId) {
        String sql = "SELECT sub_id, user_id, lecture_id FROM lecture_sub WHERE user_id = ? AND lecture_id = ?";
        try {
            // queryForObject는 결과가 없으면 EmptyResultDataAccessException 발생
            LectureSub sub = jdbcTemplate.queryForObject(sql, rowMapper, userId, lectureId);
            return Optional.ofNullable(sub);
        } catch (Exception e) {
            return Optional.empty(); // 구독 정보가 없는 경우
        }
    }

     // 특정 사용자가 구독한 모든 강의 ID 리스트
    public List<Integer> findSubscribedLectureIdsByUserId(int userId) {
        String sql = "SELECT lecture_id FROM lecture_sub WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }
}