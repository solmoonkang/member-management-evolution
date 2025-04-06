package com.authplayground.api.presentation;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static com.authplayground.support.TestConstant.*;
import static com.authplayground.support.helper.AuthTestHelper.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.support.fixture.MemberFixture;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthenticationController 통합 테스트")
class AuthenticationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("[✅ SUCCESS] loginMember - 사용자가 성공적으로 로그인을 하고, 토큰을 반환합니다.")
	void loginMember_returnsTokenResponse_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		LoginRequest loginRequest = MemberFixture.createValidLoginRequest();

		performSignup(mockMvc, objectMapper, signUpRequest).andExpect(status().isOk());

		// WHEN & THEN
		performLogin(mockMvc, objectMapper, loginRequest).andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists());
	}

	@Test
	@DisplayName("[❎ FAILURE] loginMember - 존재하지 않는 이메일로 요청하여 로그인에 실패했습니다.")
	void loginMember_throwsNotFoundException_whenEmailNotFound_failure() throws Exception {
		// GIVEN
		LoginRequest invalidEmailRequest = MemberFixture.createLoginRequest("notfound@test.com", PASSWORD);

		// WHEN & THEN
		performLogin(mockMvc, objectMapper, invalidEmailRequest).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND_FAILURE.getMessage()));
	}

	@Test
	@DisplayName("[❎ FAILURE] loginMember - 요청하신 비밀번호가 틀려서 로그인에 실패했습니다.")
	void loginMember_throwsBadRequestException_whenPasswordMismatch_failure() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

		performSignup(mockMvc, objectMapper, signUpRequest).andExpect(status().isOk());

		LoginRequest wrongPasswordRequest = MemberFixture.createWrongPasswordLoginRequest();

		// WHEN & THEN
		performLogin(mockMvc, objectMapper, wrongPasswordRequest).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(PASSWORD_MISMATCH_FAILURE.getMessage()));
	}
}
