package com.lms.urbangreen.urbangreenproject.admin.controller;

import com.lms.urbangreen.urbangreenproject.user.entity.User;
import com.lms.urbangreen.urbangreenproject.user.entity.UserType;
import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserManageController {

    @GetMapping("/adminUserManage")
    public String adminUserManage(HttpSession session,
                                  Model model,
                                  RedirectAttributes rttr) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        // 🔹 enum 타입 그대로 받아서 비교
        UserType userType = loginUser.getUserType();

        // enum 이름이 실제로 ADMIN인지, ROLE_ADMIN인지에 따라 아래 상수만 맞춰줘
        if (userType == null || userType != UserType.admin) {
            rttr.addFlashAttribute("msg", "관리자만 접근 가능합니다.");
            return "redirect:/";
        }

        model.addAttribute("admin", loginUser);
        return "admin/adminUserManage";
    }
}