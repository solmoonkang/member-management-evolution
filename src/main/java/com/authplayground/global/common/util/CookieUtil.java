package com.authplayground.global.common.util;

import static com.authplayground.global.common.util.JwtConstant.*;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return buildCookie(refreshToken, REFRESH_TOKEN_EXPIRED);
	}

	public ResponseCookie deleteRefreshTokenCookie() {
		return buildCookie("", 0);
	}

	private ResponseCookie buildCookie(String value, long maxAge) {
		return ResponseCookie.from(REFRESH_TOKEN_HEADER, value)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.sameSite("None")
			.maxAge(maxAge)
			.build();
	}
}
