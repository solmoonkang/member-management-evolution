package com.authplayground.support;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.model.Role;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;

public class MemberFixture {

	public static final String EMAIL = "member@test.com";
	public static final String PASSWORD = "test123!";
	public static final String NICKNAME = "memberTest";
	public static final String REGISTRATION_NUMBER = "980521-1234567";
	public static final String ADDRESS = "경기도 구리시 인창동 123";

	// ==============================================================
	// ✅ 도메인 객체 생성
	// ==============================================================

	public static Member createMember() {
		return Member.createMember(createSignUpRequest(), PASSWORD, REGISTRATION_NUMBER);
	}

	public static AuthMember createAuthMember() {
		return new AuthMember(EMAIL, NICKNAME, Role.MEMBER);
	}

	// ==============================================================
	// ✅ 요청 객체 생성 - 회원가입(SignUp)
	// ==============================================================

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

	// ==============================================================
	// ✅ 요청 객체 생성 - 로그인(Login)
	// ==============================================================

	public static LoginRequest createLoginRequest() {
		return LoginRequest.builder()
			.email(EMAIL)
			.password(PASSWORD)
			.build();
	}

	public static LoginRequest createWrongEmailLoginRequest() {
		return LoginRequest.builder()
			.email("notfound@test.com")
			.password(PASSWORD)
			.build();
	}

	public static LoginRequest createWrongPasswordLoginRequest() {
		return LoginRequest.builder()
			.email(EMAIL)
			.password("wrong-password")
			.build();
	}
}
