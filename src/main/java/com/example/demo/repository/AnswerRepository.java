package com.example.demo.repository;

import com.example.demo.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query(value = "select a from Answer a join fetch a.question q where q.id = :id")
    Optional<List<Answer>> findAnswerWithQuestion(@Param(value = "id") Long questionId);
}
