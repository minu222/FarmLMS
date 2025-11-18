package com.lms.urbangreen.chat.service;

import com.lms.urbangreen.chat.dto.ChatDtos;
import com.lms.urbangreen.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository repo;

    public long createRoom(String name, String desc, int creatorUserId) {
        long roomId = repo.createRoom(name, desc, creatorUserId);
        repo.joinRoom(roomId, creatorUserId, true); // 방 만든 사람은 ADMIN + 자동가입
        return roomId;
    }

    public void join(long roomId, int userId) {
        repo.joinRoom(roomId, userId, false);
    }

    public void leave(long roomId, int userId) {
        repo.leaveRoom(roomId, userId);
    }

    public ChatDtos.RoomsResponse getRoomsForUser(int userId) {
        List<ChatDtos.RoomDto> joined = repo.findJoinedRooms(userId);
        List<ChatDtos.RoomDto> others = repo.findOtherRooms(userId);
        return new ChatDtos.RoomsResponse(joined, others);
    }

    public List<ChatDtos.MessageOut> getRecentMessages(long roomId, int userId, int limit) {
        return repo.findRecentMessages(roomId, userId, limit);
    }

    public long saveMessage(long roomId, int userId, String content) {
        return repo.saveMessage(roomId, userId, content);
    }
}
