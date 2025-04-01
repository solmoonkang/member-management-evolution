package com.authplayground.global.error.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {

	// 400 Bad Request
	PASSWORD_MISMATCH_FAILURE("[❎ ERROR] 입력하신 비밀번호가 일치하지 않습니다."),

	// 401 Unauthorized
	UNAUTHORIZED_REQUEST("[❎ ERROR] 인증되지 않은 사용자 요청입니다."),
	INVALID_AUTHORIZATION_HEADER("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다."),
	BLACKLISTED_ACCESS_TOKEN("[❎ ERROR] 블랙리스트에 등록된 액세스 토큰입니다."),
	UNAUTHORIZED_REFRESH_TOKEN("[❎ ERROR] 유효하지 않은 리프레시 토큰입니다."),
	MISMATCH_REFRESH_TOKEN("[❎ ERROR] 리프레시 토큰이 일치하지 않습니다."),
	REUSED_REFRESH_TOKEN("[❎ ERROR] 재사용된 리프레시 토큰 사용을 시도했습니다."),

	// 403 Forbidden
	NO_PERMISSION_FAILURE("[❎ ERROR] 권한이 없는 사용자가 접근했습니다."),

	// 404 Not Found
	MEMBER_NOT_FOUND_FAILURE("[❎ ERROR] 요청하신 회원을 찾을 수 없습니다."),

	// 409 Conflict
	DUPLICATED_EMAIL_FAILURE("[❎ ERROR] 이미 존재하는 사용자 이메일입니다."),
	DUPLICATED_NICKNAME_FAILURE("[❎ ERROR] 이미 존재하는 사용자 닉네임입니다."),
	DUPLICATED_REGISTRATION_NUMBER_FAILURE("[❎ ERROR] 이미 존재하는 사용자 주민번호입니다."),

	// 500 Internal Server Error
	AES_INIT_FAILURE("[❎ ERROR] AES 초기화 중 예외가 발생했습니다."),
	AES_ENCRYPTION_FAILURE("[❎ ERROR] AES 암호화 중 예외가 발생했습니다."),
	AES_DECRYPTION_FAILURE("[❎ ERROR] AES 복호화 중 예외가 발생했습니다."),
	UNKNOWN_SERVER_ERROR("[❎ ERROR] 서버에서 알 수 없는 에러가 발생했습니다.");

	private final String message;
}
