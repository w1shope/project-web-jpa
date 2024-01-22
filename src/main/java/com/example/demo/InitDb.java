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
            View view = View.builder()
                    .likeCnt(0)
                    .viewCnt(0)
                    .build();
            viewRepository.save(view);

            Member member = Member.builder()
                    .loginId("test")
                    .password("test")
                    .email("test@gmail.com")
                    .phone("010-1234-5678")
                    .createdDate(LocalDateTime.now())
                    .username("nameA")
                    .nickName("nickNameA")
                    .build();
            view.relationshipToMember(member);
            memberRepository.save(member);

            Board board = Board.builder()
                    .title("제목 TEST")
                    .content("내용 TEST")
                    .editDate(LocalDateTime.now())
                    .createdDate(LocalDateTime.now())
                    .build();
            board.relationshipToView(view);
            board.relationshipToMember(member);
            boardRepository.save(board);
        }
    }
}
