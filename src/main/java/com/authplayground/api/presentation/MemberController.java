package com.authplayground.api.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.authplayground.api.application.member.MemberService;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.member.response.MemberInfoResponse;
import com.authplayground.global.auth.annotation.Auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 APIs", description = "회원가입 및 회원 정보 관리 API")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "[회원가입] 사용자 계정 생성",
		description = "요청된 사용자 정보를 바탕으로 신규 회원 계정을 생성합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "🎉 회원가입 완료"),
		@ApiResponse(responseCode = "400", description = "❌ 요청 데이터 형식 오류 또는 비밀번호 불일치"),
		@ApiResponse(responseCode = "409", description = "⚠️ 중복된 사용자 정보 존재"),
		@ApiResponse(responseCode = "500", description = "💥 서버 내부 오류 (예: AES 암호화 실패)")
	})
	public ResponseEntity<String> signUpMember(@RequestBody @Valid SignUpRequest signUpRequest) {
		memberService.signUpMember(signUpRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보를 성공적으로 생성했습니다.");
	}

	@GetMapping
	@Operation(
		summary = "[회원정보 조회] 로그인 사용자 정보 확인",
		description = "인증된 사용자의 이메일, 닉네임, 주소 정보를 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 사용자 정보 조회 성공"),
		@ApiResponse(responseCode = "401", description = "🔒 인증되지 않은 요청")
	})
	public ResponseEntity<MemberInfoResponse> findMemberInfo(@Auth AuthMember authMember) {
		return ResponseEntity.ok().body(memberService.findMemberInfo(authMember));
	}

	@PutMapping
	@Operation(
		summary = "[회원정보 수정] 사용자 닉네임 및 주소 변경",
		description = "인증된 사용자의 닉네임 및 주소를 수정합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 사용자 정보 수정 성공"),
		@ApiResponse(responseCode = "400", description = "❌ 요청 데이터 형식 오류"),
		@ApiResponse(responseCode = "409", description = "⚠️ 중복된 닉네임"),
		@ApiResponse(responseCode = "401", description = "🔒 인증되지 않은 요청")
	})
	public ResponseEntity<String> updateMember(
		@Auth AuthMember authMember,
		@RequestBody @Valid UpdateRequest updateRequest) {

		memberService.updateMember(authMember, updateRequest);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 수정을 성공적으로 완료했습니다.");
	}

	@DeleteMapping
	@Operation(
		summary = "[회원탈퇴] 사용자 계정 삭제",
		description = "현재 로그인한 사용자의 계정을 삭제합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "✅ 회원탈퇴 완료"),
		@ApiResponse(responseCode = "401", description = "🔒 인증되지 않은 요청"),
		@ApiResponse(responseCode = "500", description = "💥 서버 내부 오류")
	})
	public ResponseEntity<String> deleteMember(@Auth AuthMember authMember) {
		memberService.deleteMember(authMember);
		return ResponseEntity.ok().body("[✅ SUCCESS] 사용자 정보 삭제를 성공적으로 완료했습니다.");
	}
}
