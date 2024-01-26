package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.entity.ViewComment;
import com.example.demo.repository.ViewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewCommentService {

    private final ViewCommentRepository viewCommentRepository;

    public Optional<ViewComment> findViewCommentWithMemberAndComment(Member member, Comment comment) {
        return viewCommentRepository.findViewCommentWithMemberAndComment(member.getLoginId(), comment.getId());
    }

    @Transactional
    public void changeLikeState(ViewComment viewComment) {
        viewComment.changeLikeStatus();
    }
}
