package com.example.demo.repository;

import com.example.demo.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query(value = "select a from Answer a join fetch a.question q where q.id = :id")
    Optional<List<Answer>> findAnswerWithQuestion(@Param(value = "id") Long questionId);

    @Query(value = "select a from Answer a join fetch a.member m join fetch a.question q" +
            " where q.id = :questionId and a.member.nickName = :nickName and a.createdDate = :createdDate")
    Optional<Answer> findAnswerWithMemberAndQuestion(@Param(value = "questionId") Long questionId,
                                                     @Param(value = "nickName") String nickName,
                                                     @Param(value = "createdDate") LocalDateTime answerCreatedDate);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Answer a set a.content = :content, a.editDate = now() where a.id = :id")
    void updateContent(@Param(value = "content") String updateContent,
                       @Param(value = "id") Long answerId);
}
