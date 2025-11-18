package com.lms.urbangreen.urbangreenproject.chat.api;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMessage;
import com.lms.urbangreen.urbangreenproject.chat.domain.ChatRoom;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatMessagePayload;
import com.lms.urbangreen.urbangreenproject.chat.dto.CreateRoomRequest;
import com.lms.urbangreen.urbangreenproject.chat.dto.RoomListResponse;
import com.lms.urbangreen.urbangreenproject.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    /**
     * 참여/미참여 채팅방 목록
     * ex) GET /api/chat/rooms?userId=3
     */
    @GetMapping("/rooms")
    public RoomListResponse getRooms(@RequestParam("userId") Integer userId) {
        return chatService.getRoomLists(userId);
    }

    /**
     * 새 채팅방 생성
     * ex) POST /api/chat/rooms?userId=1
     * body: { "roomName": "스터디방" }
     */
    @PostMapping("/rooms")
    public ChatRoom createRoom(@RequestParam("userId") Integer userId,
                               @RequestBody CreateRoomRequest request) {

        return chatService.createRoom(userId, request.getRoomName());
    }

    @DeleteMapping("/rooms/{roomId}")
    public void deleteRoom(@PathVariable("roomId") Integer roomId,
                           @RequestParam("userId") Integer userId) {
        chatService.deleteRoom(roomId, userId);
    }

    /**
     * 채팅방 입장
     * ex) POST /api/chat/rooms/5/join?userId=3
     */
    @PostMapping("/rooms/{roomId}/join")
    public void joinRoom(@PathVariable("roomId") Integer roomId,
                         @RequestParam("userId") Integer userId) {

        chatService.joinRoom(userId, roomId);
    }

    /**
     * 채팅방 나가기
     * ex) POST /api/chat/rooms/5/leave?userId=3
     */
    @PostMapping("/rooms/{roomId}/leave")
    public void leaveRoom(@PathVariable("roomId") Integer roomId,
                          @RequestParam("userId") Integer userId) {

        chatService.leaveRoom(userId, roomId);
    }

    /** 방 메시지 목록 (최근 limit개) */
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessagePayload> getMessages(
            @PathVariable Integer roomId,
            @RequestParam(name = "limit", defaultValue = "100") int limit
    ) {
        return chatService.getRecentMessages(roomId, limit);
    }
}
