package com.authplayground.api.domain.member.repository;

import org.springframework.stereotype.Repository;

import com.authplayground.api.infrastructure.redis.StringRedisTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

	private final StringRedisTokenRepository stringRedisTokenRepository;

	public void saveToken(String email, String refreshToken) {
		stringRedisTokenRepository.save(email, refreshToken);
	}

	public String findTokenByEmail(String email) {
		return stringRedisTokenRepository.find(email);
	}

	public void deleteTokenByEmail(String email) {
		stringRedisTokenRepository.delete(email);
	}
}
