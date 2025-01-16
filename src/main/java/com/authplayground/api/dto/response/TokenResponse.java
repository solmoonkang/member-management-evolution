package com.authplayground.api.dto.response;

import static com.authplayground.global.common.util.RedisConstant.*;

import java.util.Map;

import lombok.Builder;

@Builder
public record TokenResponse(
	String refreshToken
) {

	public static TokenResponse createTokenFromMap(Map<String, Object> memberTokenMap) {
		return TokenResponse.builder()
			.refreshToken((String)memberTokenMap.get(REDIS_REFRESH_TOKEN_PREFIX))
			.build();
	}
}
