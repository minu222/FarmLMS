package com.lms.urbangreen.urbangreenproject.board.controller;

import com.lms.urbangreen.urbangreenproject.board.service.NoticeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) {
        this.service = service;
    }

    /* 목록 */
    @GetMapping("/board/notice")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String q,
                       Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        var result = service.getPage(page, size, q);
        int totalPages = (int)Math.ceil(result.total() / (double) result.size());

        model.addAttribute("notices", result.items());
        model.addAttribute("total", result.total());
        model.addAttribute("page", result.page());
        model.addAttribute("size", result.size());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("q", q == null ? "" : q);

        return "board/notice"; // templates/board/notice.html
    }

    /* 상세 */
    @GetMapping("/board/notice/{id}")
    public String detail(@PathVariable int id, Model model) {
        var notice = service.getDetailAndIncreaseView(id);
        if (notice == null) {
            // 필요시 404 페이지로
            return "redirect:/board/notice";
        }
        model.addAttribute("notice", notice);
        model.addAttribute("pageTitle", notice.title());
        return "board/notice-detail"; // 템플릿 추가 예시(아래 참고)
    }
}
