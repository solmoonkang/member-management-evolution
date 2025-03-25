package com.authplayground.api.domain.entity;

import com.authplayground.api.domain.model.Role;
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

	@Column(name = "registration_number", nullable = false, unique = true)
	private String registrationNumber;

	@Column(name = "address", nullable = false)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	private Member(String email, String password, String address, Role role, String registrationNumber) {
		this.email = email;
		this.password = password;
		this.address = address;
		this.role = role;
		this.registrationNumber = registrationNumber;
	}
}
