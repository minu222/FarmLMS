package com.lms.urbangreen.chat.repository;

import com.lms.urbangreen.chat.dto.ChatDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final JdbcTemplate jdbc;

    // 방 생성
    public long createRoom(String name, String desc, int creatorUserId) {
        var sql = "INSERT INTO chat_room(name, description, created_by) VALUES(?,?,?)";
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, creatorUserId);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0L : key.longValue();
    }

    // 방 가입/나가기
    public void joinRoom(long roomId, int userId, boolean admin) {
        var sql = "INSERT INTO chat_member(room_id, user_id, role) " +
                "VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE role = role";
        jdbc.update(sql, roomId, userId, admin ? "ADMIN" : "MEMBER");
    }

    public void leaveRoom(long roomId, int userId) {
        jdbc.update("DELETE FROM chat_member WHERE room_id=? AND user_id=?", roomId, userId);
    }

    // 내 방/다른 방
    public List<ChatDtos.RoomDto> findJoinedRooms(int userId) {
        var sql = """
            SELECT r.room_id, r.name, r.description,
                   (SELECT COUNT(*) FROM chat_member m WHERE m.room_id=r.room_id) AS members
              FROM chat_room r
              JOIN chat_member m ON m.room_id=r.room_id AND m.user_id=?
             ORDER BY r.room_id DESC
        """;
        return jdbc.query(sql, (rs, n) -> ChatDtos.RoomDto.builder()
                .id(rs.getLong("room_id"))
                .name(rs.getString("name"))
                .desc(rs.getString("description"))
                .members(rs.getInt("members"))
                .joined(true)
                .build(), userId);
    }

    public List<ChatDtos.RoomDto> findOtherRooms(int userId) {
        var sql = """
            SELECT r.room_id, r.name, r.description,
                   (SELECT COUNT(*) FROM chat_member m WHERE m.room_id=r.room_id) AS members
              FROM chat_room r
             WHERE NOT EXISTS (SELECT 1 FROM chat_member m WHERE m.room_id=r.room_id AND m.user_id=?)
             ORDER BY r.room_id DESC
        """;
        return jdbc.query(sql, (rs, n) -> ChatDtos.RoomDto.builder()
                .id(rs.getLong("room_id"))
                .name(rs.getString("name"))
                .desc(rs.getString("description"))
                .members(rs.getInt("members"))
                .joined(false)
                .build(), userId);
    }

    // 메시지 CRUD
    public long saveMessage(long roomId, int userId, String content) {
        var sql = "INSERT INTO chat_message(room_id, user_id, content) VALUES(?,?,?)";
        var kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, roomId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            return ps;
        }, kh);
        Number key = kh.getKey();
        return key == null ? 0L : key.longValue();
    }

    public List<ChatDtos.MessageOut> findRecentMessages(long roomId, int currentUserId, int limit) {
        var sql = """
            SELECT m.message_id, m.room_id, m.user_id, m.content, m.created_at,
                   u.nickname AS sender_name
              FROM chat_message m
              JOIN all_users u ON u.user_id=m.user_id
             WHERE m.room_id=?
             ORDER BY m.message_id DESC
             LIMIT ?
        """;
        var list = jdbc.query(sql, (rs, n) -> ChatDtos.MessageOut.builder()
                .id(rs.getLong("message_id"))
                .roomId(rs.getLong("room_id"))
                .senderId(rs.getInt("user_id"))
                .senderName(rs.getString("sender_name"))
                .content(rs.getString("content"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .mine(rs.getInt("user_id") == currentUserId)
                .build(), roomId, limit);
        // 최신 아래로 보이도록 역정렬
        java.util.Collections.reverse(list);
        return list;
    }
}
