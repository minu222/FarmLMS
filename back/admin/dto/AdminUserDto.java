package com.lms.urbangreen.urbangreenproject.admin.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {

    // all_users.user_id
    private Long userId;

    // all_users.user_type (admin / teacher / student)
    private String userType;

    // all_users.id (로그인 아이디)
    private String loginId;

    // all_users.name
    private String name;

    // all_users.nickname
    private String nickname;

    // all_users.email
    private String email;

    // all_users.birth
    private LocalDate birth;
}
