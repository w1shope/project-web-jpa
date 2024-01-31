package com.example.demo.service;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Member;
import com.example.demo.entity.Question;
import com.example.demo.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Optional<List<Answer>> findAnswerWithQuestionAll(Long questionId) {
        return answerRepository.findAnswerWithQuestion(questionId);
    }

    @Transactional
    public void enroll(Member loginMember, Question question, String answerContent) {
        Answer answer = Answer.builder()
                .createdDate(LocalDateTime.now())
                .member(loginMember)
                .editDate(null)
                .question(question)
                .content(answerContent)
                .build();
        answerRepository.save(answer);
    }

    public Optional<Answer> findAnswer(Long questionId, String nickName, LocalDateTime answerCreatedDate) {
        return answerRepository.findAnswerWithMemberAndQuestion(questionId, nickName, answerCreatedDate);
    }

    @Transactional
    public void updateContent(Answer answer, String updateContent) {
        answerRepository.updateContent(updateContent, answer.getId());
    }

    @Transactional
    public void deleteAnswer(Answer answer) {
        answerRepository.delete(answer);
    }
}
