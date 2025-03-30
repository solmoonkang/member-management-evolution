package com.authplayground.api.infrastructure.redis;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.global.common.util.RedisConstant.*;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StringRedisTokenRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void save(String email, String token) {
		executeWithRefreshTokenKey(email, key ->
			stringRedisTemplate.opsForValue().set(key, token, REFRESH_TOKEN_EXPIRED));
	}

	public String find(String email) {
		return getWithRefreshTokenKey(email, stringRedisTemplate.opsForValue()::get);
	}

	public void delete(String email) {
		executeWithRefreshTokenKey(email, stringRedisTemplate::delete);
	}

	private void executeWithRefreshTokenKey(String email, Consumer<String> action) {
		action.accept(generateRefreshTokenKey(email));
	}

	private <T> T getWithRefreshTokenKey(String email, Function<String, T> action) {
		return action.apply(generateRefreshTokenKey(email));
	}

	private String generateRefreshTokenKey(String email) {
		return REFRESH_TOKEN_KEY_PREFIX + email;
	}
}
