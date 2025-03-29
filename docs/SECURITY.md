### Spring Security (세션 기반 인증) 학습

- Spring Security의 세션 기반 인증 흐름 전체 이해
- CustomUserDetails / UserDetailsService 구현 및 활용
- 인증 객체의 SecurityContextHolder 저장 원리와 ThreadLocal 동작 이해
- @AuthenticationPrincipal 커스텀을 통해 @Auth 생성 및 인증 주입 방식 비교
- 세션 저장과 로그아웃, 동시 접속 제어 등 인증 상태 관리
- BCrypt(단방향 암호화) + AES-128(양방향 암호화 / 복호화) 알고리즘 적용

#### 인증 및 인가 흐름

1. 사용자 로그인("/api/login") 요청
2. 사용자 이메일로 회원 조회 -> 비밀번호 검증(BCryptPasswordEncoder.matches())
3. 인증 성공 시 CustomUserDetails 생성
4. UsernamePasswordAuthenticationToken으로 인증 객체 생성
5. SecurityContext에 인증 객체 저장 후 SecurityContextHolder에 주입
6. 세션에 SPRING_SECURITY_CONTEXT 키로 인증 객체 저장
7. 이후 요청마다 인증 객체가 자동 주입되어 인가 처리
8. @Auth를 통해 인증 주입 가능

#### 회원가입 기능 설명

- 회원가입 시 입력 항목:
    - 이메일 / 비밀번호 / 닉네임 / 주소 / 주민등록번호 / 비밀번호 확인
- 중복 체크:
    - 이메일, 닉네임, 주민등록번호는 각각 존재 여부 확인 후 예외 처리
- 암호화 처리:
    - 비밀번호 → BCryptPasswordEncoder.encode() 단방향 해싱
    - 주민번호 → AES128Util.encryptText() 양방향 암호화 후 저장
- 저장:
    - Member.createMember() 정적 팩토리 메서드로 객체 생성 후 DB 저장

#### 로그인 기능 설명

- 로그인 시 입력 항목:
    - 이메일 / 비밀번호
- 검증 로직:
    - 입력 이메일로 회원 조회
    - 입력 비밀번호와 DB의 암호화된 비밀번호 비교
- 인증 성공 시 처리:
    - CustomUserDetails 객체 생성
    - UsernamePasswordAuthenticationToken 으로 인증 객체 구성
    - SecurityContext를 생성하고 인증 객체 설정 → SecurityContextHolder에 저장
    - 세션에 SPRING_SECURITY_CONTEXT 키로 인증 객체 저장

#### 요구 상세

###### 보안 요구

- 회원가입 시 민감 정보 암호화 저장
    - 주민등록번호는 AES128 대칭키 방식으로 암호화 | 비밀번호는 BCrypt 해시 함수를 사용하여 저장
- 로그인 이후 인증 객체는 SecurityContext를 통해 세션에 저장, 이후 요청 시 자동 인증 처리
- 인증 실패 시 401 UNAUTHORIZED, 인가 실패 시 403 FORBIDDEN 상태 코드 반환
- 세션 고정 공격 방지를 위해 로그인 시 세션 ID를 새로 생성
- 동일 사용자 계정으로의 중복 로그인 제한, maximumSessions
    - 이미 로그인된 세션이 있는 경우 새 로그인 거부, maxSessionsPreventsLogin

###### 기능 요구

- [ ] 회원가입 / 로그인 / 로그아웃 기능 구현
- [ ] 사용자 정보 조회 / 수정 / 삭제 기능 구현
- [ ] @Auth AuthMember를 통한 인증 주입 방식 제공 (유지보수성과 가독성 확보)


