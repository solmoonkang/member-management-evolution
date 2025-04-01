package com.authplayground.api.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
	@Schema(description = "사용자 이메일", example = "test@example.com")
	@NotBlank(message = "사용자 이메일을 입력해주세요.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	String email,

	@Schema(description = "사용자 비밀번호", example = "password1234!")
	@NotBlank(message = "사용자 비밀번호를 입력해주세요.")
	String password
) {
}
