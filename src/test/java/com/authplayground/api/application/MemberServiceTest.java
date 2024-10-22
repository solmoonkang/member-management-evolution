package com.authplayground.api.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.SignUpRequest;
import com.authplayground.support.MemberFixture;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	MemberService memberService;

	@Mock
	MemberRepository memberRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("CREATE MEMBER (⭕️ SUCCESS): 사용자가 성공적으로 회원가입을 완료했습니다.")
	void createMember_void_success() {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.signUpMemberRequest();

		given(memberRepository.existsMemberByEmail(any(String.class))).willReturn(false);
		given(memberRepository.existsMemberByNickname(any(String.class))).willReturn(false);
		given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");

		// WHEN
		memberService.signUpMember(signUpRequest);

		// THEN
		verify(memberRepository).save(any(Member.class));
	}

	@Test
	@DisplayName("CREATE MEMBER (❌ FAILURE): 해당 이메일은 이미 존재하는 이메일입니다.")
	void createMember_email_IllegalStateException_fail() {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.signUpMemberRequest();

		given(memberRepository.existsMemberByEmail(any(String.class))).willReturn(true);

		// WHEN & THEN
		assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("[❎ ERROR] 입력하신 이메일은 이미 존재하는 이메일입니다.");
	}

	@Test
	@DisplayName("CREATE MEMBER (❌ FAILURE): 해당 닉네임은 이미 존재하는 닉네임입니다.")
	void createMember_nickname_IllegalStateException_fail() {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.signUpMemberRequest();

		given(memberRepository.existsMemberByNickname(any(String.class))).willReturn(true);

		// WHEN & THEN
		assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("[❎ ERROR] 입력하신 닉네임은 이미 존재하는 닉네임입니다.");
	}

	@Test
	@DisplayName("CREATE MEMBER (❌ FAILURE): 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
	void createMember_password_IllegalArgumentException_fail() {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.signUpMemberRequestWithDifferentPassword();

		// WHEN & THEN
		assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("[❎ ERROR] 입력하신 비밀번호와 일치하지 않습니다.");
	}
}
