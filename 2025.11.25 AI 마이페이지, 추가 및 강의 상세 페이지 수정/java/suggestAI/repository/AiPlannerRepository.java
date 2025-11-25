package com.lms.urbangreen.urbangreenproject.ai.suggestAI.repository;

import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.AiPlanner;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AiPlannerRepository {

    private final JdbcTemplate jdbcTemplate; // JDBC Template 주입

    // 매핑
    private final RowMapper<AiPlanner> aiPlannerRowMapper = (rs, rowNum) -> AiPlanner.builder()
            .plannerId(rs.getLong("planner_id"))
            .userId(rs.getString("user_id"))
            .plannerName(rs.getString("planner_name"))
            .plannerContent(rs.getString("planner_content"))
            // created_at 필드가 있다면 여기에 추가
            .build();

    /**
     * AI 플래너 결과를 DB에 저장합니다.
     * @param aiPlanner 저장할 플래너 객체 (userId, plannerName, plannerContent 포함)
     */
    public void savePlanner(AiPlanner aiPlanner) {
        //
        String sql = "INSERT INTO ai_planner (user_id, planner_name, planner_content, created_at) " +
                "VALUES (?, ?, ?, NOW())";

        // JdbcTemplate.update()를 사용하여 데이터 삽입
        jdbcTemplate.update(
                sql,
                aiPlanner.getUserId(),
                aiPlanner.getPlannerName(),
                aiPlanner.getPlannerContent()
        );
    }

    // ai플래너 목록 조회
    public List<AiPlanner> findAllByUserId(String userId) {
        String sql = "SELECT planner_id, user_id, planner_name, planner_content FROM ai_planner WHERE user_id = ?";
        return jdbcTemplate.query(sql, aiPlannerRowMapper, userId);
    }



}