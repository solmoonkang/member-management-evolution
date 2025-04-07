package com.authplayground.support.fixture;

import static com.authplayground.support.TestConstant.*;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.model.Role;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;

public class MemberFixture {

	public static Member createMember() {
		return Member.createMember(createSignUpRequest(), PASSWORD, REGISTRATION_NUMBER);
	}

	public static AuthMember createAuthMember() {
		return new AuthMember(EMAIL, NICKNAME, Role.MEMBER);
	}

	public static SignUpRequest createSignUpRequest(String email, String password, String passwordCheck,
		String nickname, String registrationNumber, String address) {

		return SignUpRequest.builder()
			.email(email)
			.password(password)
			.passwordCheck(passwordCheck)
			.nickname(nickname)
			.registrationNumber(registrationNumber)
			.address(address)
			.build();
	}

	public static SignUpRequest createSignUpRequest() {
		return createSignUpRequest(EMAIL, PASSWORD, PASSWORD, NICKNAME, REGISTRATION_NUMBER, ADDRESS);
	}

	public static SignUpRequest createSignUpRequestWithWrongEmail() {
		return createSignUpRequest("testEmail", PASSWORD, PASSWORD, NICKNAME, REGISTRATION_NUMBER, ADDRESS);
	}

	public static SignUpRequest createSignUpRequestWithDuplicatedNickname() {
		return createSignUpRequest("member2@test.com", PASSWORD, PASSWORD, "otherNickname", "980506-2345678", ADDRESS);
	}

	public static SignUpRequest createSignUpRequestWithWrongPasswordCheck() {
		return createSignUpRequest(EMAIL, PASSWORD, "differentPassword", NICKNAME, REGISTRATION_NUMBER, ADDRESS);
	}

	public static LoginRequest createLoginRequest(String email, String password) {
		return LoginRequest.builder()
			.email(email)
			.password(password)
			.build();
	}

	public static LoginRequest createLoginRequest() {
		return createLoginRequest(EMAIL, PASSWORD);
	}

	public static LoginRequest createLoginRequestWithWrongPassword() {
		return createLoginRequest(EMAIL, "wrong-password");
	}

	public static UpdateRequest createUpdateRequest(String nickname, String address) {
		return UpdateRequest.builder()
			.nickname(nickname)
			.address(address)
			.build();
	}

	public static UpdateRequest createUpdateRequest() {
		return createUpdateRequest("updatedNickname", "서울시 강남구");
	}

	public static UpdateRequest createUpdateRequestWithDuplicatedNickname() {
		return createUpdateRequest(NICKNAME, "서울시 강남구");
	}
}
