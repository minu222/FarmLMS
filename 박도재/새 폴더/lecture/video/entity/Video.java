package com.lms.urbangreen.urbangreenproject.lecture.video.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    private int video_id;
    private int lecture_id;
    private String video_title;
    private String video_url;
    private int video_time;
}
