package com.authplayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthPlaygroundApplicationTests {

	@Test
	@DisplayName("[✅ SUCCESS] contextLoads - Spring Boot 애플리케이션 컨텍스트가 성공적으로 로드되었습니다.")
	void contextLoads_success() {

	}
}
