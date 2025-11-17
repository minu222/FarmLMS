package com.lms.urbangreen.urbangreenproject.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DB: chat_room
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    private Integer roomId;      // room_id INT PK
    private Integer userId;      // user_id (방 만든 사람 - admin)
    private String roomName;     // room_name
    private LocalDateTime createdAt; // created_at

    public ChatRoom(Integer roomId, Integer userId, String roomName, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.roomName = roomName;
        this.createdAt = createdAt;
    }
}
