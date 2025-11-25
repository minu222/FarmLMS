package com.lms.urbangreen.urbangreenproject.mypage.repository;

import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureCommentSummaryDto;
import com.lms.urbangreen.urbangreenproject.mypage.dto.MyLectureReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcMypageCommentsRepository implements MypageCommentsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MyLectureCommentSummaryDto> findLecturesWithMyComments(int userId) {
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
                    JOIN lecture_qna q
                        ON q.lecture_id = l.lecture_id
                    JOIN all_users au
                        ON au.user_id = l.user_id
                WHERE q.user_id = ?
                  AND q.p_qna_id IS NULL       -- 질문(내 댓글)만
                GROUP BY l.lecture_id, l.title, l.category, l.sub_category, l.content, au.name
                ORDER BY MAX(q.created_at) DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapLectureSummary(rs), userId);
    }

    private MyLectureCommentSummaryDto mapLectureSummary(ResultSet rs) throws SQLException {
        MyLectureCommentSummaryDto dto = new MyLectureCommentSummaryDto();
        dto.setId(rs.getInt("lecture_id"));
        dto.setTitle(rs.getString("title"));
        dto.setCategory(rs.getString("category"));
        // 서브 카테고리를 level 개념으로 재사용
        dto.setLevel(rs.getString("sub_category"));
        dto.setDescription(rs.getString("content"));
        dto.setInstructor(rs.getString("instructor_name"));
        dto.setCommentCount(rs.getInt("comment_count"));
        // status 는 DB에 없으므로 null 또는 기본값
        dto.setStatus("공개");
        return dto;
    }

    @Override
    public List<MyLectureCommentDto> findMyCommentsByLecture(int lectureId, int userId) {
        // 1) 내가 쓴 질문(댓글) 목록
        String sql = """
                SELECT
                    q.qna_id,
                    q.content,
                    q.created_at,
                    u.name AS author_name
                FROM lecture_qna q
                    JOIN all_users u
                        ON u.user_id = q.user_id
                WHERE q.lecture_id = ?
                  AND q.user_id = ?
                  AND q.p_qna_id IS NULL        -- 상위 댓글(질문)
                ORDER BY q.created_at DESC
                """;

        List<MyLectureCommentDto> comments = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapComment(rs),
                lectureId, userId
        );

        // 2) 각 댓글에 대한 답글(선생님/관리자)이 있으면 채우기
        String replySql = """
                SELECT
                    r.qna_id,
                    r.p_qna_id,
                    r.content,
                    r.created_at,
                    u.name AS author_name
                FROM lecture_qna r
                    JOIN all_users u
                        ON u.user_id = r.user_id
                WHERE r.p_qna_id = ?
                ORDER BY r.created_at ASC
                """;

        for (MyLectureCommentDto comment : comments) {
            List<MyLectureReplyDto> replies = jdbcTemplate.query(
                    replySql,
                    (rs, rowNum) -> mapReply(rs),
                    comment.getId()
            );
            comment.setReplies(replies);
        }

        return comments;
    }

    private MyLectureCommentDto mapComment(ResultSet rs) throws SQLException {
        MyLectureCommentDto dto = new MyLectureCommentDto();
        dto.setId(rs.getInt("qna_id"));
        dto.setContent(rs.getString("content"));

        // timestamp -> LocalDateTime
        LocalDateTime createdAt = null;
        if (rs.getTimestamp("created_at") != null) {
            createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        }
        dto.setCreatedAt(createdAt);

        dto.setAuthorName(rs.getString("author_name"));
        return dto;
    }

    private MyLectureReplyDto mapReply(ResultSet rs) throws SQLException {
        MyLectureReplyDto dto = new MyLectureReplyDto();
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

    @Override
    public int deleteMyComment(int qnaId, int userId) {
        // 내가 쓴 댓글만 삭제 (연결된 답글은 FK ON DELETE CASCADE 로 자동 삭제)
        String sql = "DELETE FROM lecture_qna WHERE qna_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, qnaId, userId);
    }
}
