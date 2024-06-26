package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import com.example.demo.entity.Member;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuestionState;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.ViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        private final FileRepository fileRepository;
        private final QuestionRepository questionRepository;

        public void initService() {
            Member member = Member.builder()
                .loginId("test")
                .password("test!")
                .username("테스트")
                .nickName("운영자")
                .email("test@gmail.com")
                .phone("010-1234-5678")
                .build();
            memberRepository.save(member);

            File file = File.builder()
                .uploadFilename("capture.png")
                .storedFileName("8fbeff27-c719-403e-a485-cfa6bff1cf8f.png")
                .storedFilePath(
                    "/Users/hope/Desktop/Desktop/upload-file/8fbeff27-c719-403e-a485-cfa6bff1cf8f.png")
                .build();
            fileRepository.save(file);

            Board board = Board.builder()
                .title("titleA")
                .content("contentA")
                .member(member)
                .file(file)
                .build();
            boardRepository.save(board);

            Member member2 = Member.builder()
                .loginId("dlgmlakd6503")
                .password("Dlgmlakd01!@")
                .username("이희망")
                .nickName("호프")
                .email("a94496503@gmail.com")
                .phone("010-9449-6503")
                .build();
            memberRepository.save(member2);

            Question question = Question.builder()
                .title("질문 제목A")
                .content("질문 내용A")
                .member(member2)
                .questionState(QuestionState.UNRESOLVED)
                .build();
            questionRepository.save(question);

        }
    }
}
