package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c join fetch c.board b" +
            " join fetch c.member m")
    List<Comment> findCommentWithMemberAndBoard();

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from Comment c where c.id = :commentId")
    void deleteComment(@Param(value = "commentId") Long id);
}
