# 공유 캘린더 앱 - 프로젝트 기획안

## 1. 프로젝트 개요

### 1.1 서비스 비전
친구, 커플, 가족 등 소규모 그룹이 일정을 공유하고 소통할 수 있는 **공유 캘린더 앱**.
단순한 일정 관리를 넘어, 일정에 대한 **의견(댓글/이모지 반응)** 을 주고받으며
그룹 내 소통과 유대감을 강화하는 것이 핵심 가치.

### 1.2 타겟 사용자
| 그룹 유형 | 주요 시나리오 |
|-----------|-------------|
| **커플** | 기념일 관리, 데이트 일정, D-Day 카운트 |
| **친구 모임** | 정기 모임 조율, 여행 계획, 투표로 날짜 선정 |
| **가족** | 가족 행사, 생일, 병원 방문 등 공유 |

### 1.3 플랫폼
- **웹 (브라우저)**: React + TypeScript
- **모바일 앱 (Android/iOS)**: Flutter WebView 기반
- **백엔드 API**: Kotlin + Spring Boot

---

## 2. 핵심 기능 (MVP)

### 2.1 회원 관리
- 이메일 + 비밀번호 회원가입 / 로그인
- JWT 기반 인증 (Access Token + Refresh Token)
- 프로필 관리 (닉네임, 프로필 이미지)
- 소셜 로그인 (Google, Kakao) — Phase 2

### 2.2 그룹(캘린더) 관리
- 그룹 생성 (이름, 유형, 커버 이미지)
- **초대 코드**로 멤버 초대 (6자리 영숫자, 24시간 유효)
- 멤버 역할: `OWNER` / `ADMIN` / `MEMBER`
- 그룹 내 개인 표시명 · 색상 설정
- 한 사용자가 여러 그룹에 소속 가능 (최대 10개)

### 2.3 일정 관리
- 일정 CRUD (제목, 설명, 시작/종료 시간, 장소)
- **종일 일정** 지원
- 일정 카테고리: 일반 / 기념일 / 생일 / 모임 / 여행 / 기타
- 카테고리별 기본 색상 + 커스텀 색상
- **반복 일정**: 매일 / 매주 / 매월 / 매년 (종료일 또는 횟수 지정)
- 일정 참석 응답: 참석 / 불참 / 미정

### 2.4 댓글 · 반응 (핵심 차별점)
- 일정에 **텍스트 댓글** 작성
- 일정에 **이모지 반응** (👍❤️😂🎉😢😮 등 사전 정의 6종)
- 댓글에 대한 답글 (1depth)
- 실시간 반영 (WebSocket or SSE)

### 2.5 알림
- 일정 생성/수정 시 그룹 멤버에게 알림
- 새 댓글/반응 알림
- 일정 리마인더 (10분 전, 30분 전, 1시간 전, 1일 전)
- 인앱 알림 + 푸시 알림 (FCM)

### 2.6 캘린더 뷰
- **월간 뷰**: 한 달 전체 일정 확인 (기본 뷰)
- **주간 뷰**: 시간대별 상세 뷰
- **일간 뷰**: 하루 타임라인
- **일정 목록 뷰**: 리스트 형태로 일정 확인
- 그룹별 필터링 / 멤버별 필터링

---

## 3. 추가 기능 (Phase 2)

### 3.1 D-Day 카운트
- 기념일 · 생일 이벤트 자동 D-Day 표시
- 홈 화면 위젯에 D-Day 노출
- 커플 사귄 날, 가족 생일 등 자동 카운트

### 3.2 일정 투표 (날짜 조율)
- 후보 날짜를 여러 개 제시하고 멤버가 투표
- 가장 많은 표를 받은 날짜 자동 확정 (or 수동 확정)
- 익명 투표 옵션

### 3.3 사진 · 파일 첨부
- 일정에 사진 첨부 (최대 5장)
- 여행 후 사진 공유, 모임 사진 기록
- 파일 첨부 (PDF, 문서 등)

