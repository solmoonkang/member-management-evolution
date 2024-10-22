package com.authplayground.support;

import com.authplayground.api.dto.member.SignUpRequest;

public class MemberFixture {

	public static SignUpRequest signUpMemberRequest() {
		return SignUpRequest.builder()
			.email("test@gmail.com")
			.nickname("test")
			.password("12345")
			.checkPassword("12345")
			.build();
	}

	public static SignUpRequest signUpMemberRequestWithDifferentPassword() {
		return SignUpRequest.builder()
			.email("test@gmail.com")
			.nickname("test")
			.password("12345")
			.checkPassword("12345-wrong")
			.build();
	}
}
