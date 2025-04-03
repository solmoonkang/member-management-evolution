package com.authplayground.global.auth.token.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import com.authplayground.global.auth.token.JwtProvider;

@TestConfiguration
public class JwtProviderTestConfig {

	@Bean
	public JwtProvider jwtProvider() {
		JwtProvider jwtProvider = new JwtProvider();

		ReflectionTestUtils.setField(jwtProvider, "secretKey", "test-secret-key-test-secret-key-test-secret-key");
		ReflectionTestUtils.setField(jwtProvider, "issuer", "test-issuer");
		ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", 1000 * 60 * 5);
		ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiration", 1000 * 60 * 60 * 24 * 14);

		jwtProvider.init();
		return jwtProvider;
	}
}
