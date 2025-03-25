package com.authplayground.global.error.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {

	FAILED_AES_INIT_FAILURE("[❎ ERROR] AES 초기화 중 예외가 발생했습니다."),
	FAILED_AES_ENCRYPT_FAILURE("[❎ ERROR] AES 암호화 중 예외가 발생했습니다."),
	FAILED_AES_DECRYPT_FAILURE("[❎ ERROR] AES 복호화 중 예외가 발생했습니다.");

	private final String message;
}
