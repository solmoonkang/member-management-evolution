package com.authplayground.api.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UpdateRequest(
	@Schema(description = "변경할 닉네임", example = "newNickname")
	String nickname,

	@Schema(description = "변경할 주소", example = "서울특별시 종로구 새문안로 100")
	String address
) {
}
