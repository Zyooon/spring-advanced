# 📱 SPRING ADVANCED

- **개발 기간 : 2025.06.05 ~ 2025.06.12**
- **spring advanced 코드 리팩토링 실습을 위한 API**

## 📌 주요 개선 사항

### 1. If문의 위치 및 구조 개선

- 조건에 맞지 않는 경우 즉시 리턴하여 불필요한 로직 실행 방지
    - `package org.example.expert.domain.auth.service` 의 `AuthService` 클래스에 있는 `signup()` 메서드의 이메일 확인 부분 위치 변경
- 복잡한 If-else 문 변경하여 가독성 증가 및 유지보수성 좋게 만듬
    - `package org.example.expert.client;` 의 `WeatherClient` 클래스에 있는 `getTodayWeather()` 메서드의 `if-else` 부분 리팩토링

### 2. Valid 활용

- `package org.example.expert.domain.user.service;` 의 `UserService` 클래스에 있는 `changePassword()` 메서드에서 비밀번호 정합성 검사 부분을 `Validation` 활용하여 `UserController` 클래스에서 검사하도록 변경

### 3. N+1 문제 개선

- `TodoController`와 `TodoService`를 통해 `Todo` 관련 데이터를 처리하는 부분에서 기존 `JPQL fetch join` 을 활용하는 부분을 `@EntityGraph` 를 활용하여 처리하도록 변경

```java
@EntityGraph(attributePaths = "user")
Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);
```

### 4. 테스트 코드 오류 수정

- `PasswordEncoderTest` 클래스의 `matches_메서드가_정상적으로_동작한다()` 의 matches 파라미터의 순서가 반대로 된 것 수정
- `ManagerServiceTest` 클래스의 `manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다()` 의 `assertEquals` 안의 파라미터가 **“Manager not found”** 로 되어있던 것을 **“Todo not found”** 로 수정
- `CommentServiceTest` 클래스의 `comment_등록_중_할일을_찾지_못해_에러가_발생한다()` 의 when 부분의 `ServerException` 구문을  `InvalidRequestException` 으로 수정
- `ManagerService` 클래스의 `saveManager()` 메서드에서 담당자를 등록하려는 유저와 일정을 만든 유저의 동일성 판단 부분에서 `todo.getUser() == null` 추가하여 null 방지하도록 추가

### 5. 관리자 로깅 기능 추가

- `/admin` URI 로 접근 시 **관리자(ADMIN)**인지 확인 후 관리자라면 접근 URI 및 접근 시간 로그로 남기도록 기능 추가

### 6. 코드 리팩토링

- JwtFilter 클래스의 doFilter() 메서드 가독성과 유지보수 향상을 위해 코드 리팩토링
- [JWT 필터 메서드 리팩토링 내용](https://velog.io/@wcw7373/06110525)

## 🛠️ 사용 기술

- **언어**: Java 17
- **프레임 워크** : Spring Boot 3.4.5
- **개발 환경**: IntelliJ IDEA
- **DB** : MySQL(JDBC Driver)
- **개발 도구** : Postman