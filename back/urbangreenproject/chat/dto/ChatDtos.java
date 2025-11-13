package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ChatDtos {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class RoomDto {
        private long id;
        private String name;
        private String desc;
        private int members;
        private boolean joined;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class RoomsResponse {
        private java.util.List<RoomDto> joined;
        private java.util.List<RoomDto> others;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class MessageOut {
        private long id;
        private long roomId;
        private int senderId;
        private String senderName;
        private String content;
        private LocalDateTime createdAt;
        private boolean mine;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class MessageIn {
        private long roomId;
        private String content;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class CreateRoomRequest {
        private String name;
        private String description;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class UsersResponse {
        private java.util.List<String> online;
        private java.util.List<String> offline;
    }
}
