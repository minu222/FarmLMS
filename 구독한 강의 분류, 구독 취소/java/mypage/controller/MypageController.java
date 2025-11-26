package com.lms.urbangreen.urbangreenproject.mypage.controller;

import com.lms.urbangreen.urbangreenproject.lecture.entity.MySubscriptionLectureDto;
import com.lms.urbangreen.urbangreenproject.mypage.service.MyPageLectureService;
import com.lms.urbangreen.urbangreenproject.mypage.service.MypageAccountService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import com.lms.urbangreen.urbangreenproject.user.entity.UserType;
import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UserService userService;
    private final MypageAccountService mypageAccountService;
    private final MyPageLectureService myPageLectureService;

    // 생성자 주입
    public MypageController(UserService userService, MypageAccountService mypageAccountService,
                            MyPageLectureService myPageLectureService) {
        this.userService = userService;
        this.mypageAccountService = mypageAccountService;
        this.myPageLectureService = myPageLectureService;
    }

    @GetMapping({"", "/"})
    public String mypageRoot() {
        return "redirect:/mypage/profile";
    }

    private String currentLoginId(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj instanceof User u) return u.getId();
        Object loginId = session.getAttribute("loginId");
        return loginId instanceof String ? (String) loginId : null;
    }

    private String firstLetter(User u){
        String s = (u.getNickname()!=null && !u.getNickname().isBlank()) ? u.getNickname() : u.getName();
        return (s!=null && !s.isBlank()) ? s.substring(0,1).toUpperCase() : "U";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        String id = currentLoginId(session);
        if(id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("initial", firstLetter(user));
        model.addAttribute("active", "profile");
        return "mypage/profile";
    }

    @GetMapping("/edit")
    public String edit(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("active", "edit");
        return "mypage/edit";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String nickname,
                                @RequestParam String email,
                                @RequestParam(required=false) String birth,
                                @RequestParam(required=false) String intro,
                                HttpSession session,
                                Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        if (userService.existsByNicknameExcludingUser(nickname, id)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
            return "mypage/edit";
        }

        java.time.LocalDate birthDate = null;
        if (birth != null && !birth.isBlank()) {
            try { birthDate = java.time.LocalDate.parse(birth); }
            catch (Exception e) {
                model.addAttribute("user", userService.findById(id));
                model.addAttribute("active", "edit");
                model.addAttribute("error", "생년월일 형식이 올바르지 않습니다. (예: 2000-01-31)");
                return "mypage/edit";
            }
        }

        userService.updateProfile(id, name, nickname, email, birthDate, intro);
        return "redirect:/mypage/edit?success=1";
    }


    @PostMapping("/delete")
    public ResponseEntity<Void> deleteAccount(HttpSession session) {

        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User loginUser = (User) loginUserObj;
        UserType userType = loginUser.getUserType();
        if (userType == UserType.admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int userId = loginUser.getUserId();

        mypageAccountService.deleteAccount(userId);
        session.invalidate();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("pwError", "새 비밀번호와 확인이 일치하지 않습니다.");
            return "mypage/edit";
        }

        var result = userService.changePassword(id, currentPassword, newPassword);
        if (!result.success()) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("pwError", result.message());
            return "mypage/edit";
        }

        return "redirect:/mypage/edit?pwChanged=1";
    }

    /**
     * 내가 구독한 강의 목록 (진도율 포함)
     */
    @GetMapping("/subscriptions")
    public String subscriptions(HttpSession session, Model model) {
        User loginUser = getCurrentLoginUser(session);
        if (loginUser == null) return "redirect:/login";

        List<MySubscriptionLectureDto> subscribedLectures;
        try {
            // ⭐ MyPageLectureService를 사용하여 진도율이 포함된 DTO 목록 조회
            subscribedLectures = myPageLectureService.getSubscribedLecturesWithProgress(loginUser.getUserId());
        } catch (Exception e) {
            // DB 오류 등의 예외 처리 (실제 환경에서는 로깅 필요)
            subscribedLectures = Collections.emptyList();
        }

        // 2. 뷰에 데이터 전달
        model.addAttribute("lectures", subscribedLectures); // ⭐ DTO 타입 변경
        model.addAttribute("user", loginUser);
        model.addAttribute("active", "subscriptions");

        // 3. (옵션) JS에서 구독 취소 처리를 위해 구독 ID 목록 전달은 필요 없으므로 삭제했습니다.
        //    (DTO 자체가 충분한 정보를 가지고 있음)

        return "mypage/subscriptions";
    }


    @GetMapping("/posts")
    public String myCommentsPage(HttpSession session, Model model) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("active", "posts");
        return "mypage/posts";
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = !userService.existsByNickname(nickname);
        return Map.of("available", available);
    }

    private User getCurrentLoginUser(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        return loginUserObj instanceof User ? (User) loginUserObj : null;
    }
}