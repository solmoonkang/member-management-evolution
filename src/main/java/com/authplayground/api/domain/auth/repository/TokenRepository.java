package com.authplayground.api.domain.auth.repository;

import static com.authplayground.global.common.util.RedisConstant.*;

import java.time.Duration;

import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.response.TokenResponse;
import com.authplayground.api.infrastructure.redis.HashRedisRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

	private static final int EXPIRE_DAYS = 14;

	private final HashRedisRepository hashRedisRepository;

	public void saveToken(String email, TokenResponse tokenResponse) {
		hashRedisRepository.save(REDIS_REFRESH_TOKEN_PREFIX + email, tokenResponse, Duration.ofDays(EXPIRE_DAYS));
	}

	public TokenResponse getTokenSaveValue(String email) {
		return (TokenResponse)hashRedisRepository.get(REDIS_REFRESH_TOKEN_PREFIX + email);
	}

	public void deleteToken(String email) {
		hashRedisRepository.delete(REDIS_REFRESH_TOKEN_PREFIX + email);
	}
}
