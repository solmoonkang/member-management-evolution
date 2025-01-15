package com.authplayground.api.dto.auth;

import java.util.Map;

import lombok.Builder;

@Builder
public record TokenRequest(String refreshToken) {

	public static TokenRequest createTokenFromMap(Map<String, Object> memberTokenMap) {
		return TokenRequest.builder()
			.refreshToken((String)memberTokenMap.get("refreshToken"))
			.build();
	}
}
