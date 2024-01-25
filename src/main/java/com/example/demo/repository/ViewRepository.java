package com.example.demo.repository;

import com.example.demo.entity.Board;
import com.example.demo.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {

    @Query(value = "select v from View v join fetch v.member m where v.id = :viewId")
    Optional<View> findViewWithMemberById(@Param(value = "viewId") Long id);

    @Query(value = "select v from View v join fetch v.member m join fetch v.board b")
    Optional<List<View>> getAllViews();

    @Modifying(clearAutomatically = true)
    @Query(value = "update View v set v.likeStatus = 1 where v.id = :viewId")
    void changeStatusToPress(@Param(value = "viewId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update View v set v.likeStatus = 0 where v.id = :viewId")
    void changeStatusToCancel(@Param(value = "viewId") Long id);

    @Query(value = "select v from View v join fetch v.board b")
    List<View> findViewWithBoard();
}
