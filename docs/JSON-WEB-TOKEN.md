### JWT 기반 인증 학습 및 구현

- 스프링 시큐리티와 JWT를 활용한 토큰 기반 인증 흐름 학습
- 세션에 의존하지 않고 인증 정보를 HTTP 헤더를 통해 주고받는 방식 이해
- 로그인 / 회원가입 / 인가 처리 시 JWT 발급 및 검증 흐름 설계
- 필터 기반 인증 흐름 (UsernamePasswordAuthenticationFilter -> JwtAuthenticationFilter 등)
- @Auth 커스텀 어노테이션으로 인증된 사용자 정보 주입

#### 인증 및 인가 흐름

1. 사용자가 로그인 엔드포인트("/api/login)에 이메일 / 비밀번호 전달
2. 사용자의 이메일로 DB에서 회원 정보 조회
3. 인증 성공 시 사용자 정보를 기반으로 JWT(AccessToken) 생성
4. JWT는 응답 헤더 또는 바디에 포함되어 클라이언트에게 전달
5. 이후 요청에서는 JWT는 Authentication 헤더를 통해 전달 (Authorization: Bearer <AccessToken>)
6. 커스텀 필터(JwtAuthenticationFilter)가 JWT를 추출
7. 토큰이 유효하다면 Authentication 객체 생성 후 SecurityContext에 저장
8. 이후 인가가 필요한 요청마다 자동으로 인증 정보가 주입되어 인가 처리
9. 인증된 사용자 정보는 @Auth로 접근 가능

#### 회원가입 기능 설명

- 입력 항목: 이메일 / 비밀번호 / 닉네임 / 주소 / 주민등록번호 / 비밀번호 확인
- 중복 체크: 이메일, 닉네임, 주민등록번호는 각각 존재 여부 확인 후 예외 처리
- 암호화 처리:
    - 비밀번호 → BCryptPasswordEncoder.encode() 단방향 해싱
    - 주민번호 → AES128Util.encryptText() 양방향 암호화 후 저장
- 저장 방식: Member.createMember() 정적 팩토리 메서드를 통한 객체 생성

#### 로그인 기능 설명

- 입력 항목: 이메일 / 비밀번호
- 검증 로직:
    - 입력 이메일로 회원 조회
    - 입력 비밀번호와 DB의 암호화된 비밀번호 비교
- JWT 발급:
    - AccessToken 발급
    - RefreshToken 발급
- 반환 방식: AccessToken을 응답 헤더 또는 바디로 클라이언트에 전달

#### 요구 상세

###### 보안 요구사항

- 토큰은 Base64 인코딩된 서명 포함된 JWT 형식으로 발급
- 사용자 요청은 Authorization 헤더에 AccessToken을 담아서 인증 처리
- 유효하지 않거나 만료된 토큰은 401 UNAUTHORIZED 반환 | 권한이 부족한 경우 403 FORBIDDEN 반환
- 비밀번호는 BCrypt, 주민번호는 AES-128로 암호화하여 저장
- AccessToken은 짧은 수명, RefreshToken은 길게 유지
- RefreshToken은 Redis 또는 DB에 저장하여 재발급 흐름 관리

###### 기능 요구사항

- [ ] 회원가입 / 로그인 기능 구현
- [ ] JwtAuthenticationFilter 구현 | JWT 발급 및 검증 처리
- [ ] 인증 실패 / 인가 실패 예외 처리
- [ ] AccessToken / RefreshToken 설계 및 재발급 흐름 구현
- [ ] 토큰 만료 / 재발급 예외 흐름 정리


