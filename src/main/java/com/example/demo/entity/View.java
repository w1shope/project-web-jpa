package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "view_board")
public class View extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "view_board_id")
    private Long id;

    @Column(columnDefinition = "TINYINT")
    private int likeStatus; // 게시물 좋아요 클릭 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public View(Board board, Member member) {
        this.board = board;
        this.member = member;
    }

    public void relationToBoard(Board board) {
        this.board = board;
        board.getViews().add(this);
    }

    public void relationToMember(Member member) {
        this.member = member;
        member.getViews().add(this);
    }
}
