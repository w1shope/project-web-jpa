package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "view_id")
    private Long id;

    @Column(columnDefinition = "TINYINT")
    private int clickLike; // 게시물 좋아요 클릭 여부

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "view")
    private List<Board> boards = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
