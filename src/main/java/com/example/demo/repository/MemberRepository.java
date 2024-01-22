package com.example.demo.repository;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByLoginIdAndPassword(String loginId, String loginPassword);

    Optional<Member> findMemberByLoginId(String loginId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Member m set m.email = :email where m.loginId = :loginId")
    int updateEmail(@Param(value = "loginId") String loginId, @Param(value = "email") String email);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Member m set m.phone = :phone where m.loginId = :loginId")
    int updatePhone(@Param(value = "phone") String phone, @Param(value = "loginId") String loginId);

    @Query(value = "select m from Member m left outer join fetch m.boards b where m.loginId = :loginId")
    Optional<Member> findBoardsWrittenByMe(@Param(value = "loginId") String loginId);

    @Query(value = "select count(m) from Member m where m.loginId = :joinId")
    int joinIdAvailability(@Param(value = "joinId") String joinId);

}
