package com.authplayground.global.error.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "공통 에러 응답 포맷")
public record ErrorResponse(
	@Schema(description = "에러 메시지", example = "유효하지 않은 요청입니다.")
	String message,

	@Schema(description = "검증 실패 필드 및 메시지", example = "{\"email\": \"이메일 형식이 아닙니다.\"}")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	Map<String, String> validation
) {
}
