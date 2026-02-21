프로젝트 테스트를 실행하고 결과를 보고해주세요.

## 인자
- `$ARGUMENTS` — 테스트 대상 모듈 (backend / frontend / all). 기본값: all

## 실행 방식

### Backend (Kotlin + Spring Boot)
```bash
cd Backend && ./gradlew test 2>&1
```
- 실패 시 테스트 리포트 파일 확인: `Backend/build/reports/tests/test/index.html`
- 실패한 테스트 목록과 원인 분석

### Frontend (React + TypeScript)
```bash
cd Frontend && npm test 2>&1
```

### 결과 보고
- 전체 테스트 수 / 성공 / 실패 / 스킵
- 실패한 테스트 상세 원인
- 수정 제안 (있을 경우)
