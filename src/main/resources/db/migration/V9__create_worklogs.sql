-- V9: Worklogs
CREATE TABLE worklogs (
    worklog_id  BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES employees(user_id),
    project_id  BIGINT REFERENCES projects(project_id),
    work_date   DATE   NOT NULL,
    start_time  TIME,
    end_time    TIME,
    work_hours  NUMERIC(4,2),
    content     TEXT   NOT NULL,
    work_type   VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE worklog_confirmations (
    confirmation_id BIGSERIAL PRIMARY KEY,
    worklog_id      BIGINT NOT NULL UNIQUE REFERENCES worklogs(worklog_id) ON DELETE CASCADE,
    confirmed_by    BIGINT NOT NULL REFERENCES employees(user_id),
    note            TEXT,
    confirmed_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wl_user    ON worklogs(user_id);
CREATE INDEX idx_wl_project ON worklogs(project_id);
CREATE INDEX idx_wl_status  ON worklogs(status);
CREATE INDEX idx_wl_date    ON worklogs(work_date);
