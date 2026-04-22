-- V15: Align schema with ERD
-- 1. Alter projects table
ALTER TABLE projects
    RENAME COLUMN start_date TO start_date_plan;

ALTER TABLE projects
    RENAME COLUMN end_date TO end_date_plan;

ALTER TABLE projects
    RENAME COLUMN manager_id TO leader_id;

ALTER TABLE projects
    ADD COLUMN IF NOT EXISTS start_date_actual DATE,
    ADD COLUMN IF NOT EXISTS end_date_actual   DATE,
    ADD COLUMN IF NOT EXISTS status_delete     BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Alter project_assignments table
ALTER TABLE project_assignments
    RENAME COLUMN assigned_date    TO start_date;

ALTER TABLE project_assignments
    RENAME COLUMN role_in_project  TO role;

ALTER TABLE project_assignments
    ADD COLUMN IF NOT EXISTS end_date DATE,
    ADD COLUMN IF NOT EXISTS status   VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- 3. Alter working_days table
ALTER TABLE working_days
    ADD COLUMN IF NOT EXISTS description VARCHAR(500);

-- 4. Create confirm_ot table (Confirm-OT in ERD)
CREATE TABLE IF NOT EXISTS confirm_ot (
    cf_ot_id    BIGSERIAL PRIMARY KEY,
    cf_date     DATE,
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approve_id  BIGINT REFERENCES employees(user_id),
    ot_id       BIGINT NOT NULL UNIQUE REFERENCES ot_records(ot_id) ON DELETE CASCADE
);

-- 5. Create annual_leave table (Annual Leave in ERD)
CREATE TABLE IF NOT EXISTS annual_leave (
    annual_leave_id     BIGSERIAL PRIMARY KEY,
    annual_leave_number NUMERIC(5,1) NOT NULL DEFAULT 0,
    ct_converted_number NUMERIC(5,1) NOT NULL DEFAULT 0,
    user_id             BIGINT NOT NULL UNIQUE REFERENCES employees(user_id) ON DELETE CASCADE
);

-- 6. Create native_doc table (Native Doc in ERD)
CREATE TABLE IF NOT EXISTS native_doc (
    upload_id   BIGSERIAL PRIMARY KEY,
    upload_date DATE,
    title       VARCHAR(300),
    upload_by   BIGINT REFERENCES employees(user_id)
);

-- 7. Create tags table
CREATE TABLE IF NOT EXISTS tags (
    tag_id          BIGSERIAL PRIMARY KEY,
    tag_name        VARCHAR(100) NOT NULL UNIQUE,
    tag_description VARCHAR(500)
);

-- 8. Create confirm_leave_request table (Confirm Leave Request in ERD)
CREATE TABLE IF NOT EXISTS confirm_leave_request (
    cf_request_id   BIGSERIAL PRIMARY KEY,
    confirm_result  TEXT,
    approve_id      BIGINT REFERENCES employees(user_id),
    request_id      BIGINT NOT NULL UNIQUE REFERENCES leave_requests(request_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_confirm_ot_status      ON confirm_ot(status);
CREATE INDEX IF NOT EXISTS idx_annual_leave_user       ON annual_leave(user_id);
CREATE INDEX IF NOT EXISTS idx_native_doc_uploader     ON native_doc(upload_by);
CREATE INDEX IF NOT EXISTS idx_confirm_leave_request   ON confirm_leave_request(request_id);
