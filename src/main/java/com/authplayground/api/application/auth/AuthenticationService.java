package com.authplayground.api.application.auth;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.NotFoundException;
import com.authplayground.global.error.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;

	@Transactional
	public TokenResponse loginMember(LoginRequest loginRequest) {
		final Member member = getMemberByEmail(loginRequest.email());

		validatePasswordMatches(loginRequest.password(), member.getPassword());

		final String accessToken = generateAccessToken(member);
		final String refreshToken = generateRefreshToken(member);

		member.updateRefreshToken(refreshToken);

		return new TokenResponse(accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse reissueToken(HttpServletRequest httpServletRequest) {
		final String refreshToken = httpServletRequest.getHeader(REFRESH_TOKEN_HEADER);

		validateRefreshTokenFormat(refreshToken);

		final AuthMember authMember = jwtProvider.extractAuthMemberFromToken(refreshToken);
		final Member member = getMemberByEmail(authMember.email());

		validateRefreshTokenMatches(refreshToken, member.getRefreshToken());

		final String newAccessToken = generateAccessToken(member);
		final String newRefreshToken = generateRefreshToken(member);

		member.updateRefreshToken(newRefreshToken);

		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	private Member getMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));
	}

	private String generateAccessToken(Member member) {
		return jwtProvider.generateAccessToken(member.getEmail(), member.getNickname(), member.getRole());
	}

	private String generateRefreshToken(Member member) {
		return jwtProvider.generateRefreshToken(member.getEmail());
	}

	private void validatePasswordMatches(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}

	private void validateRefreshTokenFormat(String refreshToken) {
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new UnauthorizedException(UNAUTHORIZED_REFRESH_TOKEN);
		}
	}

	private void validateRefreshTokenMatches(String requestToken, String savedToken) {
		if (!requestToken.equals(savedToken)) {
			throw new UnauthorizedException(MISMATCH_REFRESH_TOKEN);
		}
	}
}
