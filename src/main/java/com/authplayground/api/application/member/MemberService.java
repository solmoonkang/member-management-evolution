package com.authplayground.api.application.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.member.response.MemberInfoResponse;
import com.authplayground.global.common.util.AES128Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final AES128Util aes128Util;
	private final MemberReadService memberReadService;
	private final MemberWriteService memberWriteService;

	@Transactional
	public void signUpMember(SignUpRequest signUpRequest) {
		final String encodedRegistrationNumber = aes128Util.encryptText(signUpRequest.registrationNumber());

		memberReadService.validateMemeberEmailDuplication(signUpRequest.email());
		memberReadService.validateMemberNicknameDuplication(signUpRequest.nickname());
		memberReadService.validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);
		memberReadService.validatePasswordConfirmationMatch(signUpRequest.password(), signUpRequest.passwordCheck());

		memberWriteService.createMember(signUpRequest, encodedRegistrationNumber);
	}

	public MemberInfoResponse findMemberInfo(AuthMember authMember) {
		final Member member = memberReadService.getMemberByEmail(authMember.email());
		return new MemberInfoResponse(member.getEmail(), member.getNickname(), member.getAddress());
	}

	@Transactional
	public void updateMember(AuthMember authMember, UpdateRequest updateRequest) {
		final Member member = memberReadService.getMemberByEmail(authMember.email());
		memberReadService.validateMemberNicknameDuplication(updateRequest.nickname());
		memberWriteService.updateMember(member, updateRequest);
	}

	@Transactional
	public void deleteMember(AuthMember authMember) {
		final Member member = memberReadService.getMemberByEmail(authMember.email());
		memberWriteService.deleteMember(member);
	}
}
