package com.lms.urbangreen.urbangreenproject.chat.dto;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 참여/미참여 채팅방 목록 응답 DTO
 */
@Getter
@AllArgsConstructor
public class RoomListResponse {

    private List<ChatRoom> joined;  // 참여 중인 방
    private List<ChatRoom> others;  // 아직 참여 안 한 방
}
