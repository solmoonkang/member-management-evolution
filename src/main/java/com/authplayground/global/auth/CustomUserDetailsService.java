package com.authplayground.global.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authplayground.api.domain.member.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		final Member member = memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("[❎ ERROR] 요청하신 사용자를 찾을 수 없습니다."));

		return new CustomUserDetails(member);
	}
}
