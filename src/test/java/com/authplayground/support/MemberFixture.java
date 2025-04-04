package com.authplayground.support;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.model.Role;
import com.authplayground.api.dto.member.request.SignUpRequest;

public class MemberFixture {

	public static final String EMAIL = "member@test.com";
	public static final String PASSWORD = "test123!";
	public static final String NICKNAME = "memberTest";
	public static final String REGISTRATION_NUMBER = "980521-1234567";
	public static final String ADDRESS = "경기도 구리시 인창동 123";

	public static Member createMember() {
		return Member.createMember(createSignUpRequest(), PASSWORD, REGISTRATION_NUMBER);
	}

	public static SignUpRequest createSignUpRequest() {
		return SignUpRequest.builder()
			.email(EMAIL)
			.password(PASSWORD)
			.passwordCheck(PASSWORD)
			.nickname(NICKNAME)
			.registrationNumber(REGISTRATION_NUMBER)
			.address(ADDRESS)
			.build();
	}

	public static AuthMember createAuthMember() {
		return new AuthMember(EMAIL, NICKNAME, Role.MEMBER);
	}
}
