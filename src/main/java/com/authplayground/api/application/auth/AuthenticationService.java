package com.authplayground.api.application.auth;

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

		final String accessToken = jwtProvider.generateAccessToken(
			member.getEmail(), member.getNickname(), member.getRole());
		final String refreshToken = jwtProvider.generateRefreshToken(
			member.getEmail());

		member.updateRefreshToken(refreshToken);

		return new TokenResponse(accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse reissueToken(String refreshToken) {
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new UnauthorizedException(UNAUTHORIZED_REFRESH_TOKEN);
		}

		final AuthMember authMember = jwtProvider.extractAuthMemberFromToken(refreshToken);
		final Member member = getMemberByEmail(authMember.email());

		if (!refreshToken.equals(member.getRefreshToken())) {
			throw new UnauthorizedException(MISMATCH_REFRESH_TOKEN);
		}

		final String newAccessToken = jwtProvider.generateAccessToken(
			member.getEmail(), member.getNickname(), member.getRole());
		final String newRefreshToken = jwtProvider.generateRefreshToken(
			member.getEmail());

		member.updateRefreshToken(newRefreshToken);

		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	private Member getMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));
	}

	private void validatePasswordMatches(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}
}
