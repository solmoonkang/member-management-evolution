package com.authplayground.api.application.auth;

import static com.authplayground.global.common.util.JwtConstant.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.application.auth.validator.AuthenticationValidator;
import com.authplayground.api.application.member.MemberReadService;
import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.repository.BlacklistRepository;
import com.authplayground.api.domain.member.repository.TokenRepository;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.auth.response.LoginResponse;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.token.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtProvider jwtProvider;
	private final AuthenticationValidator authenticationValidator;
	private final MemberReadService memberReadService;
	private final TokenRepository tokenRepository;
	private final BlacklistRepository blacklistRepository;

	@Transactional
	public LoginResponse loginMember(LoginRequest loginRequest) {
		final Member member = memberReadService.getMemberByEmail(loginRequest.email());

		authenticationValidator.validatePasswordMatches(loginRequest.password(), member.getPassword());

		final String accessToken = generateAccessToken(member);
		final String refreshToken = generateRefreshToken(member);

		tokenRepository.saveToken(member.getEmail(), refreshToken);

		return LoginResponse.of(member, accessToken, refreshToken);
	}

	@Transactional
	public void logoutMember(AuthMember authMember, HttpServletRequest httpServletRequest) {
		final String accessToken = jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);
		final long remaining = jwtProvider.getTokenRemainingTime(accessToken);

		blacklistRepository.registerBlacklist(accessToken, remaining);
		tokenRepository.deleteTokenByEmail(authMember.email());
	}

	@Transactional
	public TokenResponse reissueToken(HttpServletRequest httpServletRequest) {
		final String refreshToken = httpServletRequest.getHeader(REFRESH_TOKEN_HEADER);

		authenticationValidator.validateRefreshTokenFormat(refreshToken);

		final AuthMember authMember = jwtProvider.extractAuthMemberFromToken(refreshToken);
		final Member member = memberReadService.getMemberByEmail(authMember.email());
		final String savedRefreshToken = tokenRepository.findTokenByEmail(member.getEmail());

		authenticationValidator.validateRefreshTokenMatches(refreshToken, savedRefreshToken);

		final String newAccessToken = generateAccessToken(member);
		final String newRefreshToken = generateRefreshToken(member);

		tokenRepository.deleteTokenByEmail(member.getEmail());
		tokenRepository.saveToken(member.getEmail(), newRefreshToken);

		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	private String generateAccessToken(Member member) {
		return jwtProvider.generateAccessToken(member.getEmail(), member.getNickname(), member.getRole());
	}

	private String generateRefreshToken(Member member) {
		return jwtProvider.generateRefreshToken(member.getEmail());
	}
}
