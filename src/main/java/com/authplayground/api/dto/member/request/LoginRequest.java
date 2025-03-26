package com.authplayground.api.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
	@NotBlank(message = "사용자 이메일을 입력해주세요.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	String email,

	@NotBlank(message = "사용자 비밀번호를 입력해주세요.")
	String password
) {
}
