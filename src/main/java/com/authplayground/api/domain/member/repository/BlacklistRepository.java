package com.authplayground.api.domain.member.repository;

import static com.authplayground.global.common.util.RedisConstant.*;

import org.springframework.stereotype.Repository;

import com.authplayground.api.infrastructure.redis.StringRedisValueRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BlacklistRepository {

	private static final String BLACKLISTED_VALUE = "logout";

	private final StringRedisValueRepository stringRedisValueRepository;

	public void registerBlacklist(String accessToken, long remainingTokenExpirationTime) {
		stringRedisValueRepository.save(
			generateBlacklistKey(accessToken), BLACKLISTED_VALUE, remainingTokenExpirationTime);
	}

	public boolean isBlacklisted(String accessToken) {
		return stringRedisValueRepository.find(generateBlacklistKey(accessToken)) != null;
	}

	private String generateBlacklistKey(String accessToken) {
		return BLACKLIST_KEY_PREFIX + accessToken;
	}
}
