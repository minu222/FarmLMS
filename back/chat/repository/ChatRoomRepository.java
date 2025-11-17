package com.lms.urbangreen.urbangreenproject.chat.repository;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class ChatRoomRepository {

    private final JdbcTemplate jdbc;

    private ChatRoom mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ChatRoom room = new ChatRoom();
        room.setRoomId(rs.getInt("room_id"));
        room.setUserId(rs.getInt("user_id"));
        room.setRoomName(rs.getString("room_name"));

        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        room.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        return room;
    }

    /**
     * 새 채팅방 생성
     *
     * @param userId   방 만든 사람 (admin) user_id
     * @param roomName 방 이름
     * @return 생성된 room_id
     */
    public Integer createRoom(Integer userId, String roomName) {
        String sql = "INSERT INTO chat_room (user_id, room_name, created_at) VALUES (?, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, roomName);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : null;
    }

    public Optional<ChatRoom> findById(Integer roomId) {
        String sql = "SELECT * FROM chat_room WHERE room_id = ?";
        List<ChatRoom> list = jdbc.query(sql, this::mapRow, roomId);
        return list.stream().findFirst();
    }

    /**
     * 유저가 참여한 채팅방 목록
     */
    public List<ChatRoom> findJoinedRooms(Integer userId) {
        String sql = """
                SELECT r.*
                FROM chat_room r
                JOIN chat_member m ON r.room_id = m.room_id
                WHERE m.user_id = ?
                ORDER BY r.created_at DESC, r.room_id DESC
                """;

        return jdbc.query(sql, this::mapRow, userId);
    }

    /**
     * 유저가 참여하지 않은 채팅방 목록
     */
    public List<ChatRoom> findOtherRooms(Integer userId) {
        String sql = """
                SELECT r.*
                FROM chat_room r
                WHERE r.room_id NOT IN (
                    SELECT room_id FROM chat_member WHERE user_id = ?
                )
                ORDER BY r.created_at DESC, r.room_id DESC
                """;

        return jdbc.query(sql, this::mapRow, userId);
    }

    /**
     * 전체 채팅방 (관리자용 등 필요시)
     */
    public List<ChatRoom> findAll() {
        String sql = "SELECT * FROM chat_room ORDER BY created_at DESC, room_id DESC";
        return jdbc.query(sql, this::mapRow);
    }

    //채팅방 삭제
    public void delete(Integer roomId) {
        String sql = "DELETE FROM chat_room WHERE room_id = ?";
        jdbc.update(sql, roomId);
    }
}