### 3.4 날씨 정보 연동
- 일정 날짜의 날씨 예보 자동 표시
- 야외 일정 시 유용

### 3.5 위치 기반 기능
- 일정 장소 지도 표시
- 장소 검색 (Kakao/Google Maps API)
- 출발 시간 추천 (예상 이동 시간)

### 3.6 소셜 로그인
- Google OAuth 2.0
- Kakao 로그인

### 3.7 캘린더 가져오기/내보내기
- iCal (.ics) 형식 가져오기/내보내기
- Google Calendar 연동

### 3.8 앱 위젯
- 오늘의 일정 위젯
- D-Day 위젯
- 다가오는 일정 목록 위젯

---

## 4. UI/UX 디자인 방향

### 4.1 디자인 컨셉
- **미니멀 & 따뜻한**: 깔끔한 레이아웃, 라운드 카드, 부드러운 색상
- **그룹 아이덴티티**: 그룹마다 테마 색상, 멤버마다 고유 색상
- **직관적 인터랙션**: 스와이프로 날짜 이동, 길게 눌러 일정 생성, 탭으로 반응

### 4.2 색상 팔레트
```
Primary:    #4A90D9 (캘린더 블루)
Secondary:  #FF6B6B (액센트 핑크-레드)
Background: #F8F9FA (라이트 그레이)
Surface:    #FFFFFF (화이트 카드)
Text:       #2D3436 (다크 그레이)
Muted:      #95A5A6 (서브 텍스트)
```

### 4.3 주요 화면 구성

#### (1) 홈 — 오늘의 캘린더
```
┌──────────────────────────────┐
│  ☰  2026년 2월              🔔 │  ← 상단 헤더 (햄버거 메뉴, 알림)
├──────────────────────────────┤
│  일  월  화  수  목  금  토    │  ← 요일 헤더
│  25  26  27  28  29  30  31   │
│   1   2   3   4   5   6   7   │
│   8   9  10  11  12  [13] 14  │  ← 오늘 날짜 강조
│  15  16  17  18  19  20  21   │
│  22  23  24  25  26  27  28   │
├──────────────────────────────┤
│  📅 2/13 (금) 오늘의 일정       │  ← 선택 날짜 일정 목록
│ ┌────────────────────────────┐│
│ │ 🔴 데이트 14:00~18:00      ││  ← 일정 카드 (색상 = 그룹 색)
│ │    강남역 / 💬 3  👍 2      ││  ← 장소, 댓글수, 반응수
│ └────────────────────────────┘│
│ ┌────────────────────────────┐│
│ │ 🔵 팀 회의 19:00~20:00     ││
│ │    온라인 / 💬 1            ││
│ └────────────────────────────┘│
├──────────────────────────────┤
│  🏠    📅    ➕    👥    ⚙️   │  ← 하단 네비게이션
│  홈   캘린더  생성  그룹  설정   │
└──────────────────────────────┘
```

#### (2) 일정 상세 + 댓글/반응
```
┌──────────────────────────────┐
│  ←  일정 상세              ✏️ 🗑 │
├──────────────────────────────┤
│                              │
│  🎂 민지 생일                 │  ← 카테고리 아이콘 + 제목
│  2026.02.14 (토) 종일         │
│  📍 민지네 집                 │
│                              │
│  D-100 🎉                    │  ← D-Day 표시
│                              │
│  ─── 참석 응답 ───            │
│  ✅ 나  ✅ 영희  ❓ 철수       │
│                              │
│  ─── 반응 ───                │
│  👍 3   ❤️ 5   🎉 2          │  ← 이모지 반응 집계
│                              │
│  ─── 댓글 (6) ───            │
│ ┌────────────────────────────┐│
│ │ 🟢 영희  2/10 14:30         ││
│ │ 선물 뭐 사가면 좋을까?       ││
│ │   ↳ 나: 케이크가 좋을듯!     ││  ← 답글
│ └────────────────────────────┘│
│ ┌────────────────────────────┐│
│ │ 🔵 철수  2/10 15:00         ││
│ │ 🎂🎂🎂                     ││  ← 이모지 댓글
│ └────────────────────────────┘│
├──────────────────────────────┤
│  💬 댓글을 입력하세요...    📎 📤│  ← 댓글 입력
└──────────────────────────────┘
```

