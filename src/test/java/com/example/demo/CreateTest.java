package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.ViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Test
    void test() {
//        Member member = Member.builder()
//                .loginId("test")
//                .password("test")
//                .email("test@naver.com")
//                .createdDate(LocalDateTime.now())
//                .phone("010-1234-5667")
//                .nickName("test")
//                .username("test")
//                .build();
//
//        View view = View.builder()
//                .member(member)
//                .build();
//
//        Board board = Board.builder()
//                .title("test")
//                .content("test")
//                .viewCnt(0)
//                .likeCnt(0)
//                .createdDate(LocalDateTime.now())
//                .editDate(LocalDateTime.now())
//                .member(member)
////                .view(view)
//                .build();
//
//        memberRepository.save(member);
//        boardRepository.save(board);
//        viewRepository.save(view);
//
//        Board board1 = boardRepository.findById(board.getId()).get();
//        System.out.println("board1 = " + board1.getView());

    }
}
