package com.example.demo.repository;

import com.example.demo.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update View v set v.likeCnt = v.likeCnt + 1 where v.id = :viewId")
    void increaseLikeCnt(@Param(value = "viewId") Long id);
}
