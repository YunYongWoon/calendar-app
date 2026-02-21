-- ============================================================
-- V1: 초기 스키마 생성 (MVP)
-- ============================================================

-- (1) member
CREATE TABLE member (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    nickname          VARCHAR(50)  NOT NULL,
    profile_image_url VARCHAR(500),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,

    CONSTRAINT uk_member_email UNIQUE (email)
);
CREATE INDEX idx_member_status ON member (status);

-- (2) calendar_group
CREATE TABLE calendar_group (
    id                     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name                   VARCHAR(100) NOT NULL,
    type                   VARCHAR(20)  NOT NULL,
    description            VARCHAR(500),
    cover_image_url        VARCHAR(500),
    invite_code            VARCHAR(10),
    invite_code_expires_at DATETIME(6),
    max_members            INT          NOT NULL DEFAULT 50,
    created_at             DATETIME(6)  NOT NULL,
    updated_at             DATETIME(6)  NOT NULL,

    CONSTRAINT uk_group_invite_code UNIQUE (invite_code)
);

-- (3) group_member
CREATE TABLE group_member (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    group_id     BIGINT      NOT NULL,
    member_id    BIGINT      NOT NULL,
    role         VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    display_name VARCHAR(50),
    color        VARCHAR(7),
    joined_at    DATETIME(6) NOT NULL,

    CONSTRAINT uk_group_member UNIQUE (group_id, member_id),
    CONSTRAINT fk_gm_group  FOREIGN KEY (group_id)  REFERENCES calendar_group(id),
    CONSTRAINT fk_gm_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_gm_member ON group_member (member_id);

-- (4) event
CREATE TABLE event (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    creator_id  BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    start_at    DATETIME(6)  NOT NULL,
    end_at      DATETIME(6)  NOT NULL,
    all_day     BOOLEAN      NOT NULL DEFAULT FALSE,
    location    VARCHAR(300),
    color       VARCHAR(7),
    category    VARCHAR(20)  NOT NULL DEFAULT 'GENERAL',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,

    CONSTRAINT fk_event_group   FOREIGN KEY (group_id)   REFERENCES calendar_group(id),
    CONSTRAINT fk_event_creator FOREIGN KEY (creator_id) REFERENCES member(id)
);
CREATE INDEX idx_event_group_date ON event (group_id, start_at, end_at);
CREATE INDEX idx_event_creator ON event (creator_id);

-- (5) recurrence_rule
CREATE TABLE recurrence_rule (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    frequency    VARCHAR(20) NOT NULL,
    interval_val INT         NOT NULL DEFAULT 1,
    days_of_week VARCHAR(20),
    day_of_month INT,
    end_date     DATE,
    count        INT,

    CONSTRAINT uk_recurrence_event UNIQUE (event_id),
    CONSTRAINT fk_recurrence_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);

-- (6) event_attendee
CREATE TABLE event_attendee (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    member_id    BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    responded_at DATETIME(6),

    CONSTRAINT uk_event_attendee UNIQUE (event_id, member_id),
    CONSTRAINT fk_ea_event  FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_ea_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_ea_member ON event_attendee (member_id);

-- (7) event_comment
CREATE TABLE event_comment (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    event_id   BIGINT       NOT NULL,
    member_id  BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    type       VARCHAR(10)  NOT NULL DEFAULT 'TEXT',
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,

    CONSTRAINT fk_comment_event  FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_comment_event ON event_comment (event_id, created_at);

-- (8) comment_reply
CREATE TABLE comment_reply (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT       NOT NULL,
    member_id  BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,

    CONSTRAINT fk_reply_comment FOREIGN KEY (comment_id) REFERENCES event_comment(id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_member  FOREIGN KEY (member_id)  REFERENCES member(id)
);
CREATE INDEX idx_reply_comment ON comment_reply (comment_id, created_at);

-- (9) event_reaction
CREATE TABLE event_reaction (
    id        BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id  BIGINT      NOT NULL,
    member_id BIGINT      NOT NULL,
    emoji     VARCHAR(10) NOT NULL,

    CONSTRAINT uk_event_reaction UNIQUE (event_id, member_id, emoji),
    CONSTRAINT fk_reaction_event  FOREIGN KEY (event_id)  REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_reaction_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_reaction_event ON event_reaction (event_id);

-- (10) event_reminder
CREATE TABLE event_reminder (
    id                    BIGINT      AUTO_INCREMENT PRIMARY KEY,
    event_id              BIGINT      NOT NULL,
    remind_before_minutes INT         NOT NULL,
    type                  VARCHAR(20) NOT NULL DEFAULT 'PUSH',

    CONSTRAINT fk_reminder_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);
CREATE INDEX idx_reminder_event ON event_reminder (event_id);

-- (11) notification
CREATE TABLE notification (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT       NOT NULL,
    type           VARCHAR(30)  NOT NULL,
    reference_type VARCHAR(20)  NOT NULL,
    reference_id   BIGINT       NOT NULL,
    title          VARCHAR(200) NOT NULL,
    message        VARCHAR(500) NOT NULL,
    is_read        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     DATETIME(6)  NOT NULL,

    CONSTRAINT fk_notification_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_notification_member ON notification (member_id, is_read, created_at DESC);

-- (12) refresh_token
CREATE TABLE refresh_token (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT       NOT NULL,
    token      VARCHAR(500) NOT NULL,
    expires_at DATETIME(6)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,

    CONSTRAINT uk_refresh_token UNIQUE (token),
    CONSTRAINT fk_rt_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_rt_member ON refresh_token (member_id);
