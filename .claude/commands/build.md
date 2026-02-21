프로젝트 빌드를 실행하고 결과를 보고해주세요.

## 인자
- `$ARGUMENTS` — 빌드 대상 모듈 (backend / frontend / all). 기본값: all

## 빌드 방식

### Backend
```bash
cd Backend && ./gradlew clean build 2>&1
```
- 빌드 실패 시 에러 원인 분석 및 수정 제안
- 경고(warning) 목록 확인

### Frontend
```bash
cd Frontend && npm run build 2>&1
```
- TypeScript 컴파일 에러 확인
- ESLint 경고/에러 확인

### 결과 보고
- 빌드 성공/실패 여부
- 경고 사항
- 빌드 시간
