package com.authplayground.api.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.application.auth.JwtProviderService;
import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.LoginRequest;
import com.authplayground.api.dto.member.LoginResponse;
import com.authplayground.api.dto.member.SignUpRequest;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final JwtProviderService jwtProviderService;

	@Transactional
	public void signUpMember(SignUpRequest signUpRequest) {
		validateEmailDuplication(signUpRequest.email());
		validateNicknameDuplication(signUpRequest.nickname());
		validatePasswordAndCheckPasswordMatch(signUpRequest.password(), signUpRequest.checkPassword());

		final Member member = Member.createMember(signUpRequest, passwordEncoder.encode(signUpRequest.password()));

		memberRepository.save(member);
	}

	@Transactional
	public LoginResponse loginMember(LoginRequest loginRequest) {
		final Member member = findMemberByEmail(loginRequest.email());
		validateLoginPasswordMatch(loginRequest.password(), member.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(member.getEmail(), member.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(member.getEmail());
		member.updateMemberRefreshToken(refreshToken);

		return new LoginResponse(accessToken, refreshToken);
	}

	private Member findMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new NotFoundException("[❎ ERROR] 요청하신 사용자는 존재하지 않는 사용자입니다."));
	}

	private void validateEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException("[❎ ERROR] 입력하신 이메일은 이미 존재하는 이메일입니다.");
		}
	}

	private void validateNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new ConflictException("[❎ ERROR] 입력하신 닉네임은 이미 존재하는 닉네임입니다.");
		}
	}

	private void validatePasswordAndCheckPasswordMatch(String password, String checkPassword) {
		if (!password.equals(checkPassword)) {
			throw new ConflictException("[❎ ERROR] 입력하신 비밀번호와 일치하지 않습니다.");
		}
	}

	private void validateLoginPasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException("[❎ ERROR] 입력하신 비밀번호는 틀린 비밀번호입니다.");
		}
	}
}
