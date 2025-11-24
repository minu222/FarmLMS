package com.lms.urbangreen.urbangreenproject.mypage.service;

public interface MypageAccountService {

    /** 현재 로그인 사용자의 계정을 삭제 (또는 탈퇴 처리) */
    void deleteAccount(int userId);
}
