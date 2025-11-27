package com.lms.urbangreen.urbangreenproject.teacher.quiz.dto;

public class QuizSaveRequest {

    private String question;     // quiz.question
    private String modelAnswer;  // quiz.model_answer
    private Integer passScore;   // quiz.pass_score (null이면 60으로 처리)

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public void setModelAnswer(String modelAnswer) {
        this.modelAnswer = modelAnswer;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }
}