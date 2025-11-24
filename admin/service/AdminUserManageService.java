package com.lms.urbangreen.urbangreenproject.admin.service;

import com.lms.urbangreen.urbangreenproject.admin.dto.AdminUserDto;
import com.lms.urbangreen.urbangreenproject.admin.repository.AdminUserManageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserManageService {

    private final AdminUserManageRepository repository;

    /** 전체 개수 조회 (검색 포함) */
    public int count(String keyword) {
        return repository.count(keyword);
    }

    /** 페이징 목록 조회 */
    public List<AdminUserDto> findPage(int page, int size, String keyword) {
        return repository.findPage(page, size, keyword);
    }

    /** 선택 회원 삭제 */
    @Transactional
    public void deleteUsers(List<Long> userIds) {
        repository.deleteByIds(userIds);
    }

    /** 회원 구분 변경 */
    @Transactional
    public void changeUserType(Long userId, String userType) {
        repository.updateUserType(userId, userType);
    }
}
