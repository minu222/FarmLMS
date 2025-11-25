package com.lms.urbangreen.lecture.quiz.entity;

import lombok.Data;

@Data
public class Quiz {
    private int quiz_id;
    private int video_id;
    private String img_url;
    private String question;
    private String model_answer;
    private int pass_score;
}