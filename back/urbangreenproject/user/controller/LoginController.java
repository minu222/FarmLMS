package com.lms.urbangreen.urbangreenproject.user.controller;

import com.lms.urbangreen.urbangreenproject.user.entity.User;
import com.lms.urbangreen.urbangreenproject.user.entity.UserType;
import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute User user, Model model) {
        user.setUserType(UserType.student);

        // 1. 아이디 중복 체크
        if (userService.existsById(user.getId())) {
            model.addAttribute("idError", "이미 존재하는 아이디입니다.");
            return "signup";
        }

        // 2. 닉네임 중복 체크
        if (userService.existsByNickname(user.getNickname())) {
            model.addAttribute("nicknameError", "이미 존재하는 닉네임입니다.");
            return "signup";
        }

        // 3. 비밀번호 조건 검사 (영문 + 숫자, 8자 이상)
        if (!isValidPassword(user.getPassword())) {
            model.addAttribute("passwordError", "비밀번호는 영문과 숫자를 포함한 8자 이상이어야 합니다.");
            return "signup";
        }

        // 4. 기본 회원 타입 설정
        if (user.getUserType() == null) {
            user.setUserType(UserType.student);
        }

        // 5. 회원 저장
        userService.register(user);

        return "redirect:/login";
    }

    // 비밀번호 조건 검사
    private boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }



    // 로그인
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, Model model, HttpSession session) {
        User found = userService.login(user.getId(), user.getPassword());
        if (found == null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 틀립니다.");
            return "login";
        }

        // ✅ 로그인 성공 시 세션 저장
        session.setAttribute("loginUser", found);
        return "redirect:/";
    }


    //  로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/main"; // 로그아웃 후 메인 페이지
    }
}

