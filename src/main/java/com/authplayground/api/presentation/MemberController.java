package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.auth.AuthenticationService;
import com.authplayground.api.application.member.MemberService;
import com.authplayground.api.domain.auth.CustomUserDetails;
import com.authplayground.api.dto.member.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.member.response.MemberInfoResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

	private final AuthenticationService authenticationService;
	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUpMember(@RequestBody @Valid SignUpRequest signUpRequest) {
		memberService.signUpMember(signUpRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보를 성공적으로 생성했습니다.");
	}

	@PostMapping("/login")
	public ResponseEntity<String> loginMember(
		@RequestBody @Valid LoginRequest loginRequest,
		HttpServletRequest httpServletRequest) {

		authenticationService.loginMember(loginRequest, httpServletRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 인증을 성공적으로 완료했습니다.");
	}

	@GetMapping
	public ResponseEntity<MemberInfoResponse> findMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok().body(memberService.findMemberInfo(userDetails.member().getId()));
	}

	@PutMapping("/members/{memberId}/update")
	public ResponseEntity<String> updateMember(
		@PathVariable Long memberId,
		@RequestBody @Valid UpdateRequest updateRequest) {

		memberService.updateMember(memberId, updateRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 수정을 성공적으로 완료했습니다.");
	}

	@DeleteMapping("/members/{memberId}")
	public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
		memberService.deleteMember(memberId);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 삭제를 성공적으로 완료했습니다.");
	}
}
