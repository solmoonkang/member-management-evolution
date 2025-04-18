package com.authplayground.api.dto.auth.response;

import org.springframework.http.ResponseCookie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "응답 데이터와 함께 쿠키가 포함된 응답 DTO")
public record CookieAttachedResponse<T>(
	@Schema(description = "응답 데이터 (토큰 정보 또는 사용자 정보)")
	T response,

	@Schema(description = "HttpOnly 쿠키로 전달되는 리프레시 토큰 쿠키")
	ResponseCookie responseCookie
) {
}
