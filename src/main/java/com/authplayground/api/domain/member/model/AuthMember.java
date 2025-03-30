package com.authplayground.api.domain.member.model;

import com.authplayground.api.domain.member.entity.Member;

public record AuthMember(String email, String nickname, Role role) {

	public static AuthMember from(Member member) {
		return new AuthMember(member.getEmail(), member.getNickname(), member.getRole());
	}
}
