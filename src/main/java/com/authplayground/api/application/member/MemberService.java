package com.authplayground.api.application.member;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.auth.CustomUserDetails;
import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.member.response.MemberInfoResponse;
import com.authplayground.global.common.util.AES128Util;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.global.error.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
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
		validateMemberNicknameDuplication(signUpRequest.nickname());
		validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);
		validatePasswordConfirmationMatch(signUpRequest.password(), signUpRequest.passwordCheck());

		final String encodedPassword = passwordEncoder.encode(signUpRequest.password());
		final Member member = Member.createMember(signUpRequest, encodedPassword, encodedRegistrationNumber);

		memberRepository.save(member);
	}

	@Transactional
	public void loginMember(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
		final Member member = memberRepository.findMemberByEmail(loginRequest.email())
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));

		validatePasswordMatches(loginRequest.password(), member.getPassword());

		CustomUserDetails userDetails = new CustomUserDetails(member);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		httpServletRequest.getSession(true).setAttribute(
			HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
	}

	public MemberInfoResponse findMemberInfo(Long memberId) {
		final Member member = getMemberById(memberId);

		return new MemberInfoResponse(member.getEmail(), member.getNickname(), member.getAddress());
	}

	@Transactional
	public void updateMember(Long memberId, UpdateRequest updateRequest) {
		final Member member = getMemberById(memberId);

		validateMemberNicknameDuplication(updateRequest.nickname());

		member.updateMember(updateRequest);
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));
	}

	private void validatePasswordMatches(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}

	private void validateMemeberEmailDuplication(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new ConflictException(DUPLICATED_EMAIL_FAILURE);
		}
	}

	private void validateMemberNicknameDuplication(String nickname) {
		if (memberRepository.existsMemberByNickname(nickname)) {
			throw new ConflictException(DUPLICATED_NICKNAME_FAILURE);
		}
	}

	private void validateMemberRegistrationNumberDuplication(String registrationNumber) {
		if (memberRepository.existsMemberByRegistrationNumber(registrationNumber)) {
			throw new ConflictException(DUPLICATED_REGISTRATION_NUMBER_FAILURE);
		}
	}

	private void validatePasswordConfirmationMatch(String password, String passwordCheck) {
		if (!password.equals(passwordCheck)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}
}
