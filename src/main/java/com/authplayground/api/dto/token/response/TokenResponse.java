package com.authplayground.api.dto.token.response;

import lombok.Builder;

@Builder
public record TokenResponse(
	String accessToken,

	String refreshToken
) {
}
