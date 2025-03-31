package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.auth.AuthenticationService;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.annotation.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> loginMember(@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok().body(authenticationService.loginMember(loginRequest));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logoutMember(@Auth AuthMember authMember, HttpServletRequest httpServletRequest) {
		authenticationService.logoutMember(authMember, httpServletRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 로그아웃을 성공적으로 완료했습니다.");
	}

	@PostMapping("/reissue")
	public ResponseEntity<TokenResponse> reissueToken(HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok().body(authenticationService.reissueToken(httpServletRequest));
	}
}
