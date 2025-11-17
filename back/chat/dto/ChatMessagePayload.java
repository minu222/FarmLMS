package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * STOMP로 클라이언트들에게 나가는 메시지 payload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePayload {

    private Integer messageId;
    private Integer roomId;
    private Integer userId;
    private String content;
    private String createdAt;  // ISO 문자열(프론트에서 new Date()로 파싱해서 사용)
}
