package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime editDate;
    private long likeCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<ViewComment> viewComments = new ArrayList<>();

    public void updateContent(String content) {
        this.content = content;
        this.editDate = LocalDateTime.now();
    }

    public void increaseLikeCnt() {
        this.likeCnt += 1;
    }

    public void decreaseLikeCnt() {
        this.likeCnt -= 1;
    }

}
