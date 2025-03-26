package com.authplayground.api.dto.member.response;

import lombok.Builder;

@Builder
public record MemberInfoResponse(
	String email,

	String nickname,

	String address
) {
}
