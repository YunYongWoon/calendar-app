# Frontend CLAUDE.md

React + TypeScript 웹 앱. 브라우저와 Flutter WebView 양쪽에서 동작해야 한다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | TypeScript |
| Framework | React |
| Test | Vitest + React Testing Library |
| Build | Vite |
| Style | TBD (Tailwind CSS 또는 CSS Modules 고려) |

## 디렉토리 구조 (예정)

```
src/
├── components/   # 재사용 UI 컴포넌트
├── hooks/        # 커스텀 훅
├── pages/        # 페이지 단위 컴포넌트
├── services/     # API 통신, 외부 서비스
├── types/        # TypeScript 타입 정의
└── utils/        # 순수 유틸리티 함수
```

## 코딩 컨벤션

### 네이밍
- 컴포넌트 · 파일명: `PascalCase` (`EventCard.tsx`)
- 함수 · 변수: `camelCase`
- 타입 · 인터페이스: `PascalCase` (접두사 `I` 사용 금지)
- 커스텀 훅: `use` 접두사 (`useCalendar`, `useEvent`)
- 상수: `UPPER_SNAKE_CASE`

### 웹뷰 환경 분기
- 브라우저 / Flutter WebView 환경 분기는 **추상화 레이어로 분리**
- 컴포넌트 내 직접 `window.flutter_inappwebview` 등 접근 금지
- `services/webview/` 또는 `hooks/useWebViewBridge.ts` 형태로 추상화

### 테스트
- 컴포넌트 테스트: React Testing Library (`userEvent` 사용)
- 훅 테스트: `renderHook`
- 구현이 아닌 **사용자 동작** 관점으로 테스트 작성

## 현재 상태

- [ ] 프로젝트 초기 설정 예정 (Vite + React + TypeScript)
