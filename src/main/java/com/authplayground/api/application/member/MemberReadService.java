package com.authplayground.api.application.member;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.stereotype.Service;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberReadService {

	private final MemberRepository memberRepository;

	public Member getMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));
	}

	public void validateMemeberEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException(DUPLICATED_EMAIL_FAILURE);
		}
	}

	public void validateMemberNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new ConflictException(DUPLICATED_NICKNAME_FAILURE);
		}
	}

	public void validateMemberRegistrationNumberDuplication(String registrationNumber) {
		if (memberRepository.existsMemberByRegistrationNumber(registrationNumber)) {
			throw new ConflictException(DUPLICATED_REGISTRATION_NUMBER_FAILURE);
		}
	}

	public void validatePasswordConfirmationMatch(String password, String passwordCheck) {
		if (!password.equals(passwordCheck)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}
}
