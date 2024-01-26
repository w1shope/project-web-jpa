package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.ViewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
@Transactional
@Commit
public class CreateTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    ViewRepository viewRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void test() {

        View view = View.builder()
                .likeStatus(0)
                .build();

        Board board = Board.builder()
                .title("titleA")
                .content("contentA")
                .createdDate(LocalDateTime.now())
                .editDate(LocalDateTime.now())
                .viewCnt(0)
                .likeCnt(0)
                .views(new ArrayList<>())
                .build();

        Member member = Member.builder()
                .loginId("test")
                .password("test")
                .username("test")
                .nickName("test")
                .email("test")
                .phone("test")
                .createdDate(LocalDateTime.now())
                .build();

        view.relationToMember(member);
        view.relationToBoard(board);
        board.relationToMember(member);
        memberRepository.save(member);

        em.remove(member); // em = EntityManager
    }
}
