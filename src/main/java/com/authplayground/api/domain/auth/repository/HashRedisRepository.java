package com.authplayground.api.domain.auth.repository;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.response.TokenResponse;

@Repository
public class HashRedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private final HashOperations<String, String, Object> hashOperations;

	public HashRedisRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.hashOperations = redisTemplate.opsForHash();
	}

	public void save(String key, Object value, Duration timeout) {
		hashOperations.putAll(key, new Jackson2HashMapper(false).toHash(value));
		redisTemplate.expire(key, timeout);
	}

	public Object get(String key) {
		final Map<String, Object> memberTokenMap = hashOperations.entries(key);
		return TokenResponse.createTokenFromMap(memberTokenMap);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
