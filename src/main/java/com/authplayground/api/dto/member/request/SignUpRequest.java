package com.authplayground.api.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SignUpRequest(
	@Schema(description = "사용자 이메일", example = "newuser@example.com")
	@NotBlank(message = "사용자 이메일을 입력해주세요.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	String email,

	@Schema(description = "사용자 비밀번호", example = "securePassword1!")
	@NotBlank(message = "사용자 비밀번호를 입력해주세요.")
	String password,

	@Schema(description = "확인용 비밀번호", example = "securePassword1!")
	@NotBlank(message = "확인 비밀번호를 입력해주세요.")
	String passwordCheck,

	@Schema(description = "사용자 닉네임", example = "nickname")
	@NotBlank(message = "사용자 닉네임을 입력해주세요.")
	String nickname,

	@Schema(description = "주민등록번호", example = "980521-1234567")
	@NotBlank(message = "사용자 주민등록번호를 입력해주세요.")
	@Pattern(
		regexp = "^\\d{6}-[1-4]\\d{6}$",
		message = "주민등록번호 형식에 맞게 입력해주세요. (예: 980521-1234567)"
	)
	String registrationNumber,

	@Schema(description = "주소", example = "경기도 구리시 인창동 123")
	String address
) {
}
