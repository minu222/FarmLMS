package com.lms.urbangreen.urbangreenproject.chat.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatPageController {

    /**
     * 채팅 화면
     * GET /chat  → templates/chat/chat.html 렌더링
     */
    @GetMapping
    public String chatPage(HttpSession session) {
        // 로그인 체크 / 세션 사용이 필요하면 여기서 처리 가능
        // Object loginUser = session.getAttribute("loginUser");
        // if (loginUser == null) { return "redirect:/login"; }

        return "board/chat";  // templates/chat/chat.html 을 의미
    }
}
