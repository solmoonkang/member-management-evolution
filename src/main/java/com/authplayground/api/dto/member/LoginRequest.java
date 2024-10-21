package com.authplayground.api.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
	@NotBlank(message = "이메일을 입력해주세요.")
	String email,

	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}
