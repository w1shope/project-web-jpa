package com.example.demo.repository;

import com.example.demo.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.OptionalInt;

public interface ViewRepository extends JpaRepository<View, Long> {

    @Query(value = "select v from View v join fetch v.member m where v.id = :viewId")
    Optional<View> findViewWithMemberById(@Param(value = "viewId") Long id);
}
