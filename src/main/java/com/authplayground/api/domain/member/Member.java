package com.authplayground.api.domain.member;

import com.authplayground.api.dto.request.SignUpRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tbl_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "password")
	private String password;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Builder
	private Member(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
	}

	public static Member createMember(SignUpRequest signUpRequest, String password) {
		return Member.builder()
			.email(signUpRequest.email())
			.nickname(signUpRequest.nickname())
			.password(password)
			.build();
	}

	public void updateMemberRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
