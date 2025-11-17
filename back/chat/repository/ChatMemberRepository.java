package com.lms.urbangreen.urbangreenproject.chat.repository;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMember;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMemberRepository {

    private final JdbcTemplate jdbc;

    private ChatMember mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ChatMember member = new ChatMember();
        member.setMemberId(rs.getInt("member_id"));
        member.setRoomId(rs.getInt("room_id"));
        member.setUserId(rs.getInt("user_id"));

        java.sql.Timestamp ts = rs.getTimestamp("joined_at");
        member.setJoinedAt(ts != null ? ts.toLocalDateTime() : null);

        return member;
    }

    /**
     * 채팅방 입장 (member 추가)
     */
    public void joinRoom(Integer roomId, Integer userId) {
        String sql = "INSERT INTO chat_member (room_id, user_id, joined_at) VALUES (?, ?, NOW())";
        jdbc.update(sql, roomId, userId);
    }

    /**
     * 채팅방 나가기 (member 삭제)
     */
    public void leaveRoom(Integer roomId, Integer userId) {
        String sql = "DELETE FROM chat_member WHERE room_id = ? AND user_id = ?";
        jdbc.update(sql, roomId, userId);
    }

    /**
     * 해당 방의 멤버 목록
     */
    public List<ChatMember> findByRoomId(Integer roomId) {
        String sql = "SELECT * FROM chat_member WHERE room_id = ? ORDER BY joined_at ASC, member_id ASC";
        return jdbc.query(sql, this::mapRow, roomId);
    }

    /**
     * 특정 유저가 특정 방에 이미 있는지 체크
     */
    public Optional<ChatMember> findByRoomIdAndUserId(Integer roomId, Integer userId) {
        String sql = "SELECT * FROM chat_member WHERE room_id = ? AND user_id = ?";
        List<ChatMember> list = jdbc.query(sql, this::mapRow, roomId, userId);
        return list.stream().findFirst();
    }
}
