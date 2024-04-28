package com.api.almaeng2.domain.member.repository;

import com.api.almaeng2.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m.userId from MEMBER_TABLE as m where m.userId=:userId")
    String findByUserId(String userId);

    @Query("select m.userId from MEMBER_TABLE as m where m.userId=:userId")
    Optional<Member> findByDuplicateUserId(String userId);

}
