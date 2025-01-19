package com.authplayground.api.application.member;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.response.MemberInfoResponse;
import com.authplayground.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberInfoResponse searchMemberInfo(AuthMember authMember) {
		final Member member = memberRepository.findMemberByEmail(authMember.email())
			.orElseThrow(() -> new NotFoundException(FAILED_MEMBER_NOT_FOUND));

		return new MemberInfoResponse(member.getNickname());
	}
}
