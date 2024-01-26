package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.ViewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ViewCommentRepository extends JpaRepository<ViewComment, Long> {

    @Query(value = "select vc from ViewComment vc" +
            " join fetch vc.member m" +
            " join fetch vc.comment c" +
            " where m.loginId = :memberLoginId and c.id = :commentId")
    Optional<ViewComment> findViewCommentWithMemberAndComment(@Param(value = "memberLoginId") String loginId,
                                                @Param(value = "commentId") Long commentId);
}