#### (3) 일정 생성
```
┌──────────────────────────────┐
│  ✕  일정 만들기           저장  │
├──────────────────────────────┤
│                              │
│  그룹:  [👫 우리 커플 ▼]      │  ← 그룹 선택
│                              │
│  제목:  [데이트           ]    │
│                              │
│  카테고리: 일반 ○ 기념일 ○     │
│           생일 ○ 모임  ●      │
│           여행 ○ 기타  ○      │
│                              │
│  시작:  2026.02.14  14:00     │
│  종료:  2026.02.14  18:00     │
│  □ 종일                      │
│                              │
│  반복:  [반복 안 함 ▼]        │
│                              │
│  장소:  [강남역 2번 출구    ]   │
│                              │
│  설명:  [발렌타인 데이트    ]   │
│                              │
│  🔔 알림: 1시간 전            │
│                              │
│  🎨 색상: 🔴🔵🟢🟡🟣 (선택)   │
│                              │
└──────────────────────────────┘
```

#### (4) 그룹 관리
```
┌──────────────────────────────┐
│  ←  내 그룹                 ➕ │
├──────────────────────────────┤
│ ┌────────────────────────────┐│
│ │ 👫 우리 커플       멤버 2명  ││
│ │    COUPLE · 다음 일정: 2/14 ││
│ └────────────────────────────┘│
│ ┌────────────────────────────┐│
│ │ 👨‍👩‍👧‍👦 우리 가족     멤버 4명  ││
│ │    FAMILY · 다음 일정: 2/20 ││
│ └────────────────────────────┘│
│ ┌────────────────────────────┐│
│ │ 🍺 대학 동기     멤버 8명   ││
│ │    FRIEND · 다음 일정: 3/1  ││
│ └────────────────────────────┘│
├──────────────────────────────┤
│  초대 코드로 참여하기          │
│  [코드 입력          ] [참여]  │
└──────────────────────────────┘
```

#### (5) 일정 투표 (Phase 2)
```
┌──────────────────────────────┐
│  ←  날짜 투표                │
├──────────────────────────────┤
│                              │
│  📊 3월 정기 모임 날짜 투표    │
│  마감: 2026.02.28            │
│                              │
│ ┌────────────────────────────┐│
│ │ 3/1 (토) ████████ 5표  ✅  ││  ← 내가 투표한 항목
│ │ 3/7 (금) ████     3표      ││
│ │ 3/8 (토) ██████   4표  ✅  ││
│ │ 3/15(토) ██       2표      ││
│ └────────────────────────────┘│
│                              │
│  [확정하기] (OWNER/ADMIN만)   │
│                              │
│  ─── 투표 현황 ───            │
│  ✅ 영희: 3/1, 3/8            │
│  ✅ 철수: 3/1, 3/7            │
│  ❌ 민수: 미투표               │
│                              │
└──────────────────────────────┘
```

---

## 5. 데이터베이스 설계

### 5.1 ERD 개요

```
┌──────────┐       ┌──────────────┐       ┌──────────┐
│  Member  │──1:N──│ GroupMember   │──N:1──│  Group   │
└──────────┘       └──────────────┘       └──────────┘
     │                                         │
     │ 1:N                                1:N  │
     ▼                                         ▼
┌──────────┐                             ┌──────────┐
│  Notif.  │       ┌──────────────┐      │  Event   │
└──────────┘       │EventAttendee │──N:1─┤          │
                   └──────────────┘      │          │
┌──────────┐       ┌──────────────┐      │          │
│ RefToken │──N:1──│   Member     │      │          │
└──────────┘       └──────────────┘      └──────────┘
                                           │     │
                                      1:N  │     │ 0..1
                                           ▼     ▼
                                   ┌────────┐ ┌───────────────┐
                                   │Comment │ │RecurrenceRule │
                                   └────────┘ └───────────────┘
                                      │
                                 0..N  │
                                      ▼
                                   ┌────────┐
                                   │Reply   │
                                   └────────┘
```

