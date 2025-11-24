package com.lms.urbangreen.urbangreenproject.mypage.repository;

public interface MypageAccountRepository {

    /** user_id 기준으로 회원 삭제 */
    void deleteUser(int userId);
}
