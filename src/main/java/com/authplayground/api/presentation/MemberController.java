package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.auth.AuthenticationService;
import com.authplayground.api.application.member.MemberService;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.member.response.MemberInfoResponse;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.annotation.Auth;

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
	public ResponseEntity<TokenResponse> loginMember(@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok().body(authenticationService.loginMember(loginRequest));
	}

	@PostMapping("/reissue")
	public ResponseEntity<TokenResponse> reissueToken(HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok().body(authenticationService.reissueToken(httpServletRequest));
	}

	@GetMapping("/members")
	public ResponseEntity<MemberInfoResponse> findMemberInfo(@Auth AuthMember authMember) {
		return ResponseEntity.ok().body(memberService.findMemberInfo(authMember));
	}

	@PutMapping("/members")
	public ResponseEntity<String> updateMember(
		@Auth AuthMember authMember,
		@RequestBody @Valid UpdateRequest updateRequest) {

		memberService.updateMember(authMember, updateRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 수정을 성공적으로 완료했습니다.");
	}

	@DeleteMapping("/members")
	public ResponseEntity<String> deleteMember(@Auth AuthMember authMember) {
		memberService.deleteMember(authMember);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 삭제를 성공적으로 완료했습니다.");
	}
}
