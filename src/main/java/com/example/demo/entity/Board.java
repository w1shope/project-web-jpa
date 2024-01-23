package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
//@Builder
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
//    private List<View> views = new ArrayList<>();
    private List<View> views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    File file;

    @Builder
    public Board(String title, String content, LocalDateTime createdDate, LocalDateTime editDate, long viewCnt, long likeCnt, Member member, File file) {
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.editDate = editDate;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.member = member;
        this.views = new ArrayList<>();
        this.file = file;
    }

    @Builder
    public Board(String title, String content, LocalDateTime createdDate, LocalDateTime editDate, long viewCnt, long likeCnt, Member member, List<View> views, File file) {
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.editDate = editDate;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.member = member;
        this.views = views;
        this.file = file;
    }
}
