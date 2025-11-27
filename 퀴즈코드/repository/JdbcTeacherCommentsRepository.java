package com.lms.urbangreen.urbangreenproject.teacher.repository;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureReplyDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherLectureCommentsSummaryDto;
import com.lms.urbangreen.urbangreenproject.teacher.dto.TeacherReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcTeacherCommentsRepository implements TeacherCommentsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 강사가 올린 강의 목록 + 해당 강의에 달린 "질문" 개수
     */
    @Override
    public List<TeacherLectureCommentsSummaryDto> findLecturesForTeacher(int teacherUserId) {
        String sql = """
                SELECT
                    l.lecture_id,
                    l.title,
                    l.category,
                    l.sub_category,
                    l.content,
                    au.name AS instructor_name,
                    COUNT(q.qna_id) AS comment_count
                FROM lecture l
                    JOIN all_users au
                        ON au.user_id = l.user_id
                    LEFT JOIN lecture_qna q
                        ON q.lecture_id = l.lecture_id
                       AND q.p_qna_id IS NULL         -- 질문만 카운트
                WHERE l.user_id = ?
                GROUP BY l.lecture_id, l.title, l.category, l.sub_category, l.content, au.name
                ORDER BY MAX(l.created_at) DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapLectureSummary(rs), teacherUserId);
    }

    private TeacherLectureCommentsSummaryDto mapLectureSummary(ResultSet rs) throws SQLException {
        TeacherLectureCommentsSummaryDto dto = new TeacherLectureCommentsSummaryDto();
        dto.setId(rs.getInt("lecture_id"));
        dto.setTitle(rs.getString("title"));
        dto.setCategory(rs.getString("category"));
        dto.setLevel(rs.getString("sub_category"));       // sub_category 를 level 처럼 사용
        dto.setDescription(rs.getString("content"));
        dto.setInstructor(rs.getString("instructor_name"));
        dto.setCommentCount(rs.getInt("comment_count"));
        dto.setStatus("공개");                            // 별도 컬럼 없으니 일단 하드코딩
        return dto;
    }

    /**
     * 특정 강사의 특정 강의에 달린 질문 + 답글 목록
     */
    @Override
    public List<TeacherLectureCommentsDto> findCommentsForLectureOfTeacher(int lectureId, int teacherUserId) {
        // 1) 질문(최상위 QnA) 목록
        String sql = """
                SELECT
                    q.qna_id,
                    q.content,
                    q.created_at,
                    u.name AS author_name
                FROM lecture_qna q
                    JOIN lecture l
                        ON l.lecture_id = q.lecture_id
                    JOIN all_users u
                        ON u.user_id = q.user_id
                WHERE q.lecture_id = ?
                  AND q.p_qna_id IS NULL          -- 질문만
                  AND l.user_id = ?               -- 이 강의의 강사가 로그인한 강사
                ORDER BY q.created_at DESC
                """;

        List<TeacherLectureCommentsDto> comments = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapComment(rs),
                lectureId, teacherUserId
        );

        // 2) 각 질문에 대한 답글 불러오기
        String replySql = """
                SELECT
                    r.qna_id,
                    r.content,
                    r.created_at,
                    u.name AS author_name
                FROM lecture_qna r
                    JOIN lecture l
                        ON l.lecture_id = r.lecture_id
                    JOIN all_users u
                        ON u.user_id = r.user_id
                WHERE r.p_qna_id = ?
                  AND l.user_id = ?
                ORDER BY r.created_at ASC
                """;

        for (TeacherLectureCommentsDto comment : comments) {
            List<TeacherReplyDto> replies = jdbcTemplate.query(
                    replySql,
                    (rs, rowNum) -> mapReply(rs),
                    comment.getId(),
                    teacherUserId
            );
            comment.setReplies(replies);
        }

        return comments;
    }

    private TeacherLectureCommentsDto mapComment(ResultSet rs) throws SQLException {
        TeacherLectureCommentsDto dto = new TeacherLectureCommentsDto();
        dto.setId(rs.getInt("qna_id"));
        dto.setContent(rs.getString("content"));

        LocalDateTime createdAt = null;
        if (rs.getTimestamp("created_at") != null) {
            createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        }
        dto.setCreatedAt(createdAt);

        dto.setAuthorName(rs.getString("author_name"));
        return dto;
    }

    private TeacherReplyDto mapReply(ResultSet rs) throws SQLException {
        TeacherReplyDto dto = new TeacherReplyDto();
        dto.setId(rs.getInt("qna_id"));
        dto.setContent(rs.getString("content"));

        LocalDateTime createdAt = null;
        if (rs.getTimestamp("created_at") != null) {
            createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        }
        dto.setCreatedAt(createdAt);

        dto.setAuthorName(rs.getString("author_name"));
        return dto;
    }

    /**
     * 강사의 강의에 달린 질문(댓글) 삭제 (답글은 FK CASCADE 로 함께 삭제됨)
     */
    @Override
    public int deleteCommentOnMyLecture(int qnaId, int teacherUserId) {
        String sql = """
                DELETE q
                FROM lecture_qna q
                    JOIN lecture l
                        ON l.lecture_id = q.lecture_id
                WHERE q.qna_id = ?
                  AND l.user_id = ?
                """;
        return jdbcTemplate.update(sql, qnaId, teacherUserId);
    }

    /**
     * 강사의 강의에 달린 개별 답글 삭제
     */
    @Override
    public int deleteReplyOnMyLecture(int replyId, int teacherUserId) {
        String sql = """
                DELETE r
                FROM lecture_qna r
                    JOIN lecture l
                        ON l.lecture_id = r.lecture_id
                WHERE r.qna_id = ?
                  AND l.user_id = ?
                """;
        return jdbcTemplate.update(sql, replyId, teacherUserId);
    }

    @Override
    public int insertReplyToMyLectureComment(int commentId, int teacherUserId, String content) {
        String sql = """
            INSERT INTO lecture_qna (lecture_id, user_id, p_qna_id, content)
            SELECT
                q.lecture_id,      -- 질문이 달린 강의
                ?,                 -- 답변 작성자 = 로그인 강사
                q.qna_id,          -- 부모 질문 ID
                ?
            FROM lecture_qna q
                JOIN lecture l
                    ON l.lecture_id = q.lecture_id
            WHERE q.qna_id = ?
              AND l.user_id = ?   -- 이 강의의 담당 강사가 현재 로그인 강사여야 함
            """;

        return jdbcTemplate.update(
                sql,
                teacherUserId,        // ?
                content,              // ?
                commentId,            // q.qna_id = ?
                teacherUserId         // l.user_id = ?
        );
    }
}
