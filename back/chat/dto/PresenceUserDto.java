package com.lms.urbangreen.urbangreenproject.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 온라인 사용자 목록에 사용할 DTO
 */
@Getter
@AllArgsConstructor
public class PresenceUserDto {

    private Integer userId;
    private String nickname;
}
