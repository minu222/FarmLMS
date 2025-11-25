package com.lms.urbangreen.urbangreenproject.admin.repository;

import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureCommentReplyDto;
import com.lms.urbangreen.urbangreenproject.admin.dto.LectureSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAdminCommentRepository implements AdminCommentRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 왼쪽 강의 목록 + 강의별 질문 개수
     */
    @Override
    public List<LectureSummaryDto> findAllLectureSummaries() {
        String sql = """
            SELECT l.lecture_id,
                   l.title,
                   l.category,
                   l.sub_category,
                   l.content,
                   l.created_at,
                   au.name AS teacher_name,
                   (
                     SELECT COUNT(*)
                     FROM lecture_qna q
                     WHERE q.lecture_id = l.lecture_id
                       AND q.p_qna_id IS NULL
                   ) AS question_count
            FROM lecture l
            JOIN all_users au ON l.user_id = au.user_id
            ORDER BY l.created_at DESC
            """;

        return jdbcTemplate.query(sql, lectureSummaryRowMapper());
    }

    /**
     * 특정 강의의 "질문" 목록 (p_qna_id IS NULL)
     */
    @Override
    public List<LectureCommentDto> findCommentsByLectureId(Long lectureId) {
        String sql = """
            SELECT q.qna_id,
                   q.lecture_id,
                   u.name AS author_name,
                   q.content,
                   q.created_at
            FROM lecture_qna q
            JOIN all_users u ON q.user_id = u.user_id
            WHERE q.lecture_id = ?
              AND q.p_qna_id IS NULL
            ORDER BY q.created_at DESC
            """;

        return jdbcTemplate.query(sql, commentRowMapper(), lectureId);
    }

    /**
     * 특정 강의의 "답글" 목록 (p_qna_id IS NOT NULL)
     */
    @Override
    public List<LectureCommentReplyDto> findRepliesByLectureId(Long lectureId) {
        String sql = """
            SELECT r.qna_id      AS reply_id,
                   r.p_qna_id    AS comment_id,
                   u.name        AS author_name,
                   r.content,
                   r.created_at
            FROM lecture_qna r
            JOIN all_users u ON r.user_id = u.user_id
            WHERE r.lecture_id = ?
              AND r.p_qna_id IS NOT NULL
            ORDER BY r.created_at ASC
            """;

        return jdbcTemplate.query(sql, replyRowMapper(), lectureId);
    }

    /**
     * 답글 INSERT
     * - lecture_id : 부모 질문의 lecture_id
     * - user_id    : 해당 강의의 teacher ID (lecture.user_id)
     * - p_qna_id   : 부모 질문 qna_id
     */
    @Override
    public void insertReply(Long commentId, String content) {
        String sql = """
            INSERT INTO lecture_qna (lecture_id, user_id, p_qna_id, content, created_at)
            SELECT q.lecture_id,
                   l.user_id,
                   q.qna_id,
                   ?,
                   NOW()
            FROM lecture_qna q
            JOIN lecture l ON q.lecture_id = l.lecture_id
            WHERE q.qna_id = ?
            """;

        jdbcTemplate.update(sql, content, commentId);
    }

    /**
     * 질문 삭제 (답글은 FK(p_qna_id) cascade로 자동 삭제)
     */
    @Override
    public void deleteComment(Long commentId) {
        String sql = "DELETE FROM lecture_qna WHERE qna_id = ?";
        jdbcTemplate.update(sql, commentId);
    }

    /**
     * 답글 삭제
     */
    @Override
    public void deleteReply(Long replyId) {
        String sql = "DELETE FROM lecture_qna WHERE qna_id = ?";
        jdbcTemplate.update(sql, replyId);
    }

    // ================= RowMappers =================

    private RowMapper<LectureSummaryDto> lectureSummaryRowMapper() {
        return (rs, rowNum) -> {
            LectureSummaryDto dto = new LectureSummaryDto();
            dto.setId(rs.getLong("lecture_id"));
            dto.setTitle(rs.getString("title"));
            dto.setCategory(rs.getString("category"));       // gardening / field / house
            dto.setLevel(rs.getString("sub_category"));      // seed / grow / ship
            dto.setDescription(rs.getString("content"));
            dto.setInstructor(rs.getString("teacher_name")); // all_users.name
            dto.setStatus("공개");                           // 필요하면 컬럼 추가해서 변경
            dto.setCommentCount(rs.getInt("question_count"));
            return dto;
        };
    }

    private RowMapper<LectureCommentDto> commentRowMapper() {
        return (rs, rowNum) -> {
            LectureCommentDto dto = new LectureCommentDto();
            dto.setId(rs.getLong("qna_id"));
            dto.setLectureId(rs.getLong("lecture_id"));
            dto.setAuthorName(rs.getString("author_name"));
            dto.setContent(rs.getString("content"));
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return dto;
        };
    }

    private RowMapper<LectureCommentReplyDto> replyRowMapper() {
        return (rs, rowNum) -> {
            LectureCommentReplyDto dto = new LectureCommentReplyDto();
            dto.setId(rs.getLong("reply_id"));
            dto.setCommentId(rs.getLong("comment_id"));
            dto.setAuthorName(rs.getString("author_name"));
            dto.setContent(rs.getString("content"));
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return dto;
        };
    }
}