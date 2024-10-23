package com.authplayground.global.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.support.MemberFixture;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@InjectMocks
	CustomUserDetailsService customUserDetailsService;

	@Mock
	MemberRepository memberRepository;

	@Test
	@DisplayName("LOAD USER BY USERNAME (⭕️ SUCCESS): 해당 사용자 정보를 성공적으로 로드합니다.")
	void loadUserByUsername_void_success() {
		// GIVEN
		Member member = MemberFixture.createMember();

		given(memberRepository.findMemberByEmail(member.getEmail())).willReturn(Optional.of(member));

		// WHEN
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(member.getEmail());

		// THEN
		assertThat(userDetails.getUsername()).isEqualTo(member.getEmail());
	}

	@Test
	@DisplayName("LOAD USER BY USERNAME (❌ FAILURE): 해당 사용자 정보를 로드하는데 실패했습니다.")
	void loadUserByUsername_UsernameNotFoundException_fail() {
		// GIVEN
		String email = "nonexistent@example.com";

		given(memberRepository.findMemberByEmail(email)).willReturn(Optional.empty());

		// WHEN & THEN
		assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage("[❎ ERROR] 요청하신 사용자를 찾을 수 없습니다.");
	}
}
