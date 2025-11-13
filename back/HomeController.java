package com.lms.urbangreen.urbangreenproject.controller;

import com.lms.urbangreen.urbangreenproject.model.LectureListItem;
import com.lms.urbangreen.urbangreenproject.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LectureService lectureService;

    @GetMapping({"/", "/main"})
    public String showHomePage(Model model) {
        // ✅ 최신순으로 8개만 가져오기 (필요에 따라 개수 조절)
        var pageResult = lectureService.getLecturePage(1, 8, "newest", null, null);
        List<LectureListItem> lectures = pageResult.items();

        model.addAttribute("courses", lectures);
        model.addAttribute("pageTitle", "UrbanGreen - Main");
        return "main"; // templates/main.html
    }
}