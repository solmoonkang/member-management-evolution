package com.authplayground.api.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.AuthenticationService;
import com.authplayground.api.application.MemberService;
import com.authplayground.api.dto.member.LoginRequest;
import com.authplayground.api.dto.member.SignUpRequest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final MemberService memberService;
	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	public ResponseEntity<String> signUpMember(@RequestBody @Valid SignUpRequest signUpRequest) {
		memberService.signUpMember(signUpRequest);
		return ResponseEntity.ok("[✅ SUCCESS] 회원가입이 성공적으로 완료되었습니다.");
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "로그인", description = "기존 사용자가 로그인했습니다.")
	public ResponseEntity<String> loginMember(@RequestBody @Valid LoginRequest loginRequest) {
		authenticationService.authenticationMember(loginRequest);
		return ResponseEntity.ok("[✅ SUCCESS] 로그인이 성공적으로 완료되었습니다.");
	}
}
