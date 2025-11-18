// src/main/java/com/lms/urbangreen/urbangreenproject/chat/repository/AllUsersRepository.java
package com.lms.urbangreen.urbangreenproject.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AllUsersRepository {

    private final JdbcTemplate jdbcTemplate;

    public String findNicknameByUserId(Integer userId) {
        String sql = """
            SELECT COALESCE(nickname, name) AS nickname
            FROM all_users
            WHERE user_id = ?
            """;
        return jdbcTemplate.queryForObject(sql, String.class, userId);
    }
}
