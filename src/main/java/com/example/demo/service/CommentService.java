package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.repository.CommentRepository;
import com.example.demo.vo.WriteCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Comment save(WriteCommentVO vo, Board board, Member member) {
        Comment comment = Comment.builder()
                .content(vo.getContent())
                .createdDate(LocalDateTime.now())
                .editDate(LocalDateTime.now())
                .board(board)
                .member(member)
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> findCommentWithMemberAndBoard() {
        return commentRepository.findCommentWithMemberAndBoard();
    }

    public void deleteComment(Comment comment) {
        commentRepository.deleteComment(comment.getId());
    }

    @Transactional
    public void updateComment(Comment comment, String updateContent) {
        comment.updateContent(updateContent);
    }

    public Optional<Comment> findCommentWithMemberAndBoardForLikeCnt(String nickName, String content, LocalDateTime createdDate) {
        return commentRepository.findCommentWithMemberAndBoardForLikeCnt(nickName, content, createdDate);
    }

    @Transactional
    public void increaseLikeCnt(Comment comment) {
        comment.increaseLikeCnt();
    }

    @Transactional
    public void decreaseLikeCnt(Comment comment) {
        comment.decreaseLikeCnt();
    }

    public long getCommentLikeCnt(Long commentId) {
        return commentRepository.getLikeCnt(commentId);
    }

}
