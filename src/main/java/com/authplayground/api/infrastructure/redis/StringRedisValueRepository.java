package com.authplayground.api.infrastructure.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StringRedisValueRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void save(String key, String value, long expiration) {
		stringRedisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
	}

	public String find(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}
}
