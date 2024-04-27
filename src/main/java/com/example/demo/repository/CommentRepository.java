package com.example.demo.repository;

import com.example.demo.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c left join fetch c.member m left join fetch c.board b"
        + " where b.id = :boardId")
    List<Comment> findComments(@Param("boardId") Long boardId);

    @Query("select c from Comment c join fetch c.member m join fetch c.board b"
        + " where m.loginId = :loginId and b.id = :boardId and c.content = :content")
    Optional<Comment> findCommentForDelete(@Param("loginId") String loginId,
        @Param("boardId") Long boardId,
        @Param("content") String content);

    Optional<Comment> findCommentByBoardIdAndMemberId(Long boardId, Long memberId);

    @Query("select c from Comment c join fetch c.board b join fetch c.member m"
        + " where b.id = :boardId and m.nickName = :nickName and c.content = :content")
    Optional<Comment> findCommentForUpdateLikeCnt(@Param("boardId") Long boardId,
        @Param("nickName") String nickName,
        @Param("content") String commentContent);
}
