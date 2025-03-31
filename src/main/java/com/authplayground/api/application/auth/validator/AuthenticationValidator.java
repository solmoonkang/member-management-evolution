package com.authplayground.api.application.auth.validator;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationValidator {

	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;

	public void validatePasswordMatches(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}

	public void validateRefreshTokenFormat(String refreshToken) {
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new UnauthorizedException(UNAUTHORIZED_REFRESH_TOKEN);
		}
	}

	public void validateRefreshTokenMatches(String requestToken, String savedToken) {
		if (!requestToken.equals(savedToken)) {
			throw new UnauthorizedException(MISMATCH_REFRESH_TOKEN);
		}
	}
}
