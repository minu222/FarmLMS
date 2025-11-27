package com.lms.urbangreen.urbangreenproject.lecture.controller;


import com.lms.urbangreen.urbangreenproject.lecture.service.LectureSubService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe")
public class LectureSubController {

    private final LectureSubService lectureSubService;

    public LectureSubController(LectureSubService lectureSubService) {
        this.lectureSubService = lectureSubService;
    }

    /**
     * 강의 구독 API (POST /api/subscribe/{lectureId})
     */
    @PostMapping("/{lectureId}")
    public ResponseEntity<?> subscribe(@PathVariable int lectureId,
                                       @SessionAttribute(name = "loginUser", required = false) User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int userId = loginUser.getUserId();

        if (lectureSubService.subscribeLecture(userId, lectureId)) {
            return ResponseEntity.ok("강의 구독이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 구독된 강의입니다.");
        }
    }

    /**
     * 구독 상태 확인 API (GET /api/subscribe/status?lectureId={lectureId})
     */
    @GetMapping("/status")
    public ResponseEntity<Boolean> getSubscriptionStatus(@RequestParam int lectureId,
                                                         @SessionAttribute(name = "loginUser", required = false) User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.ok(false); // 비로그인 상태는 구독 아님
        }

        boolean isSubscribed = lectureSubService.isSubscribed(loginUser.getUserId(), lectureId);
        return ResponseEntity.ok(isSubscribed);
    }

    // 구독 취소
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<String> unsubscribe(@PathVariable int lectureId,
                                              @SessionAttribute(name = "loginUser", required = false) User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int userId = loginUser.getUserId();

        if (lectureSubService.unsubscribeLecture(userId, lectureId)) {
            return ResponseEntity.ok("강의 구독이 성공적으로 취소되었습니다.");
        } else {
            // 삭제된 행이 0개일 때 (원래 구독하지 않았던 강의)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 강의의 구독 정보를 찾을 수 없습니다.");
        }
    }
}