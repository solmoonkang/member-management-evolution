package com.authplayground.global.error.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {

	FAILED_INVALID_PASSWORD("[❎ ERROR] 입력하신 비밀번호는 틀린 비밀번호입니다."),
	FAILED_INVALID_TOKEN("[❎ ERROR] 유효하지 않은 토큰입니다."),
	FAILED_UNAUTHORIZED_MEMBER("[❎ ERROR] 해당 사용자는 인증되지 않은 사용자입니다."),
	FAILED_EMAIL_DUPLICATION("[❎ ERROR] 입력하신 이메일은 이미 존재하는 이메일입니다."),
	FAILED_NICKNAME_DUPLICATION("[❎ ERROR] 입력하신 닉네임은 이미 존재하는 닉네임입니다."),
	FAILED_PASSWORD_MISMATCH("[❎ ERROR] 입력하신 비밀번호와 일치하지 않습니다."),
	FAILED_MEMBER_NOT_FOUND("[❎ ERROR] 요청하신 사용자는 존재하지 않는 사용자입니다."),
	FAILED_TOKEN_NOT_FOUND("[❎ ERROR] JWT 토큰이 존재하지 않습니다."),

	FAILED_UNKNOWN_ERROR("[❎ ERROR] 서버에서 알 수 없는 에러가 발생했습니다.");

	private final String message;
}
