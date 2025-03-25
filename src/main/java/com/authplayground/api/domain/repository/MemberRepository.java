package com.authplayground.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authplayground.api.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
