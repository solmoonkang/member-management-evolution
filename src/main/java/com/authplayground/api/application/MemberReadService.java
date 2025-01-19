package com.authplayground.api.application;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberReadService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	public void validateEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException(FAILED_EMAIL_DUPLICATION);
		}
	}

	public void validateNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new ConflictException(FAILED_NICKNAME_DUPLICATION);
		}
	}

	public void validatePasswordMatch(String password, String checkPassword) {
		if (!password.equals(checkPassword)) {
			throw new ConflictException(FAILED_PASSWORD_MISMATCH);
		}
	}

	public void validateLoginPasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(FAILED_INVALID_PASSWORD);
		}
	}
}
