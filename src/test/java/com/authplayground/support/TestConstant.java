package com.authplayground.support;

import org.springframework.http.MediaType;

public class TestConstant {

	public static final String SIGNUP_URL = "/api/members/signup";
	public static final String LOGIN_URL = "/api/auth/login";
	public static final String LOGOUT_URL = "/api/auth/logout";
	public static final String REISSUE_URL = "/api/auth/reissue";

	public static final String MEMBER_URL = "/api/members";

	public static final MediaType JSON = MediaType.APPLICATION_JSON;

	public static final String EMAIL = "member@test.com";
	public static final String PASSWORD = "test123!";
	public static final String NICKNAME = "memberTest";
	public static final String REGISTRATION_NUMBER = "980521-1234567";
	public static final String ADDRESS = "경기도 구리시 인창동 123";

	public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
	public static final String BEARER_TYPE = "Bearer ";
}
