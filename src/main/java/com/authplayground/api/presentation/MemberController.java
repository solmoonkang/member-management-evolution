package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.member.MemberService;
import com.authplayground.api.dto.member.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUpMember(@RequestBody @Valid SignUpRequest signUpRequest) {
		memberService.signUpMember(signUpRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보를 성공적으로 생성했습니다.");
	}

	@PostMapping("/login")
	public ResponseEntity<String> loginMember(@RequestBody @Valid LoginRequest loginRequest) {
		memberService.loginMember(loginRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 인증을 성공적으로 완료했습니다.");
	}
}
