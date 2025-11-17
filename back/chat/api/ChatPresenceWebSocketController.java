package com.lms.urbangreen.urbangreenproject.chat.api;

import com.lms.urbangreen.urbangreenproject.chat.dto.ChatPresenceMessage;
import com.lms.urbangreen.urbangreenproject.chat.dto.PresenceUserDto;
import com.lms.urbangreen.urbangreenproject.chat.service.ChatPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatPresenceWebSocketController {

    private final ChatPresenceService presenceService;

    /**
     * /app/presence/{roomId} 로 JOIN/LEAVE가 들어오면
     * /topic/presence.{roomId} 로 현재 온라인 사용자 리스트 전체를 브로드캐스트
     */
    @MessageMapping("/presence/{roomId}")
    @SendTo("/topic/presence.{roomId}")
    public List<PresenceUserDto> handlePresence(@DestinationVariable Integer roomId,
                                                ChatPresenceMessage message) {

        if (message == null ||
                message.getUserId() == null ||
                message.getNickname() == null) {
            return Collections.emptyList();
        }

        String type = message.getType() != null ? message.getType() : "JOIN";
        return presenceService.update(roomId, message.getUserId(), message.getNickname(), type);
    }
}
