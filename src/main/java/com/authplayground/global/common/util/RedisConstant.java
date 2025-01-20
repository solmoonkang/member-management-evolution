package com.authplayground.global.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstant {

	public static final String REDIS_REFRESH_TOKEN_PREFIX = "refreshToken";
	public static final String REDIS_SESSION_PREFIX = "session";

	public static final int EXPIRE_DAYS = 14;
}
