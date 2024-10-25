package com.authplayground.global.error.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {

	FAILED_UNKNOWN_ERROR("서버에서 알 수 없는 에러가 발생했습니다.");

	private final String message;
}
