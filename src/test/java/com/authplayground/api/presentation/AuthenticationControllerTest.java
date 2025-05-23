package com.authplayground.api.presentation;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static com.authplayground.support.TestConstant.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
@DisplayName("AuthenticationController 통합 테스트")
class AuthenticationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@AfterEach
	void tearDown() {
		stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
	}

	@Test
	@DisplayName("[✅ SUCCESS] loginMember - 사용자가 성공적으로 로그인을 하고, 토큰을 반환합니다.")
	void loginMember_returnsTokenResponse_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest)
			.andExpect(status().isOk());

		LoginRequest loginRequest = MemberFixture.createLoginRequest();

		// WHEN
		ResultActions loginResultActions = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andExpect(status().isOk());

		// THEN
		TokenResponse tokenResponse = objectMapper.readValue(
			loginResultActions.andReturn().getResponse().getContentAsString(), TokenResponse.class);

		AuthTestHelper.expectTokenResponse(loginResultActions);
		AuthTestHelper.expectRefreshTokenCreated(loginResultActions, tokenResponse.refreshToken());
	}

	@Test
	@DisplayName("[❎ FAILURE] loginMember - 존재하지 않는 이메일로 요청하여 로그인에 실패했습니다.")
	void loginMember_throwsNotFoundException_whenEmailNotFound_failure() throws Exception {
		// GIVEN
		LoginRequest invalidEmailRequest = MemberFixture.createLoginRequest("notfound@test.com", PASSWORD);

		// WHEN & THEN
		ResultActions loginResultActions = AuthTestHelper.performLogin(mockMvc, objectMapper, invalidEmailRequest)
			.andExpect(status().isNotFound());

		AuthTestHelper.expectFailureMessage(loginResultActions, MEMBER_NOT_FOUND_FAILURE.getMessage());
	}

	@Test
	@DisplayName("[❎ FAILURE] loginMember - 요청하신 비밀번호가 틀려서 로그인에 실패했습니다.")
	void loginMember_throwsBadRequestException_whenPasswordMismatch_failure() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest)
			.andExpect(status().isOk());

		LoginRequest wrongPasswordRequest = MemberFixture.createLoginRequestWithWrongPassword();

		// WHEN & THEN
		ResultActions loginResultActions = AuthTestHelper.performLogin(mockMvc, objectMapper, wrongPasswordRequest)
			.andExpect(status().isBadRequest());

		AuthTestHelper.expectFailureMessage(loginResultActions, PASSWORD_MISMATCH_FAILURE.getMessage());
	}

	@Test
	@DisplayName("[✅ SUCCESS] logoutMember - 로그인 이후 성공적으로 로그아웃 하였습니다.")
	void logoutMember_returnsVoid_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest)
			.andExpect(status().isOk());

		LoginRequest loginRequest = MemberFixture.createLoginRequest();
		MvcResult actualMvcResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andExpect(status().isOk())
			.andReturn();

		TokenResponse tokenResponse = objectMapper.readValue(
			actualMvcResult.getResponse().getContentAsString(), TokenResponse.class);

		String accessToken = tokenResponse.accessToken();

		// WHEN & THEN
		ResultActions logoutResultActions = AuthTestHelper.performLogout(mockMvc, accessToken)
			.andExpect(status().isOk())
			.andExpect(content().string("[✅ SUCCESS] 사용자 로그아웃을 성공적으로 완료했습니다."));

		AuthTestHelper.expectRefreshTokenDeleted(logoutResultActions);
	}

	@Test
	@DisplayName("[❎ FAILURE] logoutMember - 인증 토큰이 존재하지 않아 로그아웃에 실패했습니다.")
	void logoutMember_throwsUnauthorizedException_whenWithoutToken_failure() throws Exception {
		// WHEN & THEN
		ResultActions logoutResultActions = AuthTestHelper.performLogout(mockMvc)
			.andExpect(status().isUnauthorized());

		AuthTestHelper.expectFailureMessage(logoutResultActions, INVALID_AUTHORIZATION_HEADER.getMessage());
	}

	@Test
	@DisplayName("[❎ FAILURE] logoutMember - 유효하지 않은 토큰으로 접근하여 로그아웃에 실패했습니다.")
	void logoutMember_withInvalidToken_returnsUnauthorized_failure() throws Exception {
		// GIVEN
		String wrongAccessToken = BEARER_TYPE + "dummy.access.token";

		// WHEN & THEN
		ResultActions logoutResultActions = AuthTestHelper.performLogout(mockMvc, wrongAccessToken)
			.andExpect(status().isUnauthorized());

		AuthTestHelper.expectFailureMessage(logoutResultActions, INVALID_AUTHORIZATION_HEADER.getMessage());
	}

	@Test
	@DisplayName("[✅ SUCCESS] reissueToken - 유효한 AccessToken과 RefreshToken을 통해 새 토큰을 재발급했습니다.")
	void reissueMember_returnsTokenResponse_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest)
			.andExpect(status().isOk());

		LoginRequest loginRequest = MemberFixture.createLoginRequest();
		MvcResult actualLoginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andExpect(status().isOk())
			.andReturn();

		TokenResponse tokenResponse = objectMapper.readValue(
			actualLoginResult.getResponse().getContentAsString(), TokenResponse.class);

		// WHEN & THEN
		ResultActions reissueResultActions = AuthTestHelper.performReissue(mockMvc, tokenResponse)
			.andExpect(status().isOk());

		AuthTestHelper.expectTokenResponse(reissueResultActions);
		AuthTestHelper.expectRefreshTokenCreated(reissueResultActions, tokenResponse.refreshToken());
	}

	@Test
	@DisplayName("[❎ FAILURE] reissueToken - RefreshToken이 존재하지 않아 재발급에 실패했습니다.")
	void reissueToken_throwsUnauthorizedException_whenWithoutRefreshToken_failure() throws Exception {
		// GIVEN
		String dummyAccessToken = BEARER_TYPE + "dummy.access.token";

		// WHEN & THEN
		ResultActions reissueResultActions = AuthTestHelper.performReissue(mockMvc, dummyAccessToken)
			.andExpect(status().isUnauthorized());

		AuthTestHelper.expectFailureMessage(reissueResultActions, UNAUTHORIZED_REFRESH_TOKEN.getMessage());
	}

	@Test
	@DisplayName("[❎ FAILURE] reissueToken - 위조된 RefreshToken으로 인해 재발급에 실패했습니다.")
	void reissueToken_throwsUnauthorizedException_whenWithInvalidRefreshToken_failure() throws Exception {
		// GIVEN
		String wrongAccessToken = BEARER_TYPE + "dummy.access.token";
		String wrongRefreshToken = "invalid.refresh.token";

		// WHEN & THEN
		ResultActions reissueResultActions = AuthTestHelper.performReissue(mockMvc, wrongAccessToken, wrongRefreshToken)
			.andExpect(status().isUnauthorized());

		AuthTestHelper.expectFailureMessage(reissueResultActions, UNAUTHORIZED_REFRESH_TOKEN.getMessage());
	}

	@Test
	@DisplayName("[❎ FAILURE] reissueToken - 이미 사용한 RefreshToken으로 재발급에 실패했습니다.")
	void reissueToken_throwsUnauthorizedException_whenWithReusedRefreshToken_failure() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest);

		LoginRequest loginRequest = MemberFixture.createLoginRequest();
		MvcResult actualLoginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andReturn();

		TokenResponse originalToken = objectMapper.readValue(
			actualLoginResult.getResponse().getContentAsString(), TokenResponse.class);

		// WHEN & THEN
		MvcResult firstReissueResultActions = AuthTestHelper.performReissue(mockMvc, originalToken)
			.andExpect(status().isOk())
			.andReturn();

		Thread.sleep(300);

		ResultActions secondReissueResultActions = AuthTestHelper.performReissue(mockMvc, originalToken)
			.andExpect(status().isUnauthorized());

		AuthTestHelper.expectFailureMessage(secondReissueResultActions, REUSED_REFRESH_TOKEN.getMessage());
	}
}
