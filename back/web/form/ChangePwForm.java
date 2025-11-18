package com.lms.urbangreen.urbangreenproject.user.web.form;

import lombok.Data;

@Data
public class ChangePwForm {
    private String newPassword;
    private String confirmPassword;
}