package com.lms.urbangreen.urbangreenproject.lecture.qna.repository;


import com.lms.urbangreen.urbangreenproject.lecture.qna.entity.Qna;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
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

    private final RowMapper<Qna> qnaRowMapper = (rs, rowNum) -> {
        System.out.println("RowMapper 실행 중: qna_id = " + rs.getInt("qna_id"));
        Qna qna = new Qna();
        qna.setQnaId(rs.getInt("qna_id"));
        qna.setLectureId(rs.getInt("lecture_id"));
        qna.setUserId(rs.getInt("user_id"));
        Integer pId = rs.getObject("p_qna_id", Integer.class);
        qna.setPQnaId(pId); // Integer는 null을 허용합니다.
        qna.setContent(rs.getString("content"));
        qna.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return qna;
    };

    @Override
    public List<Qna> findParentQnasByLectureId(int lectureId, int offset, int limit) {
        // 페이징 쿼리 (최신순)
        String sql = "SELECT * FROM lecture_qna WHERE lecture_id = ? AND (p_qna_id IS NULL OR p_qna_id = 0) ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, qnaRowMapper, lectureId, limit, offset);
    }

    @Override
    public int countParentQnasByLectureId(int lectureId) {
        String sql = "SELECT COUNT(*) FROM lecture_qna WHERE lecture_id = ? AND (p_qna_id IS NULL OR p_qna_id = 0)";
        return jdbcTemplate.queryForObject(sql, Integer.class, lectureId);
    }

    @Override
    public List<Qna> findRepliesByParentQnaId(int pQnaId) {
        // 답변은 작성순(오래된 것 위로)
        String sql = "SELECT * FROM lecture_qna WHERE p_qna_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, qnaRowMapper, pQnaId);
    }

    @Override
    public Qna save(Qna qna) {
        String sql = "INSERT INTO lecture_qna (lecture_id, user_id, p_qna_id, content, created_at) VALUES (?, ?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, qna.getLectureId());
            ps.setInt(2, qna.getUserId());

            // pQnaId가 null이면 NULL로 저장, 아니면 ID로 저장
            if (qna.getPQnaId() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, qna.getPQnaId());
            }

            ps.setString(4, qna.getContent());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            qna.setQnaId(keyHolder.getKey().intValue());
        }
        return qna;
    }

    @Override
    public void deleteById(int qnaId) {
        String sql = "DELETE FROM lecture_qna WHERE qna_id = ?";
        jdbcTemplate.update(sql, qnaId);
    }

    @Override
    public Optional<Qna> findById(int qnaId) {
        String sql = "SELECT * FROM lecture_qna WHERE qna_id = ?";

        try {
            List<Qna> results = jdbcTemplate.query(sql, qnaRowMapper, qnaId);

            if (results.isEmpty()) {
                // ▼▼▼ [수정] 부모 질문 조회 실패 시 로그 강화 ▼▼▼
                System.err.println("--- findById 실패: Qna ID " + qnaId + "에 해당하는 레코드를 찾을 수 없음 (EMPTY) ---");
                return Optional.empty();
            } else {
                System.out.println("--- findById 성공: Qna ID " + qnaId + " 조회 완료 ---");
                return Optional.of(results.get(0));
            }
        } catch (org.springframework.dao.DataAccessException e) {
            // ▼▼▼ [필수] 치명적인 DB 접근 오류 발생 시 스택 트레이스 출력 ▼▼▼
            System.err.println("!!! 치명적 DB 접근 오류 발생 - QnA ID: " + qnaId + " (Error: " + e.getMessage() + ")");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void updateContent(int qnaId, String content) {
        // 수정 시 created_at을 NOW()로 바꾸면 정렬 순서가 뒤바뀔 수 있으므로 content만 바꿉니다.
        String sql = "UPDATE lecture_qna SET content = ? WHERE qna_id = ?";
        jdbcTemplate.update(sql, content, qnaId);
    }
}