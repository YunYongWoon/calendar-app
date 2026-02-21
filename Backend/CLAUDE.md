# Backend CLAUDE.md

Kotlin + Spring Boot API 서버. DDD · TDD · Clean Code 원칙 준수.

## 기술 스택

| 분류 | 기술 | 버전 |
|------|------|------|
| Language | Kotlin | 2.1.20 |
| Framework | Spring Boot | 3.5.11 |
| ORM | Spring Data JPA | Boot 관리 |
| DB (운영) | MySQL | - |
| DB (테스트) | H2 (MySQL MODE) | - |
| Test | Kotest + MockK + springmockk | 5.9.1 / 1.13.14 / 4.0.2 |
| Docs | springdoc-openapi | 2.8.8 |
| Build | Gradle Kotlin DSL | 8.13 |
| Java | 21 | - |

## 빌드 · 실행 명령어

```bash
# 테스트 포함 전체 빌드 (Backend/ 에서 실행)
./gradlew clean build

# 테스트만 실행
./gradlew test

# 로컬 서버 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# Swagger UI: http://localhost:8080/api/swagger-ui.html
# Health:     http://localhost:8080/api/health
```

## DDD 패키지 구조

```
src/main/kotlin/com/calendar/
├── domain/
│   ├── model/          # 엔티티, Value Object (순수 Kotlin, 프레임워크 의존 없음)
│   ├── repository/     # 레포지토리 인터페이스 (포트)
│   └── service/        # 도메인 서비스
├── application/
│   ├── usecase/        # 유스케이스 인터페이스
│   ├── service/        # 유스케이스 구현체 (@Service, @Transactional)
│   └── dto/            # Command(입력) / Result(출력) DTO
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/     # JPA @Entity (도메인 모델과 분리)
│   │   └── repository/ # 레포지토리 구현체
│   └── config/         # SecurityConfig, SwaggerConfig 등
└── interfaces/
    └── rest/
        ├── controller/ # REST 컨트롤러
        └── dto/        # 요청/응답 DTO
```

**레이어 의존성 방향**: `interfaces` → `application` → `domain` ← `infrastructure`

domain은 어떤 레이어도 의존하지 않는다.

## 코딩 컨벤션

### 네이밍
- 파일/클래스: `PascalCase`
- 함수/변수: `camelCase`
- 상수: `UPPER_SNAKE_CASE`
- 패키지: `lowercase`
- 테스트 클래스: `{대상클래스}Test`

### Kotlin 규칙
- `val` 우선, `var` 사용 시 주석으로 이유 명시
- `!!` 연산자 금지 → Elvis(`?:`) 또는 `let` 사용
- 도메인 객체는 `data class` 또는 `class`(불변 val 필드)로 선언
- `@Autowired` 필드 주입 금지 → 생성자 주입 사용

### 레이어 규칙
- `domain`에 `@Entity`, `@Service`, `@Repository` 등 Spring 어노테이션 금지
- `@Transactional`은 `application/service`에만 사용
- Controller는 비즈니스 로직 없음 (요청 변환 + 서비스 위임만)
- JPA Entity와 Domain Model은 별도 클래스로 분리

## 테스트 규칙

### 테스트 스타일 — Kotest DescribeSpec 필수

```kotlin
class XxxTest : DescribeSpec({
    extensions(SpringExtension)

    describe("메서드명") {
        context("조건") {
            it("기대 동작") {
                // given / when / then
            }
        }
    }
})
```

### 레이어별 테스트 방식

| 레이어 | 어노테이션 | 주의사항 |
|--------|-----------|---------|
| Controller | `@WebMvcTest` + `@Import(SecurityConfig::class)` | `@MockkBean` 사용 |
| Application Service | 순수 단위 테스트 | `mockk<Repository>()` |
| Repository | `@DataJpaTest` | H2 실사용 |
| Domain | 순수 단위 테스트 | 의존성 없음 |

- `@MockBean` 금지 → `@MockkBean` (springmockk) 사용
- Mockito 금지 → MockK만 사용
- 테스트 프로파일: `@ActiveProfiles("test")` → H2 in-memory

## 설정 파일

| 파일 | 위치 | 용도 |
|------|------|------|
| `application.yml` | `src/main/resources/` | 공통 (context-path: /api) |
| `application-local.yml` | `src/main/resources/` | MySQL 로컬 접속 |
| `application-test.yml` | `src/test/resources/` | H2 테스트 |

## AI 에이전트 (`.claude/agents/`)

| 에이전트 | 용도 | 사용 시점 |
|----------|------|----------|
| `spring-tdd` | TDD Red-Green-Refactor, Kotest DescribeSpec | 새 기능 구현 요청 시 반드시 사용 |
| `ddd-architect` | DDD 레이어 설계, Bounded Context, 도메인 모델링 | 도메인 설계·구조 검토 요청 시 |
| `api-designer` | REST API 설계, OpenAPI/Swagger 문서화, DTO 구조 | 새 API 엔드포인트 설계 시 |
| `spring-reviewer` | SOLID·Clean Code·Kotlin 관용구·보안 관점 코드 리뷰 | 코드 작성 완료 후 리뷰 요청 시 |
| `security-expert` | Spring Security, JWT, OAuth2, OWASP Top 10 방어 | 인증/인가 구현, 보안 취약점 점검 시 |
| `concurrency-expert` | 트랜잭션 격리, 낙관적/비관적 락, N+1 방지, 코루틴 동시성 | 동시성 버그, 락 전략, 성능 문제 해결 시 |
| `exception-architect` | 전역 예외 핸들러, 커스텀 예외 계층, 에러 응답 표준화 | 예외 처리 설계 및 에러 응답 구조 정의 시 |

## 현재 상태

- [x] Gradle Kotlin DSL 빌드 설정 완료
- [x] DDD 패키지 구조 생성 완료
- [x] SecurityConfig (Stateless, CSRF 비활성화)
- [x] SwaggerConfig (BearerAuth 준비)
- [x] HealthController (`GET /health`, `GET /health/detail`)
- [x] HealthControllerTest (Kotest DescribeSpec, 전체 통과)
- [ ] 도메인 기능 구현 예정
