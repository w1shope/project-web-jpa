package com.example.demo.service;

import com.example.demo.dto.RequestEnrolBoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.vo.SearchBoardConditionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final ViewService viewService;

    @Transactional
    public Long save(RequestEnrolBoardDto request) {
        try {
            Member loginMember = memberService.getLoginMember(request.getLoginId());

            View view = View.builder()
                    .viewCnt(0)
                    .likeCnt(0)
                    .build();
            viewService.save(view);

            Board board = Board.builder()
                    .member(loginMember)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .createdDate(LocalDateTime.now())
                    .editDate(LocalDateTime.now())
                    .build();
            boardRepository.save(board);

            // 연관관계 편의 메서드
            view.relationshipToMember(loginMember);
            board.relationshipToView(view);
            board.relationshipToMember(loginMember);

            return board.getId();
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    public Board findBoard(Long id) {
        Board board = boardRepository.getBoardById(id);
        return board;
    }

    public List<Board> findAll() {
        return boardRepository.findBoardAll();
    }

    public List<Board> getPostsBySearch(SearchBoardConditionVO vo) {
        return boardRepository.getPostsBySearch(vo.getCondition());
    }

    public List<Board> getAllPosts(String loginId) {
        return boardRepository.getAllPosts(loginId);
    }

}
