package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequest {

    private String roomName;
}
