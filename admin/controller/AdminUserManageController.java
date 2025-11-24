package com.lms.urbangreen.urbangreenproject.admin.controller;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminUserDto;
import com.lms.urbangreen.urbangreenproject.admin.service.AdminUserManageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminUserManageController {

    private final AdminUserManageService adminUserManageService;

    /**
     * 관리자 회원관리 목록 + 검색
     * GET /admin/users?page=1&size=10&q=검색어
     */
    @GetMapping("/admin/adminUserManage")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        int totalCount = adminUserManageService.count(keyword);

        if (page < 1) page = 1;
        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<AdminUserDto> users = adminUserManageService.findPage(page, size, keyword);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        // templates/admin/adminUserManage.html
        return "admin/adminUserManage";
    }

    /**
     * 선택 삭제
     */
    @PostMapping("/admin/adminUserManage/delete")
    public String delete(@RequestParam(name = "userIds", required = false) List<Long> userIds,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "q", required = false) String keyword,
                         RedirectAttributes redirectAttributes) {

        adminUserManageService.deleteUsers(userIds);

        redirectAttributes.addAttribute("page", page);
        if (keyword != null && !keyword.isBlank()) {
            redirectAttributes.addAttribute("q", keyword);
        }
        return "redirect:/admin/adminUserManage";
    }

    /**
     * 회원 구분 변경 (AJAX)
     */
    @PostMapping("/admin/adminUserManage/updateRole")
    @ResponseBody
    public String updateRole(@RequestBody UpdateRoleRequest request) {
        adminUserManageService.changeUserType(request.getUserId(), request.getUserType());
        return "OK";
    }

    @Data
    public static class UpdateRoleRequest {
        private Long userId;     // all_users.user_id
        private String userType; // admin / teacher / student
    }
}
