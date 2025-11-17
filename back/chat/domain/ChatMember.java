package com.lms.urbangreen.urbangreenproject.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DB: chat_member
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMember {

    private Integer memberId;     // member_id INT PK
    private Integer roomId;       // room_id
    private Integer userId;       // user_id
    private LocalDateTime joinedAt; // joined_at

    public ChatMember(Integer memberId, Integer roomId, Integer userId, LocalDateTime joinedAt) {
        this.memberId = memberId;
        this.roomId = roomId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }
}
