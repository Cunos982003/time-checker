-- V2: Departments and Employees
CREATE TABLE departments (
    depart_id          BIGSERIAL PRIMARY KEY,
    depart_code        VARCHAR(20)  NOT NULL UNIQUE,
    depart_name        VARCHAR(100) NOT NULL,
    depart_description VARCHAR(500),
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    manager_id         BIGINT
);

CREATE TABLE employees (
    user_id    BIGSERIAL PRIMARY KEY,
    emp_id     VARCHAR(20)  NOT NULL UNIQUE,
    fullname   VARCHAR(150) NOT NULL,
    birthday   DATE,
    sex        VARCHAR(10),
    email      VARCHAR(200) NOT NULL UNIQUE,
    phone      VARCHAR(20),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    role_id    BIGINT REFERENCES roles(role_id),
    job_id     BIGINT REFERENCES positions(job_id),
    depart_id  BIGINT REFERENCES departments(depart_id),
    manager_id BIGINT REFERENCES employees(user_id)
);

ALTER TABLE departments
    ADD CONSTRAINT fk_dept_manager FOREIGN KEY (manager_id) REFERENCES employees(user_id);

CREATE TABLE user_accounts (
    account_id  BIGSERIAL PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    user_id     BIGINT NOT NULL UNIQUE REFERENCES employees(user_id)
);

CREATE TABLE refresh_tokens (
    token_id   BIGSERIAL PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    account_id BIGINT NOT NULL REFERENCES user_accounts(account_id),
    expires_at TIMESTAMPTZ  NOT NULL,
    revoked    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_emp_dept   ON employees(depart_id);
CREATE INDEX idx_emp_manager ON employees(manager_id);
CREATE INDEX idx_emp_role   ON employees(role_id);
CREATE INDEX idx_emp_status ON employees(status);
