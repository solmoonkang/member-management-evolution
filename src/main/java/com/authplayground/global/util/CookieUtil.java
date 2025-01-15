package com.authplayground.global.util;

import static com.authplayground.global.util.GlobalConstant.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

	private static final int COOKIE_MAX_AGE = 24 * 60 * 60;

	public static Cookie generateRefreshTokenCookie(String refreshTokenName, String token) {
		Cookie refreshTokenCookie = new Cookie(refreshTokenName, token);
		refreshTokenCookie.setMaxAge(COOKIE_MAX_AGE);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");

		return refreshTokenCookie;
	}

	public static String extractRefreshTokenFromCookies(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getCookies() != null) {
			for (Cookie cookie : httpServletRequest.getCookies()) {
				if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) return cookie.getValue();
			}
		}

		return null;
	}

	public static Cookie expireRefreshTokenCookie(String refreshTokenName) {
		Cookie refreshTokenCookie = new Cookie(refreshTokenName, null);
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");

		return refreshTokenCookie;
	}
}
