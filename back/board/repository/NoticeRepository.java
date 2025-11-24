package com.lms.urbangreen.urbangreenproject.board.repository;

import com.lms.urbangreen.urbangreenproject.board.entity.NoticeDetail;
import com.lms.urbangreen.urbangreenproject.board.entity.NoticeListItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NoticeRepository {

    private final JdbcTemplate jdbc;

    public NoticeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* 총 개수 */
    public int count(String q) {
        StringBuilder sb = new StringBuilder("""
            SELECT COUNT(*) FROM notice n
            """);
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sb.append(" WHERE (n.title LIKE ? OR n.content LIKE ?) ");
            String like = "%" + q + "%";
            params.add(like); params.add(like);
        }
        Integer cnt = jdbc.queryForObject(sb.toString(), Integer.class, params.toArray());
        return cnt != null ? cnt : 0;
    }

    /* 페이지 목록 */
    public List<NoticeListItem> findPage(int page, int size, String q) {
        StringBuilder sb = new StringBuilder("""
        SELECT n.notice_id AS id, n.title, n.view_count, n.created_at, n.is_pinned,
               COALESCE(u.nickname, u.name) AS author_name
        FROM notice n
        JOIN all_users u ON u.user_id = n.user_id
        """);
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            // 제목만 검색
            sb.append(" WHERE n.title LIKE ? ");
            String like = "%" + q + "%";
            params.add(like);
        }
        sb.append(" ORDER BY n.created_at DESC, n.notice_id DESC ");
        sb.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add((page - 1) * size);

        return jdbc.query(sb.toString(), (rs, i) -> new NoticeListItem(
                rs.getInt("id"),              // int id
                rs.getString("title"),         // String title
                rs.getString("author_name"),   // String authorName ⭐ 순서 수정
                rs.getInt("view_count"),       // int viewCount ⭐ 순서 수정
                toLdt(rs.getTimestamp("created_at")),  // LocalDateTime createdAt
                rs.getInt("is_pinned") == 1    // boolean isPinned ⭐ 추가
        ), params.toArray());
    }

    /* 고정 공지 조회 (최대 2개) */
    public List<NoticeListItem> findPinnedNotices() {
        String sql = """
        SELECT n.notice_id AS id, n.title, n.view_count, n.created_at, n.is_pinned,
               COALESCE(u.nickname, u.name) AS author_name
        FROM notice n
        JOIN all_users u ON u.user_id = n.user_id
        WHERE n.is_pinned = 1
        ORDER BY n.created_at DESC
        LIMIT 2
        """;

        return jdbc.query(sql, (rs, i) -> new NoticeListItem(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author_name"),
                rs.getInt("view_count"),
                toLdt(rs.getTimestamp("created_at")),
                true  // ⭐ 6번째 파라미터
        ));
    }


    /* 상세 조회 */
    public NoticeDetail findById(int id) {
        String sql = """
        SELECT n.notice_id AS id, n.user_id, n.title, n.content,
               n.view_count, n.created_at, n.updated_at, n.is_pinned,
               COALESCE(u.nickname, u.name) AS author_name
        FROM notice n
        JOIN all_users u ON u.user_id = n.user_id
        WHERE n.notice_id = ?
        """;
        try {
            return jdbc.queryForObject(sql, (rs, i) -> new NoticeDetail(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getInt("view_count"),
                    toLdt(rs.getTimestamp("created_at")),
                    toLdt(rs.getTimestamp("updated_at")),
                    rs.getString("author_name"),
                    rs.getInt("is_pinned") == 1  // TINYINT를 boolean으로 변환
            ), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* 조회수 +1 */
    public void increaseViewCount(int id) {
        jdbc.update("UPDATE notice SET view_count = view_count + 1 WHERE notice_id = ?", id);
    }

    private static java.time.LocalDateTime toLdt(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        int affected = jdbc.update(sql, id);
        return affected > 0;
    }

    /* ========= 여기부터 관리자용 INSERT / UPDATE 추가 ========= */

    /** 공지 등록 (notice_id 반환) */
    public int insert(int userId, String title, String content, boolean isPinned) {
        String sql = "INSERT INTO notice (user_id, title, content, is_pinned) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setInt(4, isPinned ? 1 : 0);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("공지사항 PK 생성 실패");
        }
        return key.intValue();
    }

    /** 공지 수정 */
    public int update(int id, String title, String content, boolean isPinned) {
        String sql = """
                UPDATE notice
                SET title = ?, content = ?, is_pinned = ?, updated_at = CURRENT_TIMESTAMP
                WHERE notice_id = ?
                """;
        return jdbc.update(sql, title, content, isPinned ? 1 : 0, id);
    }
}