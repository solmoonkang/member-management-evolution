## 🔓 Member-Management

- **인증 시스템을 체계적으로 정리하고, 실제로 동작 가능한 수준까지 직접 설계 및 구현한 프로젝트입니다.**
- **JWT 기반 인증의 단점을 보완하기 위해 Spring Security + JWT + Session을 병행하는 하이브리드 구조로 설계했습니다.**

### 개발 환경

- **Language**: JAVA 17,
- **Framework**: SpringBoot 3.4.4,
- **ORM**: JPA
- **Build-Tool**: Gradle,
- **Dev-Tool**: IntelliJ IDEA,
- **Test**: JUnit5,
- **Database**: H2

### API 문서

- **URL**: http://localhost:8080/swagger-ui/index.html
- JWT 전역 인증 설정 + 요청 / 응답 스펙 명시
- 예외 처리 명세 (ErrorResponse)도 Swagger에 명시

### 테스트

- JUnit5 기반 단위 테스트 + 통합 테스트 작성
- 향후 컨트롤러, 인증 필터 레이어에 대한 통합 테스트 추가 예정

### 인증 흐름 설계

#### 1️⃣ 로그인

- 로그인 성공 시 AccessToken + RefreshToken을 발급합니다.
- 동시에 인증된 사용자 정보를 세션에도 저장합니다.
- @Auth 어노테이션을 통해 AuthMember 객체를 자동으로 주입합니다.

#### 2️⃣ 인증 요청 처리

- 세션이 존재할 경우 세션에서 인증 정보를 주입합니다.
- 세션이 없을 경우 JWT로 인증 및 SecurityContext에 주입합니다.
- 이미 인증된 사용자는 중복 파싱 없이 바로 접근이 가능합니다.

#### 3️⃣ 재발급

- 기존 RefreshToken이 Redis에 없거나 다를 경우 재사용으로 판단하여 차단합니다.
- 재발급 시 기존 AccessToken은 블랙리스트에 등록하여 무효화합니다.

#### 4️⃣ 로그아웃

- Redis에 저장된 RefreshToken을 삭제합니다.
- 현재 AccessToken은 Blacklist에 등록합니다.
- 세션 무효화 처리로 클라이언트 상태를 초기화합니다.

### 기능 구성

- [X] 로그인 / 로그아웃 / 토큰 재발급
- [X] JWT 토큰 기반 인증 (Access + Refresh 분리)
- [X] 세션 기반 인증 병행
- [X] 토큰 블랙리스트 처리
- [X] 1회성 리프레시 토큰 처리
- [X] 세션 고정 공격 방지
