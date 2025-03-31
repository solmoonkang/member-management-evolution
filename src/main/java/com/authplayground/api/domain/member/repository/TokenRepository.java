package com.authplayground.api.domain.member.repository;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.global.common.util.RedisConstant.*;

import org.springframework.stereotype.Repository;

import com.authplayground.api.infrastructure.redis.StringRedisValueRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

	private final StringRedisValueRepository stringRedisValueRepository;

	public void saveToken(String email, String refreshToken) {
		stringRedisValueRepository.save(generateRefreshTokenKey(email), refreshToken, REFRESH_TOKEN_EXPIRED);
	}

	public String findTokenByEmail(String email) {
		return stringRedisValueRepository.find(generateRefreshTokenKey(email));
	}

	public void deleteTokenByEmail(String email) {
		stringRedisValueRepository.delete(generateRefreshTokenKey(email));
	}

	private String generateRefreshTokenKey(String email) {
		return REFRESH_TOKEN_KEY_PREFIX + email;
	}
}
