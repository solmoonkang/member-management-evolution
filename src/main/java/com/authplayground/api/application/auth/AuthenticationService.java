package com.authplayground.api.application.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.application.auth.validator.AuthenticationValidator;
import com.authplayground.api.application.member.MemberReadService;
import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.repository.BlacklistRepository;
import com.authplayground.api.domain.member.repository.TokenRepository;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.auth.response.CookieAttachedResponse;
import com.authplayground.api.dto.auth.response.LoginResponse;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.common.util.CookieUtil;
import com.authplayground.global.common.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtProvider jwtProvider;
	private final SessionManager sessionManager;
	private final CookieUtil cookieUtil;
	private final AuthenticationValidator authenticationValidator;
	private final AuthenticationTokenService authenticationTokenService;
	private final MemberReadService memberReadService;
	private final TokenRepository tokenRepository;
	private final BlacklistRepository blacklistRepository;

	@Transactional
	public CookieAttachedResponse<LoginResponse> loginMember(LoginRequest loginRequest) {
		final Member member = memberReadService.getMemberByEmail(loginRequest.email());

		authenticationValidator.validatePasswordMatches(loginRequest.password(), member.getPassword());

		final String accessToken = generateAccessToken(member);
		final String refreshToken = generateRefreshToken(member);

		tokenRepository.saveToken(member.getEmail(), refreshToken);

		final LoginResponse loginResponse = LoginResponse.of(member, accessToken, refreshToken);
		final ResponseCookie responseCookie = cookieUtil.createRefreshTokenCookie(refreshToken);

		return new CookieAttachedResponse<>(loginResponse, responseCookie);
	}

	@Transactional
	public ResponseCookie logoutMember(AuthMember authMember, HttpServletRequest httpServletRequest) {
		final String accessToken = authenticationTokenService.extractAccessToken(httpServletRequest);
		final long remaining = authenticationTokenService.getRemainingAccessTokenTime(accessToken);

		blacklistRepository.registerBlacklist(accessToken, remaining);
		tokenRepository.deleteTokenByEmail(authMember.email());

		sessionManager.expiredSession(httpServletRequest);

		return cookieUtil.deleteRefreshTokenCookie();
	}

	@Transactional
	public CookieAttachedResponse<TokenResponse> reissueToken(HttpServletRequest httpServletRequest) {
		final String accessToken = authenticationTokenService.extractAccessToken(httpServletRequest);
		final String refreshToken = authenticationTokenService.extractRefreshToken(httpServletRequest);

		authenticationValidator.validateRefreshTokenFormat(refreshToken);

		final String email = authenticationTokenService.extractEmailFromRefreshToken(refreshToken);
		final Member member = memberReadService.getMemberByEmail(email);
		final String storedRefreshToken = tokenRepository.findTokenByEmail(member.getEmail());

		authenticationValidator.validateRefreshTokenReused(storedRefreshToken, refreshToken);

		final long remainingTokenTime = authenticationTokenService.getRemainingAccessTokenTime(accessToken);
		blacklistRepository.registerBlacklist(accessToken, remainingTokenTime);

		tokenRepository.deleteTokenByEmail(email);

		final String newAccessToken = generateAccessToken(member);
		final String newRefreshToken = generateRefreshToken(member);
		tokenRepository.saveToken(member.getEmail(), newRefreshToken);

		final TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
		final ResponseCookie responseCookie = cookieUtil.createRefreshTokenCookie(newRefreshToken);

		return new CookieAttachedResponse<>(tokenResponse, responseCookie);
	}

	private String generateAccessToken(Member member) {
		return jwtProvider.generateAccessToken(member.getEmail(), member.getNickname(), member.getRole());
	}

	private String generateRefreshToken(Member member) {
		return jwtProvider.generateRefreshToken(member.getEmail());
	}
}
