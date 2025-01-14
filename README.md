# Auth-Playground 🙅🏻‍♂️

## 프로젝트 개요

Auth-Playground는 로그인 및 회원가입 기능을 구현하기 위해 시작한 사이드 프로젝트입니다.

이전 구름톤 트레이닝에서 맡았던 회원 관리 파트를 다시 공부하고, 추가적으로 개선사항이나 부족한 부분을 보완하기 위한 목적으로 개발하고 있습니다.

## 개발 환경

**Language**: JAVA 17, **Framework**: SpringBoot 3.3.4, **ORM**: JPA

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white" alt="Java"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"> <img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="JPA">
<br>

**Build-Tool**: Gradle, **Dev-Tool**: IntelliJ IDEA, **Test**: JUnit5, **Database**: H2

<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"> <img src="https://img.shields.io/badge/intellij-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA"> <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5"> <img src="https://img.shields.io/badge/h2-4CAF50?style=for-the-badge&logo=h2&logoColor=white" alt="H2">

## 구현 설명

1. **인증 및 인가 처리**
    - 사용자 인증 및 인가를 위한 시스템을 구현합니다. 
    - SpringSecurity + JWT + 세션을 활용하여 안전한 인증 절차를 마련하고, 권한에 따라 접근 제어를 설정합니다.

2. **회원가입 기능**
    - 사용자가 회원가입을 통해 계정을 생성할 수 있도록 합니다. 입력된 정보를 검증하고, 안전한 비밀번호 저장을 위해 해싱 기법을 사용합니다.

3. **로그인 기능**
    - 사용자 인증을 위해 로그인 기능을 구현합니다. 로그인 요청 시 JWT를 발급하여 클라이언트 측에서 인증을 유지할 수 있도록 합니다.

4. **테스트 코드**
    - JUnit5를 사용하여 각 기능에 대한 단위 테스트를 작성하여 코드의 신뢰성을 높입니다.

## 요구 상세 및 구현 사항

- [ ] **API 구현**
    - [ ] 회원가입 서비스 구현
    - [ ] 로그인 서비스 구현
- [ ] **확장**
    - [ ] 스프링 시큐리티 통합
    - [ ] JWT 기반 인증 구현
    - [ ] JWT + Session 기반 인증 방식 도입

추가적으로 구현할 기능이나 개선 사항이 있다면 목록을 늘려나갈 계획입니다.
