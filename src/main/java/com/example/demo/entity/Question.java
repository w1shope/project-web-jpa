package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id", "title", "content", "createdDate", "editDate", "questionState"})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "question_title")
    private String title; // 제목
    @Column(name = "question_content")
    private String content; // 내용
    private LocalDateTime createdDate; // 작성 일자
    private LocalDateTime editDate; // 수정 일자

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private QuestionState questionState; // 질문 해결 상태

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 질문 등록한 회원
}
