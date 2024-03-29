package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "view_board")
public class View {

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

    public void relationToBoard(Board board) {
        this.board = board;
        board.getViews().add(this);
    }

    public void relationToMember(Member member) {
        this.member = member;
        member.getViews().add(this);
    }
}
