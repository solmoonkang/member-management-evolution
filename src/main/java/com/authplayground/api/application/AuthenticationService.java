package com.authplayground.api.application;

import static com.authplayground.global.common.util.GlobalConstant.*;
import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.application.auth.JwtProviderService;
import com.authplayground.api.domain.auth.repository.TokenRepository;
import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.request.LoginRequest;
import com.authplayground.api.dto.request.SignUpRequest;
import com.authplayground.api.dto.response.LoginResponse;
import com.authplayground.api.dto.response.TokenResponse;
import com.authplayground.global.common.util.CookieUtil;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.global.error.exception.NotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

	private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;
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

		final String accessToken = jwtProviderService.generateAccessToken(
			member.getEmail(), member.getNickname(), member.getRole());
		final String refreshToken = jwtProviderService.generateRefreshToken(member.getEmail(), member.getRole());
		tokenRepository.saveToken(member.getEmail(), TokenResponse.builder().refreshToken(refreshToken).build());

		httpServletResponse.setHeader(ACCESS_TOKEN_HEADER, accessToken);
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
