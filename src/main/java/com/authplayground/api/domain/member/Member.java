package com.authplayground.api.domain.member;

import org.hibernate.annotations.ColumnDefault;

import com.authplayground.api.dto.request.SignUpRequest;
import com.authplayground.global.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "nickname", unique = true)
	private String nickname;

	@Column(name = "password")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	@ColumnDefault("'MEMBER'")
	private Role role;

	@Builder
	private Member(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.role = Role.MEMBER;
	}

	public static Member createMember(SignUpRequest signUpRequest, String password) {
		return Member.builder()
			.email(signUpRequest.email())
			.nickname(signUpRequest.nickname())
			.password(password)
			.build();
	}
}
