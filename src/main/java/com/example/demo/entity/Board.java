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
    private long viewCnt; // 조회수
    private long likeCnt; // 좋아요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board",
    cascade = CascadeType.PERSIST)
    private List<View> views = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    File file;

    public void changeFile(File file) {
        this.file = file;
    }

    public void relationToMember(Member member){
        this.member = member;
        member.getBoards().add(this);
    }
}
