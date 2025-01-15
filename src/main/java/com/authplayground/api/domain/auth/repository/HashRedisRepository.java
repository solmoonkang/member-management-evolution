package com.authplayground.api.domain.auth.repository;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.auth.TokenRequest;
import com.authplayground.global.error.exception.ConflictException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HashRedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private final HashOperations<String, String, Object> hashOperations;

	public void save(String key, Object value, Duration timeout) {
		hashOperations.putAll(key, new Jackson2HashMapper(false).toHash(value));
		redisTemplate.expire(key, timeout);
	}

	public Object get(String key) {
		final Map<String, Object> memberTokenMap = hashOperations.entries(key);
		validUnauthorizedMember(memberTokenMap);

		return TokenRequest.createTokenFromMap(memberTokenMap);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	// TODO: SRP(단일 책임 원칙) 위배 가능성이 있으므로, 서비스에서 비즈니스 로직을 처리할 때 검증을 할 수 있도록 하자.
	private void validUnauthorizedMember(Map<String, Object> memberTokenMap) {
		if (memberTokenMap.isEmpty()) {
			throw new ConflictException("[❎ ERROR] 해당 사용자는 인증되지 않은 사용자입니다.");
		}
	}
}
