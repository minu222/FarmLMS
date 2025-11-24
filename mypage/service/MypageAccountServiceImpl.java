package com.lms.urbangreen.urbangreenproject.mypage.service;

import com.lms.urbangreen.urbangreenproject.mypage.repository.MypageAccountRepository;
import com.lms.urbangreen.urbangreenproject.mypage.repository.MypageAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MypageAccountServiceImpl implements MypageAccountService {

    private final MypageAccountRepository mypageAccountRepository;

    @Override
    @Transactional
    public void deleteAccount(int userId) {
        // TODO: 연관 데이터(수강내역, 게시글 등)를 함께 지우거나
        //      "탈퇴" 플래그만 업데이트하는 구조로 변경 가능.
        mypageAccountRepository.deleteUser(userId);
    }
}
