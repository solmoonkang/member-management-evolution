package com.authplayground.api.dto.auth.response;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.token.response.TokenResponse;

import lombok.Builder;

@Builder
public record LoginResponse(
	TokenResponse tokenResponse,

	AuthMember authMember
) {

	public static LoginResponse of(Member member, String accessToken, String refreshToken) {
		return new LoginResponse(
			new TokenResponse(accessToken, refreshToken),
			new AuthMember(member.getEmail(), member.getNickname(), member.getRole())
		);
	}
}
