package com.lms.urbangreen.urbangreenproject.lecture.repository;


import com.lms.urbangreen.urbangreenproject.lecture.entity.LectureSub;
import com.lms.urbangreen.urbangreenproject.lecture.entity.MySubscriptionLectureDto;
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

    // 구독 취소
    public int deleteByUserIdAndLectureId(int userId, int lectureId) {
        String sql = "DELETE FROM lecture_sub WHERE user_id = ? AND lecture_id = ?";
        return jdbcTemplate.update(sql, userId, lectureId);
    }

    // 특정 사용자의 구독한 강의 목록과 진도율 조회
    public List<MySubscriptionLectureDto> findAllSubscribedLecturesWithProgress(int userId) {
        String sql = """
            SELECT 
                l.lecture_id, 
                l.category, 
                l.sub_category, 
                l.title, 
                l.content, 
                l.img_url, 
                u.nickname AS instructorNickname,
                COALESCE(lp.progress, 0.00) AS progress -- 진도율이 없으면 0으로 처리
            FROM 
                lecture_sub ls
            JOIN 
                lecture l ON ls.lecture_id = l.lecture_id
            JOIN 
                all_users u ON l.user_id = u.user_id
            LEFT JOIN 
                lecture_progress lp ON l.lecture_id = lp.lecture_id AND lp.user_id = ? -- 사용자 ID로 진도율 매칭
            WHERE 
                ls.user_id = ?
            ORDER BY 
                ls.sub_id DESC -- 최근 구독한 순서 등 원하는 정렬
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MySubscriptionLectureDto(
                rs.getInt("lecture_id"),
                rs.getString("category"),
                rs.getString("sub_category"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("img_url"),
                rs.getString("instructorNickname"),
                rs.getBigDecimal("progress")
        ), userId, userId); // 파라미터: JOIN 조건용 userId, WHERE 조건용 userId
    }
}