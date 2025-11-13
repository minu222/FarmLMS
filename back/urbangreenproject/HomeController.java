package com.lms.urbangreen.urbangreenproject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping({"/", "/main"})
    public String showHomePage(org.springframework.ui.Model model) {
        model.addAttribute("courses", java.util.Collections.emptyList());
        return "main";
    }
}