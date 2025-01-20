package com.authplayground.global.common.util;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionConstant {

	public static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);
}
