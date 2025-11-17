package com.lms.urbangreen.urbangreenproject.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DB: chat_message
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    private Integer messageId;      // message_id INT PK
    private Integer roomId;         // room_id
    private Integer userId;         // user_id
    private String content;         // content
    private LocalDateTime createdAt; // created_at

    public ChatMessage(Integer messageId, Integer roomId, Integer userId, String content, LocalDateTime createdAt) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