### 5.2 테이블 상세

#### (1) member — 회원
```sql
CREATE TABLE member (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,            -- BCrypt 해시
    nickname     VARCHAR(50)  NOT NULL,
    profile_image_url VARCHAR(500),
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / INACTIVE / DELETED
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,

    INDEX idx_member_email (email),
    INDEX idx_member_status (status)
);
```

#### (2) calendar_group — 그룹(캘린더)
```sql
CREATE TABLE calendar_group (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL,          -- COUPLE / FRIEND / FAMILY / CUSTOM
    description     VARCHAR(500),
    cover_image_url VARCHAR(500),
    invite_code     VARCHAR(10)  UNIQUE,            -- 6자리 영숫자, 초대 시 생성
    invite_code_expires_at DATETIME(6),             -- 초대 코드 만료 시각
    max_members     INT          NOT NULL DEFAULT 50,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,

    INDEX idx_group_invite_code (invite_code)
);
```

#### (3) group_member — 그룹 멤버
```sql
CREATE TABLE group_member (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    group_id     BIGINT      NOT NULL,
    member_id    BIGINT      NOT NULL,
    role         VARCHAR(20) NOT NULL DEFAULT 'MEMBER',  -- OWNER / ADMIN / MEMBER
    display_name VARCHAR(50),                             -- 그룹 내 표시명 (null이면 nickname)
    color        VARCHAR(7),                              -- 그룹 내 개인 색상 (#FF6B6B)
    joined_at    DATETIME(6) NOT NULL,

    UNIQUE KEY uk_group_member (group_id, member_id),
    FOREIGN KEY (group_id)  REFERENCES calendar_group(id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_gm_member (member_id)
);
```

#### (4) event — 일정
```sql
CREATE TABLE event (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id     BIGINT       NOT NULL,
    creator_id   BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    start_at     DATETIME(6)  NOT NULL,
    end_at       DATETIME(6)  NOT NULL,
    all_day      BOOLEAN      NOT NULL DEFAULT FALSE,
    location     VARCHAR(300),
    color        VARCHAR(7),                             -- 커스텀 색상 (#4A90D9)
    category     VARCHAR(20)  NOT NULL DEFAULT 'GENERAL', -- GENERAL/ANNIVERSARY/BIRTHDAY/MEETING/TRAVEL/ETC
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / CANCELLED
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,

    FOREIGN KEY (group_id)   REFERENCES calendar_group(id),
    FOREIGN KEY (creator_id) REFERENCES member(id),
    INDEX idx_event_group_date (group_id, start_at, end_at),
    INDEX idx_event_creator (creator_id)
);
```

#### (5) recurrence_rule — 반복 규칙
```sql
CREATE TABLE recurrence_rule (
    id            BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id      BIGINT      NOT NULL UNIQUE,
    frequency     VARCHAR(20) NOT NULL,              -- DAILY / WEEKLY / MONTHLY / YEARLY
    interval_val  INT         NOT NULL DEFAULT 1,    -- 1=매번, 2=격주 등
    days_of_week  VARCHAR(20),                       -- WEEKLY용: "MON,WED,FRI"
    day_of_month  INT,                               -- MONTHLY용: 15 (매월 15일)
    end_date      DATE,                              -- 반복 종료일 (nullable)
    count         INT,                               -- 반복 횟수 (nullable, end_date와 택1)

    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);
```

#### (6) event_attendee — 일정 참석 응답
```sql
CREATE TABLE event_attendee (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    member_id    BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / ACCEPTED / DECLINED / MAYBE
    responded_at DATETIME(6),

    UNIQUE KEY uk_event_attendee (event_id, member_id),
    FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_ea_member (member_id)
);
```

