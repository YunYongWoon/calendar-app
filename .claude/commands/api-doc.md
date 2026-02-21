API 문서 현황을 확인하고 보고해주세요.

## 확인 항목

1. **구현된 Controller 스캔**: `interfaces/rest/controller/` 내 모든 컨트롤러 파일 확인
2. **Swagger 어노테이션 검증**: @Tag, @Operation, @ApiResponse 누락 여부
3. **PROJECT_PLAN.md의 API 설계**와 현재 구현 비교
4. **누락된 API 엔드포인트** 식별
5. **DTO 검증**: 요청/응답 DTO에 @Schema 어노테이션 존재 여부

## Swagger UI 확인
로컬 서버가 실행 중이면 Playwright MCP로 Swagger UI 접속하여 스크린샷 캡처:
`http://localhost:8080/api/swagger-ui.html`

## 보고 형식
```
## API 문서 현황

### 구현된 엔드포인트
| Method | Path | Swagger 문서화 |
|--------|------|--------------|

### 누락된 엔드포인트
- [ ] METHOD /path — 설명

### 문서화 개선 필요
- ...
```
