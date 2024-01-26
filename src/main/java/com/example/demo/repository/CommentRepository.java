package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c join fetch c.board b" +
            " join fetch c.member m")
    List<Comment> findCommentWithMemberAndBoard();

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from Comment c where c.id = :commentId")
    void deleteComment(@Param(value = "commentId") Long id);

    @Query(value = "select c from Comment c join fetch c.member m join fetch c.board b" +
            " where m.nickName = :memberNickName and c.content = :commentContent and c.createdDate = :commentCreatedDate")
    Optional<Comment> findCommentWithMemberAndBoardForLikeCnt(@Param(value = "memberNickName") String nickName,
                                                              @Param(value = "commentContent") String content,
                                                              @Param(value = "commentCreatedDate") LocalDateTime createdDate);

    @Query(value = "select c.likeCnt from Comment c where c.id = :commentId")
    long getLikeCnt(@Param(value = "commentId") Long id);

}
