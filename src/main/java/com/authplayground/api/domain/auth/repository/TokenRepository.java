package com.authplayground.api.domain.auth.repository;

import java.time.Duration;

import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.auth.TokenRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

	private static final int EXPIRE_DAYS = 14;

	private final HashRedisRepository hashRedisRepository;

	public void saveToken(String email, TokenRequest tokenRequest) {
		hashRedisRepository.save(email, tokenRequest, Duration.ofDays(EXPIRE_DAYS));
	}

	public TokenRequest getTokenSaveValue(String email) {
		return (TokenRequest)hashRedisRepository.get(email);
	}

	public void deleteToken(String email) {
		hashRedisRepository.delete(email);
	}
}
