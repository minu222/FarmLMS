package com.lms.urbangreen.lecture.qna.repository;

import com.lms.urbangreen.lecture.qna.entity.Qna;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QnaRepositoryImpl implements QnaRepository {

    private final JdbcTemplate jdbcTemplate;

    // Qna 엔티티와 데이터베이스 컬럼을 매핑하는 RowMapper 정의
    private final RowMapper<Qna> qnaRowMapper = (rs, rowNum) -> {
        Qna qna = new Qna();
        qna.setQna_id(rs.getInt("qna_id"));
        qna.setLecture_id(rs.getInt("lecture_id"));
        qna.setUser_id(rs.getInt("user_id"));
        // p_qna_id는 NULL일 수 있으므로, NULL 체크를 포함합니다.
        Integer pQnaId = (Integer) rs.getObject("p_qna_id");
        qna.setP_qna_id(pQnaId != null ? pQnaId : 0); // NULL이면 0으로 처리하거나 Optional<Integer>로 처리할 수 있습니다. 여기서는 0으로 가정합니다.
        qna.setContent(rs.getString("content"));
        qna.setCreated_at(rs.getObject("created_at", LocalDateTime.class));
        return qna;
    };

    /**
     * 강의 ID를 기반으로 부모 QnA (질문) 리스트를 조회합니다. (p_qna_id IS NULL)
     */
    @Override
    public List<Qna> findParentQnasByLectureId(int lectureId) {
        // 최근 작성된 질문이 위로 오도록 정렬
        String sql = "SELECT * FROM lecture_qna WHERE lecture_id = ? AND p_qna_id IS NULL ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, qnaRowMapper, lectureId);
    }

    /**
     * 특정 질문 ID를 기반으로 답변 리스트를 조회합니다.
     */
    @Override
    public List<Qna> findRepliesByParentQnaId(int pQnaId) {
        // 오래된 답변이 위로 오도록 정렬
        String sql = "SELECT * FROM lecture_qna WHERE p_qna_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, qnaRowMapper, pQnaId);
    }

    /**
     * QnA (질문 또는 답변)를 저장하고, 생성된 ID를 포함하여 반환합니다.
     */
    @Override
    public Qna save(Qna qna) {
        String sql = "INSERT INTO lecture_qna (lecture_id, user_id, p_qna_id, content, created_at) VALUES (?, ?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, qna.getLecture_id());
            ps.setInt(2, qna.getUser_id());

            if (qna.getP_qna_id() != 0) {
                ps.setInt(3, qna.getP_qna_id()); // 답변일 경우 부모 ID 설정
            } else {
                ps.setNull(3, java.sql.Types.INTEGER); // 질문일 경우 NULL 설정
            }

            ps.setString(4, qna.getContent());
            return ps;
        }, keyHolder);

        // 생성된 qna_id를 엔티티에 설정
        if (keyHolder.getKey() != null) {
            qna.setQna_id(keyHolder.getKey().intValue());
        }
        return qna;
    }

    /**
     * QnA 내용을 수정합니다.
     */
    @Override
    public void updateContent(int qnaId, String content) {
        String sql = "UPDATE lecture_qna SET content = ?, created_at = NOW() WHERE qna_id = ?";
        jdbcTemplate.update(sql, content, qnaId);
    }

    /**
     * QnA를 ID로 삭제합니다.
     */
    @Override
    public void deleteById(int qnaId) {
        String sql = "DELETE FROM lecture_qna WHERE qna_id = ?";
        jdbcTemplate.update(sql, qnaId);
    }

    /**
     * QnA ID로 단일 엔티티를 조회합니다.
     */
    @Override
    public Optional<Qna> findById(int qnaId) {
        String sql = "SELECT * FROM lecture_qna WHERE qna_id = ?";
        try {
            Qna qna = jdbcTemplate.queryForObject(sql, qnaRowMapper, qnaId);
            return Optional.ofNullable(qna);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}