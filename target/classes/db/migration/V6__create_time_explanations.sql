-- V6: Time explanations (giải trình chấm công)
CREATE TABLE time_explanations (
    explain_id    BIGSERIAL PRIMARY KEY,
    create_date   DATE   NOT NULL DEFAULT CURRENT_DATE,
    reason        TEXT   NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    picture       VARCHAR(500),
    time_entry_id BIGINT NOT NULL REFERENCES time_entries(time_entry_id) ON DELETE CASCADE
);

CREATE TABLE confirm_explanations (
    cf_explain_id BIGSERIAL PRIMARY KEY,
    note          TEXT,
    cf_date       DATE,
    approval_id   BIGINT REFERENCES employees(user_id),
    explain_id    BIGINT NOT NULL UNIQUE REFERENCES time_explanations(explain_id) ON DELETE CASCADE
);

CREATE INDEX idx_explain_status    ON time_explanations(status);
CREATE INDEX idx_explain_entry     ON time_explanations(time_entry_id);
