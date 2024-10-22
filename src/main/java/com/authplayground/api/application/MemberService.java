package com.authplayground.api.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.SignUpRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional
	public void signUpMember(SignUpRequest signUpRequest) {
		validateEmailDuplication(signUpRequest.email());
		validateNicknameDuplication(signUpRequest.nickname());
		validatePasswordMatch(signUpRequest.password(), signUpRequest.checkPassword());

		final Member member = Member.createMember(signUpRequest, passwordEncoder.encode(signUpRequest.password()));

		memberRepository.save(member);
	}

	private void validateEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new IllegalStateException("[❎ ERROR] 입력하신 이메일은 이미 존재하는 이메일입니다.");
		}
	}

	private void validateNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new IllegalStateException("[❎ ERROR] 입력하신 닉네임은 이미 존재하는 닉네임입니다.");
		}
	}

	private void validatePasswordMatch(String password, String checkPassword) {
		if (!password.equals(checkPassword)) {
			throw new IllegalArgumentException("[❎ ERROR] 입력하신 비밀번호와 일치하지 않습니다.");
		}
	}
}
