package com.authplayground.api.application.member;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.global.common.util.AES128Util;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final AES128Util aes128Util;
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional
	public void signUpMember(SignUpRequest signUpRequest) {
		final String encodedRegistrationNumber = aes128Util.encryptText(signUpRequest.registrationNumber());

		validateMemeberEmailDuplication(signUpRequest.email());
		validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);
		validatePasswordMatch(signUpRequest.password(), signUpRequest.passwordCheck());

		final String encodedPassword = passwordEncoder.encode(signUpRequest.password());
		final Member member = Member.createMember(signUpRequest, encodedPassword, encodedRegistrationNumber);

		memberRepository.save(member);
	}

	private void validateMemeberEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException(DUPLICATED_EMAIL_FAILURE);
		}
	}

	private void validateMemberRegistrationNumberDuplication(String registrationNumber) {
		if (memberRepository.existsMemberByRegistrationNumber(registrationNumber)) {
			throw new ConflictException(DUPLICATED_REGISTRATION_NUMBER_FAILURE);
		}
	}

	private void validatePasswordMatch(String password, String passwordCheck) {
		if (!password.equals(passwordCheck)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}
}
