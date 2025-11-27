package com.lms.urbangreen.urbangreenproject.teacher.quiz.repository;

import com.lms.urbangreen.urbangreenproject.teacher.quiz.domain.Quiz;

import java.util.List;

public interface QuizRepository {

    List<Quiz> findByVideoId(Long videoId);

    void deleteByVideoId(Long videoId);

    void save(Quiz quiz);
}