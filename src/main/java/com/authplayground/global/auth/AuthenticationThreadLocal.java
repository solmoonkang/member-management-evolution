package com.authplayground.global.auth;

import com.authplayground.api.domain.auth.AuthMember;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationThreadLocal {

	private static final ThreadLocal<AuthMember> authMemberHolder = new ThreadLocal<>();

	public static void setAuthMemberHolder(AuthMember authMember) {
		AuthenticationThreadLocal.authMemberHolder.set(authMember);
	}

	public static AuthMember getAuthMemberHolder() {
		return authMemberHolder.get();
	}

	public static void removeAuthMemberHolder() {
		authMemberHolder.remove();
	}
}
