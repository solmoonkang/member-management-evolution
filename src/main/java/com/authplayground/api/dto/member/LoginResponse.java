package com.authplayground.api.dto.member;

public record LoginResponse(
	String accessToken,

	String refreshToken
) {
}
