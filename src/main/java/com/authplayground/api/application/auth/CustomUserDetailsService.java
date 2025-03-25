package com.authplayground.api.application.auth;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authplayground.api.domain.auth.CustomUserDetails;
import com.authplayground.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return memberRepository.findMemberByEmail(email)
			.map(CustomUserDetails::new)
			.orElseThrow(() -> new UsernameNotFoundException(MEMBER_NOT_FOUND_FAILURE.getMessage()));
	}
}
