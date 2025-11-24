package com.lms.urbangreen.urbangreenproject.admin.repository;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminUserManageRepository {

    private final JdbcTemplate jdbc;

    /**
     * 검색 조건에 맞는 전체 회원 수
     */
    public int count(String keyword) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM all_users
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                WHERE LOWER(name)     LIKE LOWER(?)
                   OR LOWER(id)       LIKE LOWER(?)
                   OR LOWER(nickname) LIKE LOWER(?)
                   OR LOWER(email)    LIKE LOWER(?)
                """);
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        return jdbc.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    /**
     * 검색 + 페이징된 회원 목록 조회
     *
     * @param page  현재 페이지(1부터 시작)
     * @param size  페이지당 개수
     */
    public List<AdminUserDto> findPage(int page, int size, String keyword) {
        StringBuilder sql = new StringBuilder("""
            SELECT user_id,
                   user_type,
                   id,
                   name,
                   nickname,
                   email,
                   birth
            FROM all_users
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                WHERE LOWER(name)     LIKE LOWER(?)
                   OR LOWER(id)       LIKE LOWER(?)
                   OR LOWER(nickname) LIKE LOWER(?)
                   OR LOWER(email)    LIKE LOWER(?)
                """);
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY user_id DESC ");
        sql.append(" LIMIT ? OFFSET ? ");

        params.add(size);
        params.add((page - 1) * size);

        return jdbc.query(sql.toString(), params.toArray(), this::mapRowToDto);
    }

    /**
     * 선택 회원 삭제
     */
    public void deleteByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;

        String placeholders = String.join(",", userIds.stream().map(id -> "?").toList());
        String sql = "DELETE FROM all_users WHERE user_id IN (" + placeholders + ")";

        jdbc.update(sql, userIds.toArray());
    }

    /**
     * 회원 구분(역할) 변경
     * user_type enum('admin','teacher','student')
     */
    public void updateUserType(Long userId, String userType) {
        String sql = "UPDATE all_users SET user_type = ? WHERE user_id = ?";
        jdbc.update(sql, userType, userId);
    }

    /**
     * ResultSet → AdminUserDto 매핑
     */
    private AdminUserDto mapRowToDto(ResultSet rs, int rowNum) throws SQLException {
        return AdminUserDto.builder()
                .userId(rs.getLong("user_id"))
                .userType(rs.getString("user_type"))  // admin / teacher / student
                .loginId(rs.getString("id"))          // 로그인 아이디
                .name(rs.getString("name"))
                .nickname(rs.getString("nickname"))
                .email(rs.getString("email"))
                .birth(rs.getDate("birth") != null
                        ? rs.getDate("birth").toLocalDate()
                        : null)
                .build();
    }
}
