package com.authplayground.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstant {

	public static final String[] SECURITY_IGNORED_URLS = {
		"/h2-console/**",
		"/swagger-ui/**",
		"/swagger-resources/**",
		"/v3/api-docs/**"
	};

	public static final String[] PUBLIC_API_PATHS = {
		"/api/signup", "/api/login", "/api/reissue"
	};
}
