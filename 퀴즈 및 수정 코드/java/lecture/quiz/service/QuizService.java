package com.lms.urbangreen.lecture.quiz.service;

import com.lms.urbangreen.lecture.quiz.entity.*;
import com.lms.urbangreen.lecture.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private static final int SCORE_PER_QUESTION = 20;

    // 퀴즈 목록 조회 (기존 로직 유지)
    public List<QuizResponseDto> getQuizList(int videoId) {
        // ... (기존 로직)
        List<Quiz> quizzes = quizRepository.findAllByVideoId(videoId);
        return quizzes.stream().map(q -> {
            QuizResponseDto dto = new QuizResponseDto();
            dto.setQuiz_id(q.getQuiz_id());
            dto.setQuestion(q.getQuestion());
            dto.setImg_url(q.getImg_url());
            return dto;
        }).collect(Collectors.toList());
    }

    // 퀴즈 제출 및 채점 (핵심 로직 변경: 반환 타입 QuizFinalResultDto로 변경)
    @Transactional
    public QuizFinalResultDto submitQuiz(int userId, QuizSubmissionRequest request) { // 반환 타입 변경
        int videoId = request.getVideo_id();
        LocalDateTime submitTime = LocalDateTime.now();

        List<Quiz> dbQuizzes = quizRepository.findAllByVideoId(videoId);

        if (dbQuizzes.isEmpty()) {
            throw new IllegalArgumentException("해당 비디오에 등록된 퀴즈가 없습니다.");
        }

        Map<Integer, Quiz> quizMap = dbQuizzes.stream()
                .collect(Collectors.toMap(Quiz::getQuiz_id, Function.identity()));

        int passThreshold = dbQuizzes.get(0).getPass_score();

        int totalEarnedScore = 0;
        int correctCount = 0; //  맞춘 개수 카운터
        List<IncorrectAnswerDetail> incorrectDetails = new ArrayList<>(); // 오답 상세 리스트

        // 4. 사용자 답안 순회하며 채점 및 Attempt 저장
        for (QuizSubmissionRequest.QuizAnswerDto answerDto : request.getAnswers()) {
            Quiz dbQuiz = quizMap.get(answerDto.getQuiz_id());

            if (dbQuiz == null) continue;

            // 퀴즈 정답: model_answer
            String modelAnswer = dbQuiz.getModel_answer().trim();
            // 사용자 답안: answer_text
            String userAnswerText = answerDto.getAnswer_text().trim();

            boolean isCorrect = modelAnswer.equalsIgnoreCase(userAnswerText);
            int earnedScore = isCorrect ? SCORE_PER_QUESTION : 0;

            totalEarnedScore += earnedScore;

            if (isCorrect) {
                correctCount++;
            } else {
                // 오답 상세 정보 저장: DTO의 필드명(correctAnswer, userAnswer)에 맞게 데이터 전달
                IncorrectAnswerDetail detail = new IncorrectAnswerDetail(
                        dbQuiz.getQuestion(),
                        dbQuiz.getModel_answer(), //  퀴즈 정답 (model_answer)
                        answerDto.getAnswer_text() // 사용자 답안 (answer_text)
                );
                incorrectDetails.add(detail);
            }

            // QuizAttempt 엔티티 생성 및 저장 (기존 로직 유지)
            QuizAttempt attempt = new QuizAttempt();
            attempt.setUser_id(userId);
            attempt.setQuiz_id(answerDto.getQuiz_id());
            attempt.setAnswer_text(answerDto.getAnswer_text());
            attempt.setEarned_score(earnedScore);
            attempt.setAttempted_at(submitTime);

            quizRepository.saveAttempt(attempt);
        }

        // 5. 최종 결과(Pass/Fail) 판단 (기존 로직 유지)
        QuizScore.pass passStatus = (totalEarnedScore >= passThreshold)
                ? QuizScore.pass.PASS
                : QuizScore.pass.FAIL;

        // 6. QuizScore 엔티티 생성 및 저장 (기존 로직 유지)
        QuizScore quizScore = new QuizScore();
        quizScore.setVideo_id(videoId);
        quizScore.setUser_id(userId);
        quizScore.setTotal_score(totalEarnedScore);
        quizScore.setPass(passStatus);

        quizRepository.saveScore(quizScore);

        // 7. 결과 DTO 반환 (QuizFinalResultDto 사용)
        QuizFinalResultDto result = new QuizFinalResultDto();

        // QuizResultDto 필드 채우기
        result.setTotal_score(totalEarnedScore);
        result.setPass_status(passStatus.name());
        result.setMessage(passStatus == QuizScore.pass.PASS
                ? "축하합니다! 합격하셨습니다."
                : "아쉽게도 불합격입니다. 다시 도전해보세요.");

        // QuizFinalResultDto의 추가 필드 채우기
        result.setTotal_count(dbQuizzes.size());
        result.setCorrect_count(correctCount);
        result.setIncorrect_details(incorrectDetails);

        return result;
    }
}