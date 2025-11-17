package com.lms.urbangreen.urbangreenproject.chat.service;

import com.lms.urbangreen.urbangreenproject.chat.dto.PresenceUserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 방별 온라인 사용자 목록을 메모리에 유지 (데모용)
 */
@Service
public class ChatPresenceService {

    // roomId -> (userId -> nickname)
    private final Map<Integer, Map<Integer, String>> onlineMap = new ConcurrentHashMap<>();

    public List<PresenceUserDto> update(Integer roomId,
                                        Integer userId,
                                        String nickname,
                                        String type) {

        onlineMap.putIfAbsent(roomId, new ConcurrentHashMap<>());
        Map<Integer, String> users = onlineMap.get(roomId);

        if ("JOIN".equalsIgnoreCase(type)) {
            users.put(userId, nickname);
        } else if ("LEAVE".equalsIgnoreCase(type)) {
            users.remove(userId);
        }

        return users.entrySet().stream()
                .map(e -> new PresenceUserDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
