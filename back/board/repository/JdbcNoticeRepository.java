package com.lms.urbangreen.urbangreenproject.board.repository;

import com.lms.urbangreen.urbangreenproject.board.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcNoticeRepository implements NoticeDetailRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<NoticeDto> noticeRowMapper = (rs, rowNum) -> {
        NoticeDto dto = new NoticeDto();

        dto.setId(rs.getLong("notice_id"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setViewCount(rs.getInt("view_count"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            dto.setCreatedAt(ts.toLocalDateTime());
        }

        dto.setAuthorName(rs.getString("author_name"));

        // 🔥 DB의 is_pinned 컬럼 → DTO로 매핑
        dto.setIsPinned(rs.getBoolean("is_pinned"));

        dto.setImgUrl(rs.getString("img_url"));

        return dto;
    };

    @Override
    public List<NoticeDto> findPage(String keyword, int page, int size) {
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder(
                "SELECT n.notice_id, n.title, n.content, n.view_count, n.created_at, " +
                        "       n.is_pinned, n.img_url, " +
                        "       COALESCE(a.name, '관리자') AS author_name " +
                        "FROM notice n " +
                        "LEFT JOIN all_users a ON n.user_id = a.user_id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim() + "%";
            sql.append("AND (n.title LIKE ? OR n.content LIKE ? OR a.name LIKE ?) ");
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append("ORDER BY n.notice_id DESC ");
        sql.append("LIMIT ? OFFSET ?");

        params.add(size);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), noticeRowMapper, params.toArray());
    }

    @Override
    public int count(String keyword) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) " +
                        "FROM notice n " +
                        "LEFT JOIN all_users a ON n.user_id = a.user_id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim() + "%";
            sql.append("AND (n.title LIKE ? OR n.content LIKE ? OR a.name LIKE ?) ");
            params.add(like);
            params.add(like);
            params.add(like);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return (count != null) ? count : 0;
    }

    @Override
    public List<NoticeDto> findPinned(int limit) {
        String sql =
                "SELECT n.notice_id, n.title, n.content, n.view_count, n.created_at, " +
                        "       n.is_pinned, n.img_url, " +
                        "       COALESCE(a.name, '관리자') AS author_name " +
                        "FROM notice n " +
                        "LEFT JOIN all_users a ON n.user_id = a.user_id " +
                        "WHERE n.is_pinned = 1 " +
                        "ORDER BY n.notice_id DESC " +
                        "LIMIT ?";

        return jdbcTemplate.query(sql, noticeRowMapper, limit);
    }

    @Override
    public NoticeDto findById(Long id) {
        String sql =
                "SELECT n.notice_id, n.title, n.content, n.view_count, n.created_at, " +
                        "       n.is_pinned, n.img_url, " +
                        "       COALESCE(a.name, '관리자') AS author_name " +
                        "FROM notice n " +
                        "LEFT JOIN all_users a ON n.user_id = a.user_id " +
                        "WHERE n.notice_id = ?";

        return jdbcTemplate.queryForObject(sql, noticeRowMapper, id);
    }

    @Override
    public void increaseViewCount(Long id) {
        String sql = "UPDATE notice SET view_count = view_count + 1 WHERE notice_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        jdbcTemplate.update(sql, id);
    }
}