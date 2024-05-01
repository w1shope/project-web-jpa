package com.example.demo.service;

import com.example.demo.dto.GetCommentResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.entity.ViewComment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ViewCommentRepository;
import com.example.demo.vo.DeleteCommentRequestDto;
import com.example.demo.vo.EditCommentRequestDto;
import com.example.demo.vo.UpdateCommentLikeCntRequestDto;
import com.example.demo.vo.WriteCommentRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final BoardService boardService;
    private final ViewCommentRepository viewCommentRepository;

    @Transactional
    public Comment save(WriteCommentRequestDto vo, Board board, Member member) {
        Comment comment = Comment.builder()
            .content(vo.getContent())
            .board(board)
            .member(member)
            .build();
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<GetCommentResponseDto> findComments(Long boardId) {
        return commentRepository.findComments(boardId).stream()
            .map(c -> new GetCommentResponseDto(c.getMember().getNickName(), c.getContent(),
                c.getLikeCnt()))
            .toList();
    }

    @Transactional
    public Comment writeComment(WriteCommentRequestDto request, String loginId) {

        // 댓글 작성자 조회
        Member member = memberService.getLoginMember(loginId);

        // 게시물 조회
        Board board = boardService.findBoardById(request.getBoardId());

        Comment comment = Comment.builder()
            .content(request.getContent())
            .board(board)
            .member(member)
            .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(EditCommentRequestDto request, String loginId) {

        // 댓글 수정자 조회
        Member member = memberService.getLoginMember(loginId);

        // 댓글 조회
        Comment comment = commentRepository.findCommentByBoardIdAndMemberId(request.getBoardId(),
                member.getId())
            .orElseThrow(() -> new NoSuchElementException("Not Found Comment"));

        comment.updateContent(request.getNewContent());
    }

    @Transactional
    public void deleteComment(DeleteCommentRequestDto request, String loginId) {

        // 댓글 조회
        Comment comment = commentRepository.findCommentForDelete(loginId,
                request.getBoardId(),
                request.getCommentContent())
            .orElseThrow(() -> new NoSuchElementException("Not Found Comment"));

        // 댓글 삭제
        commentRepository.delete(comment);
    }

    @Transactional
    public long updateLikeCnt(UpdateCommentLikeCntRequestDto request, String loginId) {

        // 사용자 조회
        Member member = memberService.getLoginMember(loginId);

        // 댓글 조회
        Comment comment = commentRepository.findCommentForUpdateLikeCnt(request.getBoardId(),
                request.getCommentWriter(), request.getCommentContent())
            .orElseThrow(() -> new NoSuchElementException("Not Found Comment"));

        // 댓글 작성자만 수정이 가능하다.
        if (comment.getMember().getId() != member.getId()) {
            throw new IllegalStateException("댓글 작성자만 수정이 가능합니다.");
        }

        // 좋아요 기록 조회
        ViewComment findViewComment = viewCommentRepository.findViewCommentWithMemberAndComment(
                member.getId(), comment.getId())
            .orElse(null);

        // 좋아요 누른 적이 없는 경우
        if (findViewComment == null) {
            // 좋아요 1개 증가
            comment.increaseLikeCnt();
            ViewComment viewComment = ViewComment.builder()
                .comment(comment)
                .member(member)
                .build();
            viewCommentRepository.save(viewComment);
        } else {
            // 좋아요 취소
            comment.decreaseLikeCnt();
            // DB 삭제
            viewCommentRepository.delete(findViewComment);
        }

        return comment.getLikeCnt();
    }
}
