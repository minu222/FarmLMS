package com.lms.urbangreen.urbangreenproject.chat.repository;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMessage;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository {

    private final JdbcTemplate jdbc;

    private ChatMessage mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ChatMessage msg = new ChatMessage();
        msg.setMessageId(rs.getInt("message_id"));
        msg.setRoomId(rs.getInt("room_id"));
        msg.setUserId(rs.getInt("user_id"));
        msg.setContent(rs.getString("content"));

        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        msg.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        return msg;
    }

    /**
     * 메시지 저장
     *
     * @return 생성된 message_id
     */
    public Integer save(Integer roomId, Integer userId, String content) {
        String sql = "INSERT INTO chat_message (room_id, user_id, content, created_at) VALUES (?, ?, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : null;
    }

    /**
     * PK로 단건 조회
     */
    public Optional<ChatMessage> findById(Integer messageId) {
        String sql = "SELECT * FROM chat_message WHERE message_id = ?";
        List<ChatMessage> list = jdbc.query(sql, this::mapRow, messageId);
        return list.stream().findFirst();
    }

    /**
     * 해당 방의 메시지 목록 (limit 포함)
     */
    public List<ChatMessage> findByRoomId(Integer roomId, int limit) {
        String sql = """
                SELECT * 
                FROM chat_message
                WHERE room_id = ?
                ORDER BY created_at ASC, message_id ASC
                LIMIT ?
                """;

        return jdbc.query(sql, this::mapRow, roomId, limit);
    }

    /**
     * 해당 방의 메시지 전체
     */
    public List<ChatMessage> findByRoomId(Integer roomId) {
        String sql = """
                SELECT * 
                FROM chat_message
                WHERE room_id = ?
                ORDER BY created_at ASC, message_id ASC
                """;
        return jdbc.query(sql, this::mapRow, roomId);
    }

    public List<ChatMessagePayload> findRecentMessagesWithNickname(Integer roomId, int limit) {
        String sql = """
            SELECT m.message_id,
                   m.room_id,
                   m.user_id,
                   COALESCE(u.nickname, u.name) AS nickname,
                   m.content,
                   m.created_at
            FROM chat_message m
            JOIN chat_member cm
              ON cm.room_id = m.room_id
             AND cm.user_id = m.user_id
            JOIN all_users u
              ON u.user_id = cm.user_id
            WHERE m.room_id = ?
            ORDER BY m.created_at ASC, m.message_id ASC
            LIMIT ?
            """;

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return jdbc.query(sql, (rs, i) -> {
            Integer messageId = rs.getInt("message_id");
            Integer rId       = rs.getInt("room_id");
            Integer uId       = rs.getInt("user_id");
            String nickname   = rs.getString("nickname");
            String content    = rs.getString("content");

            Timestamp ts = rs.getTimestamp("created_at");
            String createdAtStr = null;
            if (ts != null) {
                LocalDateTime ldt = ts.toLocalDateTime();
                createdAtStr = ldt.format(fmt);
            }

            return new ChatMessagePayload(
                    messageId,
                    rId,
                    uId,
                    nickname,
                    content,
                    createdAtStr
            );
        }, roomId, limit);
    }
}
