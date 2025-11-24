package com.lms.urbangreen.urbangreenproject.mypage.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcMypageAccountRepository implements MypageAccountRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteUser(int userId) {
        // ⚠️ 여기서 테이블/컬럼 이름은 실제 DB에 맞게 변경 필요
        // 예시: users 테이블, user_type 컬럼이 있고 admin 방어까지 같이 함
        String sql = "DELETE FROM all_users " +
                "WHERE user_id = ? " +
                "  AND user_type <> 'admin'";

        jdbcTemplate.update(sql, userId);
    }
}
