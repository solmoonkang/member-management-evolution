package com.authplayground.api.infrastructure.redis;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	public <T> T get(String key, Class<T> clazz) {
		final Map<String, Object> memberDataMap = hashOperations.entries(key);
		return createObjectFromMap(memberDataMap, clazz);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	private <T> T createObjectFromMap(Map<String, Object> dataMap, Class<T> clazz) {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.convertValue(dataMap, clazz);
	}
}
