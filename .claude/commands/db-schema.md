데이터베이스 스키마를 검증하고 현황을 보고해주세요.

## 확인 항목

1. **JPA Entity 스캔**: `Backend/src/main/kotlin/com/calendar/infrastructure/persistence/entity/` 내 모든 Entity 파일 확인
2. **PROJECT_PLAN.md의 DB 설계**와 현재 Entity 비교
3. **누락된 Entity** 식별
4. **관계 매핑 검증**: @OneToMany, @ManyToOne, @ManyToMany 올바른지 확인
5. **인덱스 설정 검증**: 필요한 인덱스가 @Index로 정의되어 있는지

## 보고 형식
```
## DB 스키마 현황

### 구현된 Entity
| Entity | 테이블명 | 상태 |
|--------|---------|------|

### 누락된 테이블
- [ ] 테이블명 — 설명

### 관계 매핑 이슈
- ...

### 인덱스 검토
- ...
```
