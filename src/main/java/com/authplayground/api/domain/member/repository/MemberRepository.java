package com.authplayground.api.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authplayground.api.domain.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsMemberByEmail(String email);

	boolean existsMemberByNickname(String nickname);
}
