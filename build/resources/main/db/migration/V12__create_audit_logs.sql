-- V12: Audit log for tracking all state-change actions
CREATE TABLE audit_logs (
    log_id       BIGSERIAL PRIMARY KEY,
    actor_id     BIGINT       REFERENCES employees(user_id),
    action       VARCHAR(50)  NOT NULL,
    entity_type  VARCHAR(100) NOT NULL,
    entity_id    BIGINT,
    old_value    TEXT,
    new_value    TEXT,
    ip_address   VARCHAR(45),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_actor      ON audit_logs(actor_id);
CREATE INDEX idx_audit_entity     ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);
