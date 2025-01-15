package com.authplayground.api.domain.auth.repository;

import java.time.Duration;

import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.response.LoginResponse;
import com.authplayground.api.dto.response.TokenResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

	private static final int EXPIRE_DAYS = 14;

	private final HashRedisRepository hashRedisRepository;

	public void saveToken(String email, TokenResponse tokenResponse) {
		hashRedisRepository.save(email, tokenResponse, Duration.ofDays(EXPIRE_DAYS));
	}

	public LoginResponse getTokenSaveValue(String email) {
		return (LoginResponse)hashRedisRepository.get(email);
	}

	public void deleteToken(String email) {
		hashRedisRepository.delete(email);
	}
}
