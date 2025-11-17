package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 온라인 상태 변경 요청 (JOIN / LEAVE)
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatPresenceMessage {

    private Integer roomId;
    private Integer userId;
    private String nickname;
    private String type; // "JOIN" 또는 "LEAVE"
}
