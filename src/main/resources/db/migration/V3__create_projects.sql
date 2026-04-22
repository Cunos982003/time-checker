-- V3: Projects and assignments
CREATE TABLE projects (
    project_id   BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(30)  NOT NULL UNIQUE,
    project_name VARCHAR(200) NOT NULL,
    description  TEXT,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    start_date   DATE,
    end_date     DATE,
    manager_id   BIGINT REFERENCES employees(user_id),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE project_assignments (
    assignment_id   BIGSERIAL PRIMARY KEY,
    project_id      BIGINT NOT NULL REFERENCES projects(project_id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES employees(user_id)   ON DELETE CASCADE,
    assigned_date   DATE   NOT NULL DEFAULT CURRENT_DATE,
    role_in_project VARCHAR(100),
    UNIQUE (project_id, user_id)
);

CREATE INDEX idx_proj_status    ON projects(status);
CREATE INDEX idx_assign_project ON project_assignments(project_id);
CREATE INDEX idx_assign_user    ON project_assignments(user_id);
