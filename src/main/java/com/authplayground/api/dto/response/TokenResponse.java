package com.authplayground.api.dto.response;

import java.util.Map;

import lombok.Builder;

@Builder
public record TokenResponse(
	String refreshToken
) {

	// TODO: Map에 refreshToken으로 할당하는 이유
	public static TokenResponse createTokenFromMap(Map<String, Object> memberTokenMap) {
		return TokenResponse.builder()
			.refreshToken((String)memberTokenMap.get("refreshToken"))
			.build();
	}
}
