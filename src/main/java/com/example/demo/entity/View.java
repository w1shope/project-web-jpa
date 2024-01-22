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

    private long likeCnt; // 좋아요 개수

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "view")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
