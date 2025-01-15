package com.authplayground.api.application;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static com.authplayground.global.util.GlobalConstant.*;

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
import com.authplayground.global.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
	public LoginResponse loginMember(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
		final Member member = findMemberByEmail(loginRequest.email());
		validateLoginPasswordMatch(loginRequest.password(), member.getPassword());

		final String accessToken = jwtProviderService.generateAccessToken(member.getEmail(), member.getNickname());
		final String refreshToken = jwtProviderService.generateRefreshToken(member.getEmail());
		member.updateMemberRefreshToken(refreshToken);

		addRefreshTokenCookie(refreshToken, httpServletResponse);

		return new LoginResponse(accessToken, refreshToken);
	}

	private Member findMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new NotFoundException(FAILED_MEMBER_NOT_FOUND));
	}

	private void validateEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException(FAILED_EMAIL_DUPLICATION);
		}
	}

	private void validateNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new ConflictException(FAILED_NICKNAME_DUPLICATION);
		}
	}

	private void validatePasswordAndCheckPasswordMatch(String password, String checkPassword) {
		if (!password.equals(checkPassword)) {
			throw new ConflictException(FAILED_PASSWORD_MISMATCH);
		}
	}

	private void validateLoginPasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(FAILED_INVALID_PASSWORD);
		}
	}

	private void addRefreshTokenCookie(String refreshToken, HttpServletResponse httpServletResponse) {
		final Cookie refreshTokenCookie = CookieUtil.generateRefreshTokenCookie(REFRESH_TOKEN_COOKIE, refreshToken);
		httpServletResponse.addCookie(refreshTokenCookie);
	}
}
