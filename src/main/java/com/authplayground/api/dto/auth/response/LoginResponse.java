package com.authplayground.api.dto.auth.response;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.token.response.TokenResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(description = "로그인 응답 DTO")
public record LoginResponse(
	@Schema(description = "JWT 액세스 토큰 및 리프레시 토큰")
	TokenResponse tokenResponse,

	@Schema(description = "인증된 사용자 정보")
	AuthMember authMember
) {

	public static LoginResponse of(Member member, String accessToken, String refreshToken) {
		return new LoginResponse(
			new TokenResponse(accessToken, refreshToken),
			new AuthMember(member.getEmail(), member.getNickname(), member.getRole())
		);
	}
}
