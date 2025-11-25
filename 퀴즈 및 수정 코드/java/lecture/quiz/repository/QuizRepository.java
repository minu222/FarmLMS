package com.lms.urbangreen.lecture.quiz.repository;

import com.lms.urbangreen.lecture.quiz.entity.Quiz;
import com.lms.urbangreen.lecture.quiz.entity.QuizAttempt;
import com.lms.urbangreen.lecture.quiz.entity.QuizScore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp; // LocalDateTime 변환용
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // 1. 비디오 ID로 퀴즈 목록 조회 (정답 및 Pass Score 확인용)
    public List<Quiz> findAllByVideoId(int videoId) {
        String sql = "SELECT * FROM quiz WHERE video_id = :videoId ORDER BY quiz_id ASC";
        MapSqlParameterSource params = new MapSqlParameterSource("videoId", videoId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Quiz.class));
    }

    // 2. [개별 시도] QuizAttempt 저장
    public void saveAttempt(QuizAttempt attempt) {
        String sql = """
            INSERT INTO quiz_attempt (user_id, quiz_id, answer_text, earned_score, attempted_at)
            VALUES (:userId, :quizId, :answerText, :earnedScore, :attemptedAt)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", attempt.getUser_id())
                .addValue("quizId", attempt.getQuiz_id())
                .addValue("answerText", attempt.getAnswer_text())
                .addValue("earnedScore", attempt.getEarned_score())
                .addValue("attemptedAt", Timestamp.valueOf(attempt.getAttempted_at())); // LocalDateTime -> Timestamp

        jdbcTemplate.update(sql, params);
    }

    // 3. QuizScore 저장
    public void saveScore(QuizScore score) {
        String sql = """
            INSERT INTO quiz_score (video_id, user_id, total_score, pass)
            VALUES (:videoId, :userId, :totalScore, :pass)
            ON DUPLICATE KEY UPDATE 
                total_score = VALUES(total_score), 
                pass = VALUES(pass)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("videoId", score.getVideo_id())
                .addValue("userId", score.getUser_id())
                .addValue("totalScore", score.getTotal_score())
                .addValue("pass", score.getPass().name());

        jdbcTemplate.update(sql, params);
    }

    // (선택) 특정 유저가 이미 해당 비디오 퀴즈를 통과했는지 확인
    public boolean existsPassingScore(int userId, int videoId) {
        String sql = "SELECT COUNT(*) FROM quiz_score WHERE user_id = :userId AND video_id = :videoId AND pass = 'PASS'";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("videoId", videoId);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}