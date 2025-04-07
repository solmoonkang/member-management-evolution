package com.authplayground.api.application.auth;

import static com.authplayground.global.common.util.JwtConstant.*;

import org.springframework.stereotype.Service;

import com.authplayground.global.auth.token.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationTokenService {

	private final JwtProvider jwtProvider;

	public String extractAccessToken(HttpServletRequest httpServletRequest) {
		return jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);
	}

	public String extractRefreshToken(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getHeader(REFRESH_TOKEN_HEADER);
	}

	public long getRemainingAccessTokenTime(String accessToken) {
		return jwtProvider.getTokenRemainingTime(accessToken);
	}

	public String extractEmailFromRefreshToken(String refreshToken) {
		return jwtProvider.extractEmailFromToken(refreshToken);
	}
}
