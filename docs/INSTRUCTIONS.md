# 실행 방법 및 설정

## H2 데이터베이스 실행 방법

H2 데이터베이스에 접속하기 위해 아래의 URL을 사용할 수 있습니다.

- `H2 Console URL`: http://localhost:8082/h2-console
- `JDBC URL`: jdbc:h2:tcp://localhost/~/auth-playground
- `Driver Class Name`: org.h2.Driver
- `Username`: sa
- `Password`:

## Springdoc Swagger 실행 방법

- `Springdoc OpenApi Swagger URL`: http://localhost:8080/swagger-ui/index.html
- `Springdoc OpenApi Swagger`를 통해 APIs를 문서화했습니다.

## Redis 실행 방법

- `brew install redis` 명령어를 통해 Redis 설치를 진행한다.
- `redis-server` 명령어로 Redis 서버를 시작한다.
- `redis-cli ping` 명령어를 입력하여 PONG 응답을 확인하여, Redis 서버에 연결되었는지 확인한다.
- `brew services start redis` 명령어를 통해 Redis를 시스템 시작 시 자동으로 실행되도록 설정할 수 있다.
