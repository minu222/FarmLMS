package com.example.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamePageController {

    @GetMapping("/game")
    public String gamePage() {
        return "index";   // templates/index.html 반환
    }
}
