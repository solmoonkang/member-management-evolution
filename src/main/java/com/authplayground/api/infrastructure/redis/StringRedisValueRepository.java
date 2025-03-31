package com.authplayground.api.infrastructure.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StringRedisValueRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void save(String key, String value, Duration expired) {
		stringRedisTemplate.opsForValue().set(key, value, expired);
	}

	public String find(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}
}
