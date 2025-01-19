package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.member.MemberService;
import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.api.dto.response.MemberInfoResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/search")
	@Operation(summary = "회원 정보 조회", description = "인증된 사용자의 회원 정보를 조회합니다." + "사용자의 이메일, 닉네임 및 역할 정보를 포함합니다.")
	public ResponseEntity<MemberInfoResponse> searchMemberInfo(AuthMember authMember) {
		return ResponseEntity.ok(memberService.searchMemberInfo(authMember));
	}
}
