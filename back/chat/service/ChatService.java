package com.lms.urbangreen.urbangreenproject.chat.service;

import com.lms.urbangreen.urbangreenproject.chat.domain.ChatMessage;
import com.lms.urbangreen.urbangreenproject.chat.domain.ChatRoom;
import com.lms.urbangreen.urbangreenproject.chat.dto.RoomListResponse;
import com.lms.urbangreen.urbangreenproject.chat.repository.ChatMemberRepository;
import com.lms.urbangreen.urbangreenproject.chat.repository.ChatMessageRepository;
import com.lms.urbangreen.urbangreenproject.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 유저 기준 참여/미참여 방 목록
     */
    @Transactional(readOnly = true)
    public RoomListResponse getRoomLists(Integer userId) {
        List<ChatRoom> joined = chatRoomRepository.findJoinedRooms(userId);
        List<ChatRoom> others = chatRoomRepository.findOtherRooms(userId);
        return new RoomListResponse(joined, others);
    }

    /**
     * 방 생성 + 생성자 자동 입장
     */
    @Transactional
    public ChatRoom createRoom(Integer ownerUserId, String roomName) {
        Integer roomId = chatRoomRepository.createRoom(ownerUserId, roomName);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("방 생성 후 조회 실패"));

        // 방 만든 사람 자동 입장
        chatMemberRepository.joinRoom(roomId, ownerUserId);

        return room;
    }

    @Transactional
    public void deleteRoom(Integer roomId, Integer userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        // 방 만든 사람인지 확인
        if (!room.getUserId().equals(userId)) {
            throw new IllegalStateException("방 생성자만 삭제할 수 있습니다.");
        }

        chatRoomRepository.delete(roomId);
    }

    /**
     * 방 입장
     */
    @Transactional
    public void joinRoom(Integer userId, Integer roomId) {
        // 이미 참여중이면 아무 것도 안 함
        chatMemberRepository.findByRoomIdAndUserId(roomId, userId)
                .ifPresentOrElse(
                        m -> {},  // do nothing
                        () -> chatMemberRepository.joinRoom(roomId, userId)
                );
    }

    /**
     * 방 나가기
     */
    @Transactional
    public void leaveRoom(Integer userId, Integer roomId) {
        chatMemberRepository.leaveRoom(roomId, userId);
    }

    /**
     * 메시지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Integer roomId, int limit) {
        return chatMessageRepository.findByRoomId(roomId, limit);
    }



    /**
     * 메시지 저장 (다음 단계 WebSocket에서 사용 예정)
     */
    @Transactional
    public ChatMessage saveMessage(Integer roomId, Integer userId, String content) {
        Integer messageId = chatMessageRepository.save(roomId, userId, content);
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("메시지 저장 후 조회 실패"));
    }
}
