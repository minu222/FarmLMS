package com.lms.urbangreen.urbangreenproject.user.controller;

import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-id")
    public Map<String, Boolean> checkId(@RequestParam String id) {
        return Map.of("exists", userService.existsById(id));
    }

    @GetMapping("/check-nickname")
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        return Map.of("exists", userService.existsByNickname(nickname));
    }
}