#### (7) event_comment — 일정 댓글
```sql
CREATE TABLE event_comment (
    id         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id   BIGINT      NOT NULL,
    member_id  BIGINT      NOT NULL,
    content    VARCHAR(500) NOT NULL,
    type       VARCHAR(10) NOT NULL DEFAULT 'TEXT',   -- TEXT / EMOJI
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_comment_event (event_id, created_at)
);
```

#### (8) comment_reply — 댓글 답글 (1depth)
```sql
CREATE TABLE comment_reply (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT       NOT NULL,
    member_id  BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,

    FOREIGN KEY (comment_id) REFERENCES event_comment(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id)  REFERENCES member(id),
    INDEX idx_reply_comment (comment_id, created_at)
);
```

#### (9) event_reaction — 이모지 반응 (댓글과 별도)
```sql
CREATE TABLE event_reaction (
    id        BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id  BIGINT      NOT NULL,
    member_id BIGINT      NOT NULL,
    emoji     VARCHAR(10) NOT NULL,                  -- 👍❤️😂🎉😢😮

    UNIQUE KEY uk_event_reaction (event_id, member_id, emoji), -- 같은 이모지 중복 방지
    FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_reaction_event (event_id)
);
```

#### (10) event_reminder — 일정 알림 설정
```sql
CREATE TABLE event_reminder (
    id                    BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id              BIGINT      NOT NULL,
    remind_before_minutes INT         NOT NULL,       -- 10, 30, 60, 1440(1일)
    type                  VARCHAR(20) NOT NULL DEFAULT 'PUSH', -- PUSH / IN_APP

    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    INDEX idx_reminder_event (event_id)
);
```

#### (11) notification — 알림
```sql
CREATE TABLE notification (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT       NOT NULL,
    type           VARCHAR(30)  NOT NULL,            -- EVENT_CREATED / EVENT_UPDATED / EVENT_CANCELLED
                                                     -- COMMENT_ADDED / REACTION_ADDED
                                                     -- GROUP_INVITED / MEMBER_JOINED
                                                     -- REMINDER / VOTE_CREATED
    reference_type VARCHAR(20)  NOT NULL,            -- EVENT / GROUP / COMMENT
    reference_id   BIGINT       NOT NULL,
    title          VARCHAR(200) NOT NULL,
    message        VARCHAR(500) NOT NULL,
    is_read        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     DATETIME(6)  NOT NULL,

    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_notification_member (member_id, is_read, created_at DESC)
);
```

#### (12) refresh_token — 리프레시 토큰
```sql
CREATE TABLE refresh_token (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT       NOT NULL,
    token      VARCHAR(500) NOT NULL UNIQUE,
    expires_at DATETIME(6)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,

    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_rt_member (member_id),
    INDEX idx_rt_token (token)
);
```

### 5.3 Phase 2 추가 테이블

#### (13) event_vote — 일정 투표 (날짜 조율)
```sql
CREATE TABLE event_vote (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    creator_id  BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    deadline    DATETIME(6)  NOT NULL,
    is_anonymous BOOLEAN     NOT NULL DEFAULT FALSE,
    is_closed   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6)  NOT NULL,

    FOREIGN KEY (group_id)   REFERENCES calendar_group(id),
    FOREIGN KEY (creator_id) REFERENCES member(id),
    INDEX idx_vote_group (group_id)
);
```

#### (14) vote_option — 투표 선택지
```sql
CREATE TABLE vote_option (
    id         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    vote_id    BIGINT      NOT NULL,
    date       DATE        NOT NULL,
    start_time TIME,                                 -- 시간대 지정 시
    end_time   TIME,

    FOREIGN KEY (vote_id) REFERENCES event_vote(id) ON DELETE CASCADE,
    INDEX idx_vo_vote (vote_id)
);
```

#### (15) vote_response — 투표 응답
```sql
CREATE TABLE vote_response (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,

    UNIQUE KEY uk_vote_response (option_id, member_id),
    FOREIGN KEY (option_id) REFERENCES vote_option(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(id)
);
```

