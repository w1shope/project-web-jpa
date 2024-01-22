package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_id")
    private Long id;

    private String title; // 제목
    private String content; // 내용
    private LocalDateTime createdDate; // 작성 일자
    private LocalDateTime editDate; // 수정 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id")
    private View view;

    public void relationshipToView(View view) {
        this.view = view;
        view.getBoards().add(this);
    }

    public void relationshipToMember(Member member) {
        this.member = member;
        member.getBoards().add(this);
    }
}
