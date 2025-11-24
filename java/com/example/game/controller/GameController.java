package com.example.game.controller;

import com.example.game.dto.GameSaveRequest;
import com.example.game.entity.GameEntity;
import com.example.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 저장
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody GameSaveRequest req) {
        req.setUserId(1); // 테스트용 유저아이디 1로 강제 실제 로그인해서 할때는 삭제
        gameService.save(req);
        return ResponseEntity.ok("success");
    }

    // 이어하기
    @GetMapping("/load")
    public ResponseEntity<GameEntity> load(@RequestParam Integer userId) {
        return ResponseEntity.ok(gameService.loadLatest(userId));
    }
}
