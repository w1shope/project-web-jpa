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

    private long viewCnt; // 조회수
    private long likeCnt; // 좋아요 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Builder.Default
    @OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    public void relationshipToMember(Member member) {
        this.member = member;
        member.getViews().add(this);
    }

    public void increaseViewCnt() {
        this.viewCnt += 1;
    }
}
