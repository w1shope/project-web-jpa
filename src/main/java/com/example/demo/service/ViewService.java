package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.ViewRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewService {

    private final ViewRepository viewRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(View view) {
        return viewRepository.save(view).getId();
    }

    @Transactional(readOnly = true)
    public View getView(Long boardId, Long memberId) {
        // null -> 사용자가 게시물에 좋아요를 누르지 않았다.
        return viewRepository.findByBoardIdAndMemberId(boardId, memberId)
            .orElse(null);
    }

    /**
     * 좋아요를 취소한 경우 DB 데이터를 지운다.
     */
    @Transactional
    public void deleteView(Long viewId) {
        viewRepository.deleteById(viewId);
    }

    @Transactional
    public void saveView(Long boardId, Long memberId) {
        Board board = getBoard(boardId);
        Member member = findMember(memberId);

        View view = View.builder()
            .board(board)
            .member(member)
            .build();
        viewRepository.save(view);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("Not Found Member Id : " + memberId));
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new NoSuchElementException("Not Found Board Id : " + boardId));
    }
}
