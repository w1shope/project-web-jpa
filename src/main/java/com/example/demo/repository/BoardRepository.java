package com.example.demo.repository;

import com.example.demo.entity.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(value = "select b from Board b join fetch b.member m")
    List<Board> findBoardAll();

    @Query(value = "select b from Board b join fetch  b.member m")
    List<Board> findBoardAll(Pageable pageable);

    @Query(value = "select b from Board b join fetch b.member m where lower(b.title) like lower(concat('%', :searchTitle, '%'))")
    List<Board> getPostsBySearch(@Param(value = "searchTitle") String searchTitle);

    @Query(value = "select b from Board b join fetch b.member m where m.loginId = :loginId")
    List<Board> getAllPosts(@Param(value = "loginId") String loginId);

    @Query(value = "select b from Board b left join fetch b.file f where b.id = :boardId")
    Optional<Board> findBoardWithFileByBoardId(@Param(value = "boardId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Board b set b.title = :title, b.content = :content" +
        " where b.id = :boardId")
    void update(@Param(value = "boardId") Long id, @Param(value = "title") String title,
        @Param(value = "content") String content);


    @Query(value = "select b from Board b left join fetch b.file")
    List<Board> findBoardWithFile();

    @Query(value = "select b from Board b left join fetch b.file")
    List<Board> findBoardWithFile(Pageable pageable);
}