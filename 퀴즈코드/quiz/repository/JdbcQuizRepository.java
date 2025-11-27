package com.lms.urbangreen.urbangreenproject.teacher.quiz.repository;

import com.lms.urbangreen.urbangreenproject.teacher.quiz.domain.Quiz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcQuizRepository implements QuizRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcQuizRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Quiz> quizRowMapper() {
        return new RowMapper<Quiz>() {
            @Override
            public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
                Quiz q = new Quiz();
                q.setQuizId(rs.getLong("quiz_id"));
                q.setVideoId(rs.getLong("video_id"));
                q.setQuizNumber(rs.getInt("quiz_number"));
                q.setImgUrl(rs.getString("img_url"));
                q.setQuestion(rs.getString("question"));
                q.setModelAnswer(rs.getString("model_answer"));
                q.setPassScore(rs.getInt("pass_score"));
                return q;
            }
        };
    }

    @Override
    public List<Quiz> findByVideoId(Long videoId) {
        String sql =
                "SELECT quiz_id, video_id, quiz_number, img_url, question, model_answer, pass_score " +
                        "FROM quiz WHERE video_id = ? ORDER BY quiz_number ASC";
        return jdbcTemplate.query(sql, quizRowMapper(), videoId);
    }

    @Override
    public void deleteByVideoId(Long videoId) {
        String sql = "DELETE FROM quiz WHERE video_id = ?";
        jdbcTemplate.update(sql, videoId);
    }

    @Override
    public void save(Quiz quiz) {
        String sql =
                "INSERT INTO quiz (video_id, quiz_number, img_url, question, model_answer, pass_score) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                quiz.getVideoId(),
                quiz.getQuizNumber(),
                quiz.getImgUrl(),
                quiz.getQuestion(),
                quiz.getModelAnswer(),
                quiz.getPassScore()
        );
    }
}