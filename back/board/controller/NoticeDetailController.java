package com.lms.urbangreen.urbangreenproject.board.controller;

import com.lms.urbangreen.urbangreenproject.board.dto.NoticeDto;
import com.lms.urbangreen.urbangreenproject.board.service.NoticeDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/board/notice")
@RequiredArgsConstructor
public class NoticeDetailController {

    private final NoticeDetailService noticeDetailService;

    /**
     * 공지사항 목록
     * GET /board/notice
     */
    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int total = noticeDetailService.getTotalCount(keyword);
        int totalPages = (total == 0) ? 0 : (int) Math.ceil((double) total / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        // 일반 공지 목록
        List<NoticeDto> notices = noticeDetailService.getNoticePage(keyword, page, size);

        // 상단 고정 공지 2개(최근 공지 2개 사용)
        List<NoticeDto> pinnedNotices = noticeDetailService.getPinnedNotices(2);
        Set<Long> pinnedIds = new HashSet<>();
        for (NoticeDto n : pinnedNotices) {
            n.setIsPinned(true);
            pinnedIds.add(n.getId());
        }

        // 일반 목록에서 고정 공지는 isPinned = true
        for (NoticeDto n : notices) {
            if (pinnedIds.contains(n.getId())) {
                n.setIsPinned(true);
            } else {
                n.setIsPinned(false);
            }
        }

        model.addAttribute("notices", notices);
        model.addAttribute("pinnedNotices", pinnedNotices);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("q", keyword);

        // templates/board/notice.html
        return "board/notice";
    }

    /**
     * 공지사항 상세
     * GET /board/notice/{id}
     */
    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {

        NoticeDto notice = noticeDetailService.getNotice(id);
        if (notice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다.");
        }

        model.addAttribute("notice", notice);
        return "board/noticeDetail";
    }

    /**
     * 공지사항 삭제
     * DELETE /board/notice/{id}
     * (JS에서 fetch(`/board/notice/${noticeId}`, { method: 'DELETE' }) 로 호출)
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        // TODO: 여기서 세션/권한 체크해서 관리자만 삭제 가능하도록 추가 가능
        noticeDetailService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 공지사항 수정 화면
     * GET /board/notice/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        // 기존 상세 조회 재사용 (조회수 1 올라가는 건 크게 문제 안 되면 그냥 사용)
        NoticeDto notice = noticeDetailService.getNotice(id);
        if (notice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다.");
        }

        model.addAttribute("notice", notice);

        // 관리자 수정 템플릿 사용
        return "admin/modifyNotice";
    }
}