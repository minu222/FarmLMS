package com.lms.urbangreen.urbangreenproject.admin.repository;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminNoticeListDto;
import com.lms.urbangreen.urbangreenproject.admin.repository.AdminNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAdminNoticeRepository implements AdminNoticeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminNoticeListDto> noticeRowMapper = (rs, rowNum) -> {
        AdminNoticeListDto dto = new AdminNoticeListDto();

        long id = rs.getLong("notice_id");
        dto.setNoticeId(id);
        dto.setId(id);

        dto.setTitle(rs.getString("title"));
        dto.setAuthorName(rs.getString("author_name"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            dto.setCreatedAt(ts.toLocalDateTime());
        }

        dto.setViewCount(rs.getInt("view_count"));
        return dto;
    };

    @Override
    public List<AdminNoticeListDto> findPage(String keyword, int page, int size) {
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder(
                "SELECT n.notice_id, n.title, " +
                        "       COALESCE(a.name, '관리자') AS author_name, " +
                        "       n.created_at, n.view_count " +
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
    public void deleteByIds(List<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return;
        }

        String sql = "DELETE FROM notice WHERE notice_id = ?";

        jdbcTemplate.batchUpdate(
                sql,
                noticeIds,
                noticeIds.size(),
                (ps, id) -> ps.setLong(1, id)
        );
    }

    @Override
    public Long insertNotice(int userId, String title, String content, boolean isPinned, String imgUrl) {
        String sql = "INSERT INTO notice (user_id, title, content, img_url, view_count, is_pinned, created_at) " +
                "VALUES (?, ?, ?, ?, 0, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setString(4, imgUrl);
            ps.setBoolean(5, isPinned);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return (key != null) ? key.longValue() : null;
    }

    @Override
    public void updateNotice(Long noticeId, String title, String content, boolean isPinned, String imgUrl) {
        String sql = "UPDATE notice " +
                "SET title = ?, content = ?, img_url = ?, is_pinned = ? " +
                "WHERE notice_id = ?";

        jdbcTemplate.update(sql, title, content, imgUrl, isPinned, noticeId);
    }
}

