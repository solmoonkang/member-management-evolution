package com.authplayground.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConstant {

	// JWT 클레임 상수
	public static final String MEMBER_EMAIL = "email";
	public static final String MEMBER_NICKNAME = "nickname";
	public static final String MEMBER_ROLE = "role";

	// JWT 관련 상수
	public static final String BLANK = " ";
	public static final String BEARER = "Bearer";

	// 액세스 토큰 & 리프레시 토큰 헤더 및 쿠키 상수
	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_COOKIE = "Authorization_RefreshToken";
}
