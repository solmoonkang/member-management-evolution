package com.authplayground.api.domain.auth;

import java.util.Objects;

import com.authplayground.api.domain.member.Role;

public record AuthMember(
	String email,
	String nickname,
	Role role
) {

	public static AuthMember createAuthMember(String email, String nickname, Role role) {
		return new AuthMember(
			Objects.requireNonNull(email),
			Objects.requireNonNull(nickname),
			Objects.requireNonNull(role)
		);
	}
}
