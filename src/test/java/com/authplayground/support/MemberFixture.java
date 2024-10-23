package com.authplayground.support;

import com.authplayground.api.domain.member.Member;
import com.authplayground.api.dto.member.LoginRequest;
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

	public static LoginRequest loginMemberRequest() {
		return LoginRequest.builder()
			.email("test@gmail.com")
			.password("12345")
			.build();
	}

	public static LoginRequest loginMemberRequestWithDifferentPassword() {
		return LoginRequest.builder()
			.email("test@gmail.com")
			.password("12345-wrong")
			.build();
	}

	public static Member createMember() {
		return Member.createMember(signUpMemberRequest(), "12345");
	}
}
