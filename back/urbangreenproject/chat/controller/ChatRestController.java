package com.lms.urbangreen.urbangreenproject.chat.controller;

import com.lms.urbangreen.urbangreenproject.chat.service.ChatService;
import com.lms.urbangreen.urbangreenproject.chat.service.PresenceService;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatDtos;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController {

    private final ChatService service;
    private final PresenceService presence;

    private int currentUserId(HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) throw new RuntimeException("Unauthorized");
        return u.getUserId();
    }

    @GetMapping("/api/rooms")
    public ChatDtos.RoomsResponse rooms(HttpSession session) {
        return service.getRoomsForUser(currentUserId(session));
    }

    @PostMapping("/rooms")
    public ChatDtos.RoomDto createRoom(@ModelAttribute ChatDtos.CreateRoomRequest req,
                                       HttpSession session) {
        int uid = currentUserId(session);
        long id = service.createRoom(req.getName(), req.getDescription(), uid);
        return ChatDtos.RoomDto.builder().id(id).name(req.getName()).desc(req.getDescription())
                .members(1).joined(true).build();
    }

    @PostMapping("/rooms/{roomId}/join")
    public void join(@PathVariable long roomId, HttpSession session) {
        service.join(roomId, currentUserId(session));
    }

    @PostMapping("/rooms/{roomId}/leave")
    public void leave(@PathVariable long roomId, HttpSession session) {
        service.leave(roomId, currentUserId(session));
    }

    @GetMapping("/api/rooms/{roomId}/messages")
    public java.util.List<ChatDtos.MessageOut> messages(@PathVariable long roomId,
                                                        @RequestParam(defaultValue = "50") int limit,
                                                        HttpSession session) {
        return service.getRecentMessages(roomId, currentUserId(session), Math.min(200, Math.max(1, limit)));
    }

    @GetMapping("/api/users")
    public ChatDtos.UsersResponse users(HttpSession session) {
        // 간단 구현: 온라인 사용자는 presence 목록, 오프라인은 생략
        var onlineIds = presence.getOnlineUserIds();
        // 닉네임 조회를 단순화: 필요 시 UserRepository로 이름 목록을 조인/조회해 반환
        var online = onlineIds.stream().map(id -> "user#" + id).toList();
        return new ChatDtos.UsersResponse(online, java.util.Collections.emptyList());
    }
}
