package com.authplayground.api.dto.member.request;

import lombok.Builder;

@Builder
public record UpdateRequest(
	String nickname,

	String address
) {
}
