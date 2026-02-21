프로젝트의 Conventional Commits 규칙에 맞는 커밋을 생성해주세요.

## 규칙

1. `git status`와 `git diff --staged`로 변경사항 파악
2. `git log --oneline -10`으로 최근 커밋 스타일 확인
3. Conventional Commits 형식으로 커밋 메시지 작성:
   - `feat:` 새 기능
   - `fix:` 버그 수정
   - `refactor:` 리팩터링
   - `test:` 테스트 추가/수정
   - `docs:` 문서 수정
   - `chore:` 빌드, 의존성 등
4. 커밋 메시지는 **한글**로 작성
5. 민감 파일(.env, credentials) 포함 여부 확인
6. 커밋 실행 전 사용자에게 메시지 확인 요청

## 예시
```
feat: 일정 생성 기능 추가
fix: 날짜 파싱 오류 수정
test: EventRepository 단위 테스트 추가
```
