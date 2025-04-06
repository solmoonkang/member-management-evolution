package com.authplayground.support.fixture;

import com.authplayground.api.domain.member.model.AuthMember;

public class JwtFixture {

	public static AuthMember createAuthMember() {
		return MemberFixture.createAuthMember();
	}
}
