package com.authplayground.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwaggerConstant {

	public static final String API_TITLE = "Auth Playground API Docs";
	public static final String API_DESCRIPTION = "JWT + 세션 기반 인증 시스템의 API 문서입니다.";
	public static final String API_VERSION = "1.0.0";

	public static final String SECURITY_SCHEME_NAME = "AccessToken";
	public static final String SECURITY_SCHEME_DESCRIPTION = "JWT 형식의 Bearer 토큰을 입력하세요";
	public static final String AUTH_HEADER_NAME = "Authorization";
	public static final String BEARER_TYPE = "bearer";
}
