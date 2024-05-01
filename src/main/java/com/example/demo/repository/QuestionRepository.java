package com.example.demo.repository;

import com.example.demo.entity.Question;
import com.example.demo.entity.QuestionState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "select q from Question q join fetch q.member")
    List<Question> findQuestionAllWithMember();

    @Query(value = "select q from Question q join fetch q.member")
    List<Question> findQuestionAllWithMember(Pageable pageable);

    @Query(value = "select q from Question q join fetch  q.member where q.id = :questionId")
    Optional<Question> findQuestionById(@Param(value = "questionId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from Question q where q.id = :questionId")
    void delete(@Param(value = "questionId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Question q set q.title = :title, q.content = :content where q.id = :questionId")
    void update(@Param(value = "title") String title, @Param(value = "content") String content, @Param(value = "questionId") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Question q set q.questionState = :state where q.id = :id")
    void updateState(@Param(value = "state") QuestionState questionState, @Param("id") Long questionId);
}
