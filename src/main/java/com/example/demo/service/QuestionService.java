package com.example.demo.service;

import com.example.demo.dto.RequestQuestionEnrolDto;
import com.example.demo.dto.RequestUpdateQuestionDto;
import com.example.demo.dto.RequestUpdateQuestionStateDto;
import com.example.demo.entity.Member;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuestionState;
import com.example.demo.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 전체 게시물 가져오기
     * question - member 페치 조인
     */
    public List<Question> getAllWithMember() {
        return questionRepository.findQuestionAllWithMember();
    }

    /**
     * 특정 게시물 가져오기
     * question - member 페치 조인
     */
    public Optional<Question> findQuestionWithMemberById(Long questionId) {
        return questionRepository.findQuestionById(questionId);
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deleteQuestion(Question question){
        questionRepository.delete(question.getId());
    }

    /**
     * 게시물 등록
     */
    @Transactional
    public Question save(RequestQuestionEnrolDto request, Member member) {
        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .createdDate(LocalDateTime.now())
                .editDate(null)
                .questionState(QuestionState.UNRESOLVED)
                .build();
        return questionRepository.save(question);
    }

    @Transactional
    public void update(RequestUpdateQuestionDto request) {
        questionRepository.update(request.getTitle(), request.getContent(), request.getId());
    }

    @Transactional
    public void updateState(RequestUpdateQuestionStateDto request) {
        questionRepository.updateState(request.getState(), request.getId());
    }
}
