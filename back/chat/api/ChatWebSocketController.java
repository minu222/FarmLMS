package com.lms.urbangreen.urbangreenproject.chat.api;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMessage;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatMessagePayload;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatSendMessageRequest;
import com.lms.urbangreen.urbangreenproject.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * 클라이언트에서 /app/chat/{roomId} 로 보내는 메시지를 처리
     * -> /topic/room.{roomId} 로 브로드캐스트
     */
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessagePayload sendMessage(@DestinationVariable Integer roomId,
                                          ChatSendMessageRequest request) {

        if (request == null || request.getUserId() == null) {
            return null;
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return null;
        }

        // DB 저장 (roomId는 경로 변수 사용, payload 안의 roomId는 무시)
        ChatMessage saved = chatService.saveMessage(
                roomId,
                request.getUserId(),
                request.getContent().trim()
        );

        String createdAtStr;
        if (saved.getCreatedAt() != null) {
            createdAtStr = saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            createdAtStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        return new ChatMessagePayload(
                saved.getMessageId(),
                saved.getRoomId(),
                saved.getUserId(),
                saved.getContent(),
                createdAtStr
        );
    }
}
