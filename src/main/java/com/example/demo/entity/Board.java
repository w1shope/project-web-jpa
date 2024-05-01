package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_id")
    private Long id;

    private String title; // 제목
    private String content; // 내용
    private long viewCnt; // 조회수
    private long likeCnt; // 좋아요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board",
        cascade = CascadeType.PERSIST)
    private List<View> views = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "file_id")
    File file;

    public void changeFile(File file) {
        this.file = file;
    }

    // 조회수 증가
    public void increaseViewCnt() {
        this.viewCnt += 1;
    }

    // 좋아요 증가
    public void increaseLikeCnt() {
        this.likeCnt += 1;
    }

    // 좋아요 감소
    public void decreaseLikeCnt() {
        this.likeCnt -= 1;
    }
}
