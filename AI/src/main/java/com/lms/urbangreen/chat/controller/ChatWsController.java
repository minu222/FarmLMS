package com.lms.urbangreen.chat.controller;

import com.lms.urbangreen.chat.dto.ChatDtos;
import com.lms.urbangreen.chat.service.ChatService;
import com.lms.urbangreen.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService service;
    private final SimpMessagingTemplate messaging;

    @MessageMapping("room.{roomId}.send")
    public void send(@DestinationVariable long roomId,
                     ChatDtos.MessageIn payload,
                     SimpMessageHeaderAccessor headers) {

        Object userObj = headers.getSessionAttributes() != null ? headers.getSessionAttributes().get("loginUser") : null;
        if (!(userObj instanceof User u)) return;

        long msgId = service.saveMessage(roomId, u.getUserId(), payload.getContent());

        var out = ChatDtos.MessageOut.builder()
                .id(msgId)
                .roomId(roomId)
                .senderId(u.getUserId())
                .senderName(u.getNickname() != null ? u.getNickname() : u.getName())
                .content(payload.getContent())
                .createdAt(LocalDateTime.now())
                .mine(false) // 브로드캐스트용: 클라이언트에서 mine 처리 가능
                .build();

        messaging.convertAndSend("/topic/room." + roomId, out);
    }
}
