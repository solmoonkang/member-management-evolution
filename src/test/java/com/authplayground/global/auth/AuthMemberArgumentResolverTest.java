package com.authplayground.global.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.support.fixture.MemberFixture;
import com.authplayground.support.helper.AuthTestHelper;
import com.authplayground.support.helper.MemberTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthMemberArgumentResolver 통합 테스트")
class AuthMemberArgumentResolverTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("[✅ SUCCESS] @Auth AuthMember가 컨트롤러 파라미터에 성공적으로 주입되었습니다.")
	void authMemberParameterInjected_returnsVoid_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest);

		LoginRequest loginRequest = MemberFixture.createLoginRequest();
		MvcResult loginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest).andReturn();

		TokenResponse tokenResponse = objectMapper
			.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);

		// WHEN & THEN
		MemberTestHelper.performFind(mockMvc, tokenResponse)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(signUpRequest.email()))
			.andExpect(jsonPath("$.nickname").value(signUpRequest.nickname()))
			.andExpect(jsonPath("$.address").value(signUpRequest.address()));
	}
}
