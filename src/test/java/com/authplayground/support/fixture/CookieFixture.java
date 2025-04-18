package com.authplayground.support.fixture;

import org.springframework.http.ResponseCookie;

import com.authplayground.support.TestConstant;

public class CookieFixture {

	public static ResponseCookie createRefreshTokenCookie(String value, long refreshTokenMaxAge) {
		return ResponseCookie.from(TestConstant.REFRESH_TOKEN_HEADER, value)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.sameSite("None")
			.maxAge(refreshTokenMaxAge)
			.build();
	}
}
