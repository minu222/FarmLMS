package com.lms.urbangreen.mypage.controller;

import com.lms.urbangreen.user.entity.User;
import com.lms.urbangreen.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UserService userService;
    public MypageController(UserService userService) { this.userService = userService; }

    // 루트는 프로필로 리다이렉트
    @GetMapping({"", "/"})
    public String mypageRoot() {
        return "redirect:/mypage/profile";
    }

    // 공통: 세션 사용자 꺼내기 (loginUser 우선, 없으면 loginId 호환)
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


    //내 프로필
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        String id = currentLoginId(session);
        if(id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("initial", firstLetter(user));   // ✅
        model.addAttribute("active", "profile");
        return "mypage/profile";
    }

    // GET: 내 정보수정 화면
    @GetMapping("/edit")
    public String edit(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("active", "edit"); // 사이드바 active
        return "mypage/edit"; // ↔ templates/mypage/edit.html
    }

    // POST: 프로필(이름/닉네임/이메일/생년월일/소개) 저장
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String nickname,
                                @RequestParam String email,
                                @RequestParam(required=false) String birth, // "" 가능
                                @RequestParam(required=false) String intro,
                                HttpSession session,
                                Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        // 닉네임 중복(본인 제외) 체크
        if (userService.existsByNicknameExcludingUser(nickname, id)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
            return "mypage/edit";
        }

        // birth 파싱 (빈값 → null)
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

    // POST: 비밀번호 변경
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

    //내가 구독한 강의

    @GetMapping("/subscriptions")
    public String subscriptions(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "mypage/subscriptions";
    }


    // 내 댓글 보기
    @GetMapping("/posts")
    public String posts(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "mypage/posts";
    }

}
