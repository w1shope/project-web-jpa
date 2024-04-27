package com.example.demo.repository;

import com.example.demo.entity.ViewComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewCommentRepository extends JpaRepository<ViewComment, Long> {

    @Query(value = "select vc from ViewComment vc" +
        " join fetch vc.member m" +
        " join fetch vc.comment c" +
        " where m.id = :memberId and c.id = :commentId")
    Optional<ViewComment> findViewCommentWithMemberAndComment(
        @Param(value = "memberId") Long memberId,
        @Param(value = "commentId") Long commentId);
}
