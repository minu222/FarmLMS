package com.lms.urbangreen.urbangreenproject.lecture.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {

    public enum category {
        GARDENING, FIELD, HOUSE
    }

    public enum sub_category {
        SEED,GROW,SHIP
    }

    private int lecture_id;
    private int user_id;
    private category category;
    private sub_category sub_category;
    private String img_url;
    private String title;
    private String content;
    private int subs_count;
    private LocalDateTime created_at;

}
