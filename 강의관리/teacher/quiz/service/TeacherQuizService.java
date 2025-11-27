package com.lms.urbangreen.urbangreenproject.teacher.quiz.service;

import com.lms.urbangreen.urbangreenproject.teacher.quiz.domain.Quiz;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.QuizSaveRequest;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherQuizService {

    private final QuizRepository quizRepository;

    public TeacherQuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<Quiz> getQuizzesByVideoId(Long videoId) {
        return quizRepository.findByVideoId(videoId);
    }

    @Transactional
    public void replaceQuizzes(Long videoId, List<QuizSaveRequest> requests) {
        // 기존 퀴즈 싹 지우고 다시 INSERT (등록/수정을 동일 로직으로 처리)
        quizRepository.deleteByVideoId(videoId);

        int quizNumber = 1;
        for (QuizSaveRequest req : requests) {
            Quiz quiz = new Quiz();
            quiz.setVideoId(videoId);
            quiz.setQuizNumber(quizNumber++);
            quiz.setImgUrl(null);  // 지금은 이미지 사용 X
            quiz.setQuestion(req.getQuestion());
            quiz.setModelAnswer(req.getModelAnswer());
            quiz.setPassScore(
                    (req.getPassScore() != null && req.getPassScore() > 0)
                            ? req.getPassScore()
                            : 60 // 기본값
            );
            quizRepository.save(quiz);
        }
    }

    public void deleteByVideoId(Long videoId) {
        quizRepository.deleteByVideoId(videoId);
    }
}