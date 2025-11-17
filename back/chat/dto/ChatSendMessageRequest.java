package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * STOMP로 들어오는 채팅 전송 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatSendMessageRequest {

    private Integer roomId;
    private Integer userId;
    private String content;
}
