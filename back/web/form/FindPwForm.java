package com.lms.urbangreen.urbangreenproject.user.web.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class FindPwForm {
    private String id;
    private String email;

}