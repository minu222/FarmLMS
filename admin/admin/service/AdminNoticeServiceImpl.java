package com.lms.urbangreen.urbangreenproject.admin.service;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminNoticeListDto;
import com.lms.urbangreen.urbangreenproject.admin.repository.AdminNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeRepository adminNoticeRepository;

    // 🔥 업로드 경로/URL prefix (application.properties에 없으면 디폴트값 사용)
    @Value("${file.notice-upload-dir:uploads/notice}")
    private String noticeUploadDir;

    @Value("${file.notice-url-prefix:/uploads/notice}")
    private String noticeUrlPrefix;

    @Override
    @Transactional(readOnly = true)
    public List<AdminNoticeListDto> getNoticePage(String keyword, int page, int size) {
        return adminNoticeRepository.findPage(keyword, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCount(String keyword) {
        return adminNoticeRepository.count(keyword);
    }

    @Override
    @Transactional
    public void deleteNotices(List<Long> noticeIds) {
        adminNoticeRepository.deleteByIds(noticeIds);
    }

    @Override
    @Transactional
    public Long createNotice(int userId, String category, String title, String content, List<MultipartFile> files) {

        boolean isPinned = "fixedNotice".equals(category);

        // 🔥 새로 올린 이미지 저장 후 URL 반환
        String imgUrl = saveImageAndGetUrl(files);

        return adminNoticeRepository.insertNotice(userId, title, content, isPinned, imgUrl);
    }

    @Override
    @Transactional
    public void updateNotice(Long noticeId,
                             String category,
                             String title,
                             String content,
                             String currentImgUrl,
                             List<MultipartFile> files) {

        boolean isPinned = "fixedNotice".equals(category);

        // 기본값은 기존 이미지
        String imgUrl = currentImgUrl;

        // 새 파일이 올라오면 새 파일로 교체
        String newImgUrl = saveImageAndGetUrl(files);
        if (newImgUrl != null) {
            imgUrl = newImgUrl;
        }

        adminNoticeRepository.updateNotice(noticeId, title, content, isPinned, imgUrl);
    }

    /**
     * 파일 리스트 중 첫 번째 파일을 저장하고, 브라우저에서 접근 가능한 URL을 반환.
     * 파일이 없으면 null 리턴.
     */
    private String saveImageAndGetUrl(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        MultipartFile file = files.get(0);
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadRoot = Paths.get(noticeUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadRoot);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int dot = originalFilename.lastIndexOf('.');
            if (dot != -1) {
                ext = originalFilename.substring(dot);
            }

            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = uploadRoot.resolve(filename);

            file.transferTo(target.toFile());

            String prefix = noticeUrlPrefix;
            if (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }

            // 예: /uploads/notice/xxxxxx.png
            return prefix + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("공지 이미지 저장 중 오류가 발생했습니다.", e);
        }
    }
}