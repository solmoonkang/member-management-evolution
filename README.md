## 🔒 Member-Management

**Spring Security + JWT + Session 기반의 인증 시스템**
토큰 기반 인증의 장점과 세션 기반 인증의 안정성을 모두 취하고, 실제 동작 가능한 수준의 인증 구조를 설계했습니다.

### 🧱 기술 스택

| 분류         | 기술                                |
|------------|-----------------------------------|
| Language   | Java 17                           |
| Framework  | Spring Boot 3.4.4                 |
| ORM        | JPA + Hibernate                   |
| DB         | H2 (테스트), Redis (RefreshToken 저장) |
| Build Tool | Gradle                            |
| 테스트        | JUnit 5, MockMvc                  |

### 🧪 테스트 전략 및 커버리지

- **단위 테스트**: 서비스, 유효성 검증, 토큰 유틸
- **통합 테스트**: 컨트롤러, 인증 필터 흐름 전체
- **테스트 커버리지**:
    - ✅ Class: `97%`
    - ✅ Method: `94%`
    - ✅ Line: `86%`

### 🔐 인증 흐름 요약

| 단계     | 설명                                                     |
|--------|--------------------------------------------------------|
| 로그인    | AccessToken + RefreshToken 발급, 동시에 Session에도 사용자 정보 저장 |
| 인증 요청  | 세션이 존재하면 세션 우선 인증, 없으면 JWT 인증                          |
| 인증 주입  | 커스텀 어노테이션 `@Auth`로 컨트롤러에 `AuthMember` 주입               |
| 토큰 재발급 | Redis에 저장된 RefreshToken과 비교하여 재사용 여부 확인 후 재발급          |
| 로그아웃   | AccessToken을 블랙리스트에 등록 + RefreshToken 삭제 + 세션 만료 처리    |

### ✨ 주요 기능 요약

- [x] Spring Security 기반 로그인 / 로그아웃 / 회원가입
- [x] JWT + Session 병행 인증 처리
- [x] RefreshToken Redis 저장 및 1회성 사용 처리
- [x] AccessToken 블랙리스트 등록
- [x] @Auth 커스텀 어노테이션 기반 인증 주입
- [x] Swagger 전역 인증 + 예외 명세 포함
- [x] 주민등록번호 AES-128 암호화

### 📚 API 문서

- Swagger URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 전역 인증 헤더 설정 가능 (AccessToken, RefreshToken)
- `ErrorResponse` 형식으로 통일된 예외 명세 제공

### 🧠 고민 포인트 & 해결

| 고민                  | 해결 방식                           |
|---------------------|---------------------------------|
| RefreshToken 재사용 공격 | Redis에 저장된 값과 비교하여 재사용 차단       |
| 토큰 탈취 대응            | AccessToken을 블랙리스트 등록 후 무효화     |
| 인증 주입 방식            | `@Auth` + ArgumentResolver로 추상화 |
| 테스트 커버리지 확보         | 통합 테스트 + 커버리지 리포트 정기 확인         |