#### (16) event_attachment — 첨부 파일
```sql
CREATE TABLE event_attachment (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT       NOT NULL,
    uploader_id  BIGINT       NOT NULL,
    file_url     VARCHAR(500) NOT NULL,
    file_name    VARCHAR(200) NOT NULL,
    file_size    BIGINT       NOT NULL,              -- bytes
    content_type VARCHAR(100) NOT NULL,              -- image/jpeg, application/pdf
    created_at   DATETIME(6)  NOT NULL,

    FOREIGN KEY (event_id)    REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES member(id),
    INDEX idx_attachment_event (event_id)
);
```

---

## 6. 도메인 모델 (DDD Bounded Context)

### 6.1 Bounded Context 식별

```
┌─────────────────────────────────────────────────────────────┐
│                    Calendar Platform                         │
│                                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│  │  Auth     │  │  Group   │  │ Schedule │  │  Social   │  │
│  │ Context  │  │ Context  │  │ Context  │  │  Context  │  │
│  │          │  │          │  │          │  │           │  │
│  │ - 회원    │  │ - 그룹    │  │ - 일정    │  │ - 댓글     │  │
│  │ - 인증    │  │ - 멤버십  │  │ - 반복    │  │ - 반응     │  │
│  │ - 토큰    │  │ - 초대    │  │ - 참석    │  │ - 알림     │  │
│  │          │  │ - 역할    │  │ - 리마인더 │  │           │  │
│  └──────────┘  └──────────┘  └──────────┘  └───────────┘  │
│                                                             │
│                         Phase 2                             │
│  ┌──────────┐  ┌──────────┐                                │
│  │  Vote    │  │  Media   │                                │
│  │ Context  │  │ Context  │                                │
│  │          │  │          │                                │
│  │ - 투표    │  │ - 첨부    │                                │
│  │ - 선택지  │  │ - 파일    │                                │
│  │ - 응답    │  │          │                                │
│  └──────────┘  └──────────┘                                │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 핵심 Aggregate

| Aggregate Root | 엔티티 | Value Object |
|---------------|--------|-------------|
| **Member** | Member | Email, Password, Nickname |
| **CalendarGroup** | CalendarGroup, GroupMember | InviteCode, GroupType, MemberRole |
| **Event** | Event, RecurrenceRule, EventAttendee, EventReminder | DateTimeRange, Category, Location |
| **EventComment** | EventComment, CommentReply | CommentType |
| **EventReaction** | EventReaction | Emoji |
| **Notification** | Notification | NotificationType |

---

## 7. API 엔드포인트 설계 (요약)

### Auth (인증)
| Method | Path | 설명 |
|--------|------|------|
| POST | `/auth/signup` | 회원가입 |
| POST | `/auth/login` | 로그인 (JWT 발급) |
| POST | `/auth/refresh` | 토큰 갱신 |
| POST | `/auth/logout` | 로그아웃 (Refresh Token 무효화) |

### Members (회원)
| Method | Path | 설명 |
|--------|------|------|
| GET | `/members/me` | 내 정보 조회 |
| PATCH | `/members/me` | 내 정보 수정 |
| DELETE | `/members/me` | 회원 탈퇴 |

### Groups (그룹)
| Method | Path | 설명 |
|--------|------|------|
| POST | `/groups` | 그룹 생성 |
| GET | `/groups` | 내 그룹 목록 |
| GET | `/groups/{id}` | 그룹 상세 |
| PATCH | `/groups/{id}` | 그룹 수정 |
| DELETE | `/groups/{id}` | 그룹 삭제 |
| POST | `/groups/{id}/invite-code` | 초대 코드 생성 |
| POST | `/groups/join` | 초대 코드로 참여 |
| GET | `/groups/{id}/members` | 멤버 목록 |
| PATCH | `/groups/{id}/members/{memberId}` | 멤버 역할/표시명 변경 |
| DELETE | `/groups/{id}/members/{memberId}` | 멤버 내보내기 |
| DELETE | `/groups/{id}/members/me` | 그룹 나가기 |

### Events (일정)
| Method | Path | 설명 |
|--------|------|------|
| POST | `/groups/{groupId}/events` | 일정 생성 |
| GET | `/groups/{groupId}/events` | 일정 목록 (기간 필터) |
| GET | `/events/{id}` | 일정 상세 |
| PATCH | `/events/{id}` | 일정 수정 |
| DELETE | `/events/{id}` | 일정 삭제 |
| POST | `/events/{id}/attendees` | 참석 응답 |
| GET | `/events/me` | 내 전체 일정 (그룹 통합) |

### Comments & Reactions (댓글/반응)
| Method | Path | 설명 |
|--------|------|------|
| POST | `/events/{eventId}/comments` | 댓글 작성 |
| GET | `/events/{eventId}/comments` | 댓글 목록 |
| PATCH | `/comments/{id}` | 댓글 수정 |
| DELETE | `/comments/{id}` | 댓글 삭제 |
| POST | `/comments/{id}/replies` | 답글 작성 |
| POST | `/events/{eventId}/reactions` | 반응 추가/제거 (토글) |
| GET | `/events/{eventId}/reactions` | 반응 목록 |

### Notifications (알림)
| Method | Path | 설명 |
|--------|------|------|
| GET | `/notifications` | 내 알림 목록 |
| PATCH | `/notifications/{id}/read` | 알림 읽음 처리 |
| PATCH | `/notifications/read-all` | 전체 읽음 처리 |
| GET | `/notifications/unread-count` | 안 읽은 알림 수 |

---

## 8. 구현 우선순위 (로드맵)

### Phase 1 — MVP (핵심 기능)
```
Sprint 1: Auth + Member
  ├── 회원가입 / 로그인 / JWT
  └── 프로필 관리

