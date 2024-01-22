package com.example.demo.repository;

import com.example.demo.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(value = "select b from Board b join fetch b.view v join fetch b.member m")
    List<Board> findBoardAll();

    @Query(value = "select b from Board b join fetch b.member m where b.title like concat('%', :searchTitle, '%')")
    List<Board> getPostsBySearch(@Param(value = "searchTitle") String searchTitle);

    @Query(value = "select b from Board b join fetch b.member m" +
            " join fetch b.view v where m.loginId = :loginId")
    List<Board> getAllPosts(@Param(value = "loginId") String loginId);

    @Query(value = "select b from Board b join fetch b.view v where b.id = :boardId")
    Optional<Board> findBoardWithViewById(@Param(value = "boardId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Board b set b.viewCnt = b.viewCnt + 1 where b.id = :boardId")
    void increaseViewCnt(@Param(value = "boardId") Long id);
}
