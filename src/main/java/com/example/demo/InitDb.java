package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.ViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.initService();
    }


    @Data
    @Component
    static class InitService {

        private final MemberRepository memberRepository;
        private final ViewRepository viewRepository;
        private final BoardRepository boardRepository;

        public void initService() {
            Member member = Member.builder()
                    .loginId("test")
                    .password("test!")
                    .username("이희망")
                    .nickName("호프")
                    .email("test@gmail.com")
                    .phone("010-1234-5678")
                    .createdDate(LocalDateTime.now())
                    .build();
            memberRepository.save(member);

            Board board = Board.builder()
                    .title("titleA")
                    .content("contentA")
                    .createdDate(LocalDateTime.now())
                    .editDate(LocalDateTime.now())
                    .viewCnt(0)
                    .likeCnt(0)
                    .member(member)
                    .build();
            boardRepository.save(board);

        }
    }
}
