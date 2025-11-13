package com.lms.urbangreen.urbangreenproject.service;

import com.lms.urbangreen.urbangreenproject.model.User;
import com.lms.urbangreen.urbangreenproject.model.UserType;
import com.lms.urbangreen.urbangreenproject.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    private final UserRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public UserService(UserRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 로그인
    public User login(String id, String password) {
        return repository.findByIdAndPassword(id, password);
    }

    // 회원가입
    public boolean register(User user) {
        try {
            if (user.getUserType() == null) {
                user.setUserType(UserType.student); // Enum은 대문자 STUDENT
            }
            repository.save(user);
            return true; // 성공 시 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    // 아이디 중복 체크
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM all_users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // 닉네임 중복 체크
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM all_users WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return count != null && count > 0;
    }


    // ID로 사용자 조회
    public User findById(String id) {
        String sql = "SELECT * FROM all_users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setNickname(rs.getString("nickname"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setBirth(rs.getDate("birth").toLocalDate());
            user.setIntro(rs.getString("intro")); // 내소개 칸
            user.setUserType(UserType.valueOf(rs.getString("user_type")));
            return user;
        }, id);
    }

    // 내소개 업데이트
    public void updateIntro(String userId, String intro) {
        repository.updateIntro(userId, intro);
    }


    // 닉네임 중복(본인 제외) 체크
    public boolean existsByNicknameExcludingUser(String nickname, String id) {
        return repository.countByNicknameExcludingId(nickname, id) > 0;
    }

    // 프로필 업데이트
    public void updateProfile(String id, String name, String nickname, String email,
                              java.time.LocalDate birth, String intro) {
        repository.updateProfile(id, name, nickname, email, birth, intro);
    }

    // 비밀번호 규칙 (로그인 컨트롤러와 동일: 영문+숫자 8자 이상)
    private boolean isValidPassword(String pw) {
        return pw != null && pw.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    // 비밀번호 변경 결과 DTO (간단 레코드)
    public record PwChangeResult(boolean success, String message) {}

    public PwChangeResult changePassword(String id, String currentPw, String newPw) {
        User dbUser = repository.findById(id);   // repo의 RowMapper는 password 포함
        if (dbUser == null) return new PwChangeResult(false, "사용자를 찾을 수 없습니다.");

        // 평문 저장 구조라 현재 비번 비교를 평문으로 합니다 (향후 BCrypt 권장)
        if (!dbUser.getPassword().equals(currentPw)) {
            return new PwChangeResult(false, "현재 비밀번호가 일치하지 않습니다.");
        }
        if (!isValidPassword(newPw)) {
            return new PwChangeResult(false, "새 비밀번호는 영문과 숫자를 포함한 8자 이상이어야 합니다.");
        }
        repository.updatePassword(id, newPw);
        return new PwChangeResult(true, "OK");
    }
}