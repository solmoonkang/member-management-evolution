package com.authplayground.api.application.auth;

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
import com.authplayground.global.common.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtProvider jwtProvider;
	private final SessionManager sessionManager;
	private final AuthenticationValidator authenticationValidator;
	private final AuthenticationTokenService authenticationTokenService;
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
		final String accessToken = authenticationTokenService.extractAccessToken(httpServletRequest);
		final long remaining = authenticationTokenService.getRemainingAccessTokenTime(accessToken);

		blacklistRepository.registerBlacklist(accessToken, remaining);
		tokenRepository.deleteTokenByEmail(authMember.email());

		sessionManager.expiredSession(httpServletRequest);
	}

	@Transactional
	public TokenResponse reissueToken(HttpServletRequest httpServletRequest) {
		final String accessToken = authenticationTokenService.extractAccessToken(httpServletRequest);
		final String refreshToken = authenticationTokenService.extractRefreshToken(httpServletRequest);

		authenticationValidator.validateRefreshTokenFormat(refreshToken);

		final String email = authenticationTokenService.extractEmailFromRefreshToken(refreshToken);
		final Member member = memberReadService.getMemberByEmail(email);
		final String storedRefreshToken = tokenRepository.findTokenByEmail(member.getEmail());
		final long remainingTokenTime = authenticationTokenService.getRemainingAccessTokenTime(accessToken);

		authenticationValidator.validateRefreshTokenReused(storedRefreshToken, refreshToken);

		blacklistRepository.registerBlacklist(accessToken, remainingTokenTime);
		tokenRepository.deleteTokenByEmail(email);

		final String newAccessToken = generateAccessToken(member);
		final String newRefreshToken = generateRefreshToken(member);

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
