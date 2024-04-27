package com.example.demo.repository;

import com.example.demo.entity.View;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewRepository extends JpaRepository<View, Long> {

    @Query("select v from View v left join v.member m left join v.board b where m.id = :memberId and b.id = :boardId")
    Optional<View> findByBoardIdAndMemberId(@Param("boardId") Long boardId,
        @Param("memberId") Long memberId);
}
