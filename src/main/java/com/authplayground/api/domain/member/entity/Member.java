package com.authplayground.api.domain.member.entity;

import static java.util.Objects.*;

import com.authplayground.api.domain.member.model.Role;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TBL_MEMBERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false, unique = true)
	private String password;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "registration_number", nullable = false, unique = true)
	private String registrationNumber;

	@Column(name = "address", nullable = false)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;

	private Member(String email, String password, String nickname, String registrationNumber, String address, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.registrationNumber = registrationNumber;
		this.address = address;
		this.role = role;
	}

	public static Member createMember(SignUpRequest signUpRequest, String password, String registrationNumber) {
		return new Member(
			requireNonNull(signUpRequest.email()),
			requireNonNull(password),
			requireNonNull(signUpRequest.nickname()),
			requireNonNull(registrationNumber),
			requireNonNull(signUpRequest.address()),
			Role.MEMBER
		);
	}

	public void updateMember(UpdateRequest updateRequest) {
		this.nickname = updateRequest.nickname();
		this.address = updateRequest.address();
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
