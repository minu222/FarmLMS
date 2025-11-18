package com.lms.urbangreen.user.entity;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int userId;
    private UserType userType;
    private String id; // 로그인 아이디
    private String password;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String email;
    private String intro; // 자기소개 추가
}