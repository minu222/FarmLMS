package com.lms.urbangreen.urbangreenproject.chat.service;

import com.lms.urbangreen.urbangreenproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PresenceService {

    private final Set<Integer> onlineUserIds = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onConnect(SessionConnectEvent e) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(e.getMessage());
        Object userObj = sha.getSessionAttributes() != null ? sha.getSessionAttributes().get("loginUser") : null;
        if (userObj instanceof User u) {
            onlineUserIds.add(u.getUserId());
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(e.getMessage());
        Object userObj = sha.getSessionAttributes() != null ? sha.getSessionAttributes().get("loginUser") : null;
        if (userObj instanceof User u) {
            onlineUserIds.remove(u.getUserId());
        }
    }

    public Set<Integer> getOnlineUserIds() {
        return onlineUserIds;
    }
}