Sprint 2: Group
  ├── 그룹 CRUD
  ├── 초대 코드 생성 / 참여
  └── 멤버 관리 (역할, 표시명, 색상)

Sprint 3: Event (Core)
  ├── 일정 CRUD
  ├── 종일 일정
  ├── 카테고리 · 색상
  └── 반복 일정

Sprint 4: Social (댓글/반응)
  ├── 텍스트 댓글 + 답글
  ├── 이모지 반응
  └── 참석 응답

Sprint 5: Notification + Polish
  ├── 인앱 알림
  ├── 일정 리마인더
  └── 캘린더 뷰 (월간/주간/일간)
```

### Phase 2 — 확장 기능
```
Sprint 6: Vote (투표)
Sprint 7: Media (사진/파일 첨부)
Sprint 8: Social Login (Google, Kakao)
Sprint 9: Push Notification (FCM)
Sprint 10: 위젯 + 날씨 연동
```

---

## 9. 기술 아키텍처 요약

```
┌──────────────────────────────────────────────┐
│                  Client                       │
│  ┌──────────┐  ┌──────────┐  ┌────────────┐ │
│  │  Web     │  │ WebView  │  │  Flutter   │ │
│  │ (React) │  │ (React) │  │  (Native) │ │
│  └────┬─────┘  └────┬─────┘  └─────┬──────┘ │
└───────┼──────────────┼──────────────┼─────────┘
        │              │              │
        ▼              ▼              ▼
┌──────────────────────────────────────────────┐
│              API Gateway (Future)            │
├──────────────────────────────────────────────┤
│           Spring Boot Backend                │
│  ┌──────────────────────────────────────┐    │
│  │  interfaces (REST Controller)        │    │
│  ├──────────────────────────────────────┤    │
│  │  application (UseCase / Service)     │    │
│  ├──────────────────────────────────────┤    │
│  │  domain (Entity / Repository / VO)   │    │
│  ├──────────────────────────────────────┤    │
│  │  infrastructure (JPA / Config)       │    │
│  └──────────────────────────────────────┘    │
├──────────────────────────────────────────────┤
│  MySQL          Redis(Session/Cache)  S3     │
│  (Primary DB)   (Future)             (Files) │
└──────────────────────────────────────────────┘
```
