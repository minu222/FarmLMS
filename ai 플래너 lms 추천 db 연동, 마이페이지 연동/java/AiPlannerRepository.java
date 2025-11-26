package com.lms.urbangreen.urbangreenproject.ai.suggestAI.repository;

import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.AiPlanner;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    // AI가 호출할 강의 검색 메서드
    public List<Map<String, Object>> searchLectures(String keyword, String category) {
        // 검색어와 카테고리를 이용해 관련 강의를 찾습니다 (최대 5개 제한)
        String sql = "SELECT l.lecture_id, l.title, l.category, l.sub_category, u.nickname AS instructor_nickname " +
                "FROM lecture l " +
                "JOIN all_users u ON l.user_id = u.user_id " + // user_id로 JOIN
                "WHERE (l.title LIKE ? OR l.content LIKE ?) ";

        // 카테고리가 명확하면 필터링 (선택 사항)
        if (category != null && !category.isEmpty() && !category.equals("ALL")) {
            sql += "AND l.category = '" + category + "' "; // 카테고리 필터링 시에도 별칭 사용
        }

        sql += "ORDER BY l.lecture_id DESC LIMIT 5";

        String searchPattern = "%" + keyword + "%";

        // 결과를 Map 리스트로 반환 (JSON 변환 용이)
        // 쿼리 파라미터는 keyword에 대한 두 개의 LIKE 절에 바인딩됩니다.
        return jdbcTemplate.queryForList(sql, searchPattern, searchPattern);
    }

    // 플래너 삭제
    public int deletePlanner(Long plannerId, String userId) {
        // ⭐ 중요: 해당 플래너가 요청한 사용자의 것인지 반드시 확인하고 삭제해야 합니다.
        String sql = "DELETE FROM ai_planner WHERE planner_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, plannerId, userId);
    }



}