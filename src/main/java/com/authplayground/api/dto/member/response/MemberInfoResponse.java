package com.authplayground.api.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
	@Schema(description = "사용자 이메일", example = "user@example.com")
	String email,

	@Schema(description = "사용자 닉네임", example = "nickname123")
	String nickname,

	@Schema(description = "사용자 주소", example = "부산광역시 해운대구 APT 101동")
	String address
) {
}
