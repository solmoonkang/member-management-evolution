package com.authplayground.api.domain.auth.repository;

import static com.authplayground.global.common.util.GlobalConstant.*;
import static com.authplayground.global.common.util.RedisConstant.*;
import static com.authplayground.global.common.util.SessionConstant.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.authplayground.api.dto.response.SessionResponse;
import com.authplayground.api.infrastructure.redis.HashRedisRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

	private final HashRedisRepository hashRedisRepository;

	public void saveSession(String email) {
		Map<String, Object> sessionDataMap = new HashMap<>();
		sessionDataMap.put(MEMBER_EMAIL, email);
		hashRedisRepository.save(REDIS_SESSION_PREFIX + email, sessionDataMap, SESSION_TIMEOUT);
	}

	public SessionResponse getEmailFromSession(String email) {
		return hashRedisRepository.get(REDIS_SESSION_PREFIX + email, SessionResponse.class);
	}

	public void deleteSession(String email) {
		hashRedisRepository.delete(REDIS_SESSION_PREFIX + email);
	}
}
