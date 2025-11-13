package com.lms.urbangreen.urbangreenproject.repository;

import com.lms.urbangreen.urbangreenproject.model.NoticeDetail;
import com.lms.urbangreen.urbangreenproject.model.NoticeListItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
            SELECT n.notice_id AS id,
                   n.title,
                   n.view_count,
                   n.created_at,
                   COALESCE(u.nickname, u.name) AS author_name
            FROM notice n
            JOIN all_users u ON u.user_id = n.user_id
            """);
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sb.append(" WHERE (n.title LIKE ? OR n.content LIKE ?) ");
            String like = "%" + q + "%";
            params.add(like); params.add(like);
        }
        sb.append(" ORDER BY n.created_at DESC, n.notice_id DESC ");
        sb.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add((page - 1) * size);

        return jdbc.query(sb.toString(), (rs, i) -> new NoticeListItem(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author_name"),
                rs.getInt("view_count"),
                toLdt(rs.getTimestamp("created_at"))
        ), params.toArray());
    }

    /* 상세 조회 */
    public NoticeDetail findById(int id) {
        String sql = """
            SELECT n.notice_id AS id, n.user_id, n.title, n.content,
                   n.view_count, n.created_at, n.updated_at,
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
                    rs.getString("author_name")
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
}
