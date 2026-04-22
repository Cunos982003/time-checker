-- V10: Notifications
CREATE TABLE notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    content         TEXT         NOT NULL,
    created_by      BIGINT NOT NULL REFERENCES employees(user_id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_active ON notifications(is_active, created_at DESC);
