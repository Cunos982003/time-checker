-- V8: OT records
CREATE TABLE ot_records (
    ot_id        BIGSERIAL PRIMARY KEY,
    cf_date      DATE,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    work_hours_ot NUMERIC(5,2),
    reason       TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    confirm_by   BIGINT REFERENCES employees(user_id),
    project_id   BIGINT REFERENCES projects(project_id),
    user_id      BIGINT NOT NULL REFERENCES employees(user_id),
    type_id      BIGINT NOT NULL REFERENCES ot_types(type_id),
    ot_date      DATE   NOT NULL,
    CONSTRAINT chk_ot_time CHECK (end_time > start_time)
);

CREATE INDEX idx_ot_user   ON ot_records(user_id);
CREATE INDEX idx_ot_status ON ot_records(status);
CREATE INDEX idx_ot_date   ON ot_records(ot_date);
