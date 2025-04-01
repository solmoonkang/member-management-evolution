package com.authplayground.api.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.auth.AuthenticationService;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.auth.response.LoginResponse;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.global.auth.annotation.Auth;
import com.authplayground.global.common.util.SessionManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 APIs", description = "로그인, 로그아웃, 토큰 재발급 기능을 제공합니다.")
public class AuthenticationController {

	private final SessionManager sessionManager;
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	@Operation(
		summary = "[로그인] 사용자 인증 및 토큰 발급",
		description = "사용자의 이메일과 비밀번호를 검증한 뒤, AccessToken과 RefreshToken을 발급합니다. 세션에 사용자 인증 정보를 저장합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 로그인 성공 및 토큰 발급"),
		@ApiResponse(responseCode = "400", description = "❌ 형식 오류 또는 비밀번호 불일치"),
		@ApiResponse(responseCode = "404", description = "🔍 존재하지 않는 사용자"),
		@ApiResponse(responseCode = "500", description = "💥 서버 내부 오류")
	})
	public ResponseEntity<TokenResponse> loginMember(
		@RequestBody @Valid LoginRequest loginRequest,
		HttpServletRequest httpServletRequest) {

		LoginResponse loginResponse = authenticationService.loginMember(loginRequest);
		sessionManager.saveAuthMember(httpServletRequest, loginResponse.authMember());

		return ResponseEntity.ok().body(loginResponse.tokenResponse());
	}

	@PostMapping("/logout")
	@Operation(
		summary = "[로그아웃] 사용자 로그아웃 및 토큰 무효화",
		description = "현재 인증된 사용자의 액세스 토큰을 블랙리스트에 등록하고, Redis에 저장된 리프레시 토큰을 삭제하며, 세션을 만료시킵니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 로그아웃 성공"),
		@ApiResponse(responseCode = "401", description = "🔒 인증되지 않은 사용자 요청"),
		@ApiResponse(responseCode = "500", description = "💥 서버 내부 오류")
	})
	public ResponseEntity<String> logoutMember(@Auth AuthMember authMember, HttpServletRequest httpServletRequest) {
		authenticationService.logoutMember(authMember, httpServletRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 로그아웃을 성공적으로 완료했습니다.");
	}

	@PostMapping("/reissue")
	@Operation(
		summary = "[토큰 재발급] RefreshToken을 통한 AccessToken 재발급",
		description = "요청 헤더에 있는 RefreshToken을 검증한 뒤, 새로운 AccessToken과 RefreshToken을 발급합니다. 기존 AccessToken은 블랙리스트에 등록되어 무효화 처리됩니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 토큰 재발급 성공"),
		@ApiResponse(responseCode = "401", description = "🔒 토큰이 유효하지 않거나 재사용된 경우"),
		@ApiResponse(responseCode = "404", description = "🔍 존재하지 않는 사용자"),
		@ApiResponse(responseCode = "500", description = "💥 서버 내부 오류")
	})
	public ResponseEntity<TokenResponse> reissueToken(HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok().body(authenticationService.reissueToken(httpServletRequest));
	}
}
