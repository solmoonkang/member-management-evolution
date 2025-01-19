package com.authplayground.api.dto.response;

import lombok.Builder;

@Builder
public record MemberInfoResponse(
	String nickname
) {
}
