package com.lms.urbangreen.user.repository;

import com.lms.urbangreen.user.entity.User;
import com.lms.urbangreen.user.entity.UserType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getInt("user_id"),
            UserType.valueOf(rs.getString("user_type")),
            rs.getString("id"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("nickname"),
            rs.getDate("birth") != null ? rs.getDate("birth").toLocalDate() : null,
            rs.getString("email"),
            rs.getString("intro")
    );



    public void save(User user) {
        // userType이 null일 경우 기본값 STUDENT 적용
        if (user.getUserType() == null) {
            user.setUserType(UserType.student);
        }

        String sql = "INSERT INTO all_users (user_type, id, password, name, nickname, birth, email, intro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getUserType().name(), // Enum → 문자열 변환
                user.getId(),
                user.getPassword(),
                user.getName(),
                user.getNickname(),
                user.getBirth(),
                user.getEmail(),
                user.getIntro()
        );
    }
    // 로그인 조회
    public User findByIdAndPassword(String id, String password) {
        String sql = "SELECT * FROM all_users WHERE id = ? AND password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, id, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 로그인 후 유저 정보 조회용
    public User findById(String id) {
        String sql = "SELECT * FROM all_users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    // 내소개 업데이트
    public void updateIntro(String userId, String intro) {
        String sql = "UPDATE all_users SET intro = ? WHERE id = ?";
        jdbcTemplate.update(sql, intro, userId);
    }



    // 닉네임 중복 (본인 제외)
    public int countByNicknameExcludingId(String nickname, String id) {
        String sql = "SELECT COUNT(*) FROM all_users WHERE nickname = ? AND id <> ?";
        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, nickname, id);
        return cnt != null ? cnt : 0;
    }

    // 프로필 업데이트
    public void updateProfile(String id, String name, String nickname, String email,
                              java.time.LocalDate birth, String intro) {
        String sql = "UPDATE all_users SET name=?, nickname=?, email=?, birth=?, intro=? WHERE id=?";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, nickname);
            ps.setString(3, email);
            if (birth != null) ps.setDate(4, java.sql.Date.valueOf(birth));
            else ps.setNull(4, java.sql.Types.DATE);
            ps.setString(5, intro);
            ps.setString(6, id);
            return ps;
        });
    }

    // 비밀번호 변경
    public void updatePassword(String id, String newPassword) {
        String sql = "UPDATE all_users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, newPassword, id);
    }
}