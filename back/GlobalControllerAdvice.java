package com.lms.urbangreen.urbangreenproject;

import com.lms.urbangreen.urbangreenproject.model.User;
import com.lms.urbangreen.urbangreenproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("loginUser")
    public User addLoginUserToModel(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId != null) {
            return userService.findById(loginId);
        }
        return null;
    }
}