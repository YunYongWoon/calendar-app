# CLAUDE.md

캘린더 앱 풀스택 프로젝트. 웹 · 웹뷰(앱) · 네이티브 앱을 단일 코드베이스로 지원한다.

## 프로젝트 구조

```
07_Calender/
├── Backend/    # Kotlin + Spring Boot API 서버
├── Frontend/   # React + TypeScript 웹/웹뷰
└── App/        # Flutter 네이티브 앱 (WebView 기반)
```

모듈별 상세 규칙은 각 디렉토리의 `CLAUDE.md` 참조.

## 공통 개발 원칙

- **TDD**: 테스트를 먼저 작성하고 구현한다
- **DDD**: 도메인 중심 설계, Ubiquitous Language 사용
- **Clean Code**: 의미 있는 이름, 단일 책임, 부수효과 최소화
- **SOLID**: 특히 SRP · OCP · DIP 준수
- 불변(immutable) 객체 선호, `var` 사용 시 이유 주석 필수

## Git 컨벤션

### 커밋 메시지 — Conventional Commits

```
feat: 일정 생성 기능 추가
fix: 날짜 파싱 오류 수정
refactor: EventService 레이어 분리
test: EventRepository 단위 테스트 추가
docs: API 명세 업데이트
chore: 의존성 버전 업그레이드
```

### 브랜치 전략

| 브랜치 | 용도 |
|--------|------|
| `main` | 배포 가능한 안정 버전 |
| `develop` | 통합 개발 브랜치 |
| `feature/*` | 새 기능 |
| `fix/*` | 버그 수정 |
| `release/*` | 배포 준비 |

- PR 단위로 병합, 코드 리뷰 필수
- 영문 네이밍, 약어 지양

## AI 에이전트 (`.claude/agents/`)

### Backend 에이전트

| 에이전트 | 용도 | 사용 시점 |
|----------|------|----------|
| `spring-tdd` | TDD Red-Green-Refactor, Kotest DescribeSpec | Backend 새 기능 구현 요청 시 반드시 사용 |
| `ddd-architect` | DDD 레이어 설계, Bounded Context, 도메인 모델링 | 도메인 설계·구조 검토 요청 시 |
| `api-designer` | REST API 설계, OpenAPI/Swagger 문서화, DTO 구조 | 새 API 엔드포인트 설계 시 |
| `spring-reviewer` | SOLID·Clean Code·Kotlin 관용구·보안 관점 코드 리뷰 | 코드 작성 완료 후 리뷰 요청 시 |
| `security-expert` | Spring Security, JWT, OAuth2, OWASP Top 10 방어 | 인증/인가 구현, 보안 취약점 점검 시 |
| `concurrency-expert` | 트랜잭션 격리, 낙관적/비관적 락, N+1 방지, 코루틴 동시성 | 동시성 버그, 락 전략, 성능 문제 해결 시 |
| `exception-architect` | 전역 예외 핸들러, 커스텀 예외 계층, 에러 응답 표준화 | 예외 처리 설계 및 에러 응답 구조 정의 시 |

### Frontend / App 에이전트

| 에이전트 | 용도 | 사용 시점 |
|----------|------|----------|
| `react-tdd` | React + TypeScript TDD, Vitest + Testing Library | Frontend 새 기능 구현 요청 시 반드시 사용 |
| `flutter-developer` | Flutter WebView 앱, JS Bridge, 네이티브 기능 | App 모듈 개발 시 |

### 공통 에이전트

| 에이전트 | 용도 | 사용 시점 |
|----------|------|----------|
| `db-designer` | DB 스키마 설계, JPA Entity 매핑, 인덱스 최적화 | 테이블 추가·변경, Entity 매핑 검토 시 |
| `integration-tester` | E2E/통합 테스트, 시나리오 기반 테스트, Playwright | 전체 흐름 테스트, API 통합 테스트 시 |
| `performance-tuner` | 쿼리 최적화, 캐싱, 렌더링 최적화, 번들 사이즈 | "느려요", 성능 개선 요청 시 |

## 슬래시 커맨드 (`.claude/commands/`)

| 커맨드 | 용도 |
|--------|------|
| `/commit` | Conventional Commits 규칙에 맞는 한글 커밋 생성 |
| `/review` | 변경 코드에 대한 코드 리뷰 실행 |
| `/test [module]` | 프로젝트 테스트 실행 및 결과 보고 |
| `/build [module]` | 프로젝트 빌드 실행 및 결과 보고 |
| `/sprint-status` | 스프린트 진행 현황 보고 |
| `/db-schema` | DB 스키마 현황 검증 및 보고 |
| `/api-doc` | API 문서 현황 확인 및 보고 |
| `/new-feature [설명]` | TDD 방식으로 새 기능 구현 (설계→API→TDD→리뷰) |

## MCP 서버

| MCP | 용도 |
|-----|------|
| `playwright` | 브라우저 자동화, E2E 테스트, Swagger UI 확인 |
| `github` | GitHub PR/Issue 관리, 코드 리뷰 |
