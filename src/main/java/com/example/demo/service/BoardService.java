package com.example.demo.service;

import com.example.demo.dto.RequestEnrolBoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
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
                    .member(loginMember)
                    .build();
            viewService.save(view);

            Board board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .createdDate(LocalDateTime.now())
                    .editDate(LocalDateTime.now())
                    .viewCnt(0)
                    .likeCnt(0)
                    .member(loginMember)
                    .build();
            boardRepository.save(board);

            return board.getId();
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    public Board findBoard(Long id) {
        return  boardRepository.findBoardByBoardId(id)
                .orElseThrow(() -> new NoSuchElementException("게시물을 읽어올 수 없습니다"));
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

    @Transactional
    public void increaseViewCnt(Board board) {
        boardRepository.increaseViewCnt(board.getId());
    }

    @Transactional
    public void increaseLikeCnt(Board board) {
        boardRepository.increaseLikeCnt(board.getId());
    }

    @Transactional
    public void decreaseLikeCnt(Board board) {
        boardRepository.decreaseLikeCnt(board.getId());
    }

}
