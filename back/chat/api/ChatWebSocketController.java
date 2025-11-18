package com.lms.urbangreen.urbangreenproject.chat.api;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMessage;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatMessagePayload;
import com.lms.urbangreen.urbangreenproject.chat.dto.ChatSendMessageRequest;
import com.lms.urbangreen.urbangreenproject.chat.repository.AllUsersRepository;
import com.lms.urbangreen.urbangreenproject.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final AllUsersRepository allUsersRepository;

    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @MessageMapping("/chat.send")      // 클라: /app/chat.send
    @SendTo("/topic/chat")            // 클라: /topic/chat 구독
    public ChatMessagePayload sendMessage(ChatSendMessageRequest request) {

        if (request == null ||
                request.getRoomId() == null ||
                request.getUserId() == null) {
            return null;
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return null;
        }

        Integer roomId = request.getRoomId();
        Integer userId = request.getUserId();
        String content = request.getContent().trim();

        // 1) DB 저장 → message_id
        Integer messageId = chatService.saveMessage(roomId, userId, content);

        // 2) DB에서 닉네임 조회 (all_users)
        String nickname = allUsersRepository.findNicknameByUserId(userId);

        // 3) 시간 문자열
        String createdAtStr = LocalDateTime.now().format(ISO_FMT);

        // 4) 클라이언트로 보낼 payload
        return new ChatMessagePayload(
                messageId,
                roomId,
                userId,
                nickname,
                content,
                createdAtStr
        );
    }
}
