package com.lms.urbangreen.urbangreenproject.admin.controller;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminNoticeListDto;
import com.lms.urbangreen.urbangreenproject.admin.service.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    /**
     * 관리자 공지사항 목록
     * GET /admin/notices
     */
    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int totalCount = adminNoticeService.getTotalCount(keyword);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<AdminNoticeListDto> notices = adminNoticeService.getNoticePage(keyword, page, size);

        model.addAttribute("notices", notices);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        // templates/admin/adminNotice.html
        return "admin/adminNotice";
    }

    /**
     * 공지 등록 페이지
     * GET /admin/notices/new
     */
    @GetMapping("/new")
    public String showCreateForm() {
        // templates/admin/addNotice.html
        return "admin/addNotice";
    }

    /**
     * 공지 등록 처리
     * POST /admin/notices
     * addNotice.html 의 fetch('/admin/notices', ...) 와 매핑
     */
    @PostMapping
    @ResponseBody
    public String createNotice(@RequestParam("category") String category,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        // TODO: 실제 로그인한 관리자 ID로 교체 (지금은 임시로 1번 관리자 사용)
        int adminUserId = 1;

        adminNoticeService.createNotice(adminUserId, category, title, content, files);

        // fetch 에서 res.ok 체크 후 목록으로 이동하므로 단순 OK 텍스트만 반환
        return "OK";
    }

    /**
     * 선택 삭제
     * POST /admin/notices/delete
     */
    @PostMapping("/delete")
    public String delete(@RequestParam(name = "noticeIds", required = false) List<Long> noticeIds,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "q", required = false) String keyword,
                         RedirectAttributes redirectAttributes) {

        if (noticeIds != null && !noticeIds.isEmpty()) {
            adminNoticeService.deleteNotices(noticeIds);
        }

        redirectAttributes.addAttribute("page", page);
        if (keyword != null && !keyword.isBlank()) {
            redirectAttributes.addAttribute("q", keyword);
        }

        return "redirect:/admin/notices";
    }

    /**
     * 공지 수정 처리
     * POST /admin/notices/{id}
     * admin/modifyNotice.html 의 fetch(submitUrl, ...) 과 매핑
     */
    @PostMapping("/{id}")
    @ResponseBody
    public String updateNotice(@PathVariable("id") Long id,
                               @RequestParam("category") String category,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam(value = "currentImgUrl", required = false) String currentImgUrl,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        adminNoticeService.updateNotice(id, category, title, content, currentImgUrl, files);

        return "OK";
    }
}