package com.authplayground.api.application.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	public void createMember(SignUpRequest signUpRequest, String encodedRegNum) {
		String encodedPassword = passwordEncoder.encode(signUpRequest.password());
		Member member = Member.createMember(signUpRequest, encodedPassword, encodedRegNum);
		memberRepository.save(member);
	}

	public void updateMember(Member member, UpdateRequest updateRequest) {
		member.updateMember(updateRequest);
	}

	public void deleteMember(Member member) {
		memberRepository.delete(member);
	}
}
