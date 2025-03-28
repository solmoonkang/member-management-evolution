package com.authplayground.api.domain.auth;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.Role;

public record AuthMember(Long id, String email, Role role) {

	public static AuthMember from(Member member) {
		return new AuthMember(member.getId(), member.getEmail(), member.getRole());
	}
}
