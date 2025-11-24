package com.lms.urbangreen.urbangreenproject.admin.service;


import com.lms.urbangreen.urbangreenproject.admin.dto.AdminNoticeListDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminNoticeService {

    List<AdminNoticeListDto> getNoticePage(String keyword, int page, int size);

    int getTotalCount(String keyword);

    void deleteNotices(List<Long> noticeIds);

    /**
     * 공지사항 신규 등록
     *
     * @param userId   작성자(관리자) user_id
     * @param category 고정/자유 공지 (현재 DB에는 저장하지 않고 추후 확장용)
     * @param title    제목
     * @param content  내용
     * @param files    첨부 파일 목록 (현재는 저장하지 않음)
     * @return 생성된 notice_id (PK)
     */
    Long createNotice(int userId, String category, String title, String content, List<MultipartFile> files);
    // 🔥 공지 수정
    /** 공지 수정 (기존 이미지 URL 포함) */
    void updateNotice(Long noticeId,
                      String category,
                      String title,
                      String content,
                      String currentImgUrl,
                      List<MultipartFile> files);
}
