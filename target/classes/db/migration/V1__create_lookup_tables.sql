-- V1: Core lookup tables
CREATE TABLE roles (
    role_id         BIGSERIAL PRIMARY KEY,
    role_name       VARCHAR(50)  NOT NULL UNIQUE,
    role_description VARCHAR(255)
);

CREATE TABLE positions (
    job_id          BIGSERIAL PRIMARY KEY,
    job_code        VARCHAR(20)  NOT NULL UNIQUE,
    job_name        VARCHAR(100) NOT NULL,
    level           VARCHAR(50),
    job_description VARCHAR(500)
);

CREATE TABLE ot_types (
    type_id     BIGSERIAL PRIMARY KEY,
    type_name   VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    ot_rate     NUMERIC(5,2)
);

CREATE TABLE leave_types (
    leave_type_id     BIGSERIAL PRIMARY KEY,
    type_code         VARCHAR(30)  NOT NULL UNIQUE,
    type_name         VARCHAR(100) NOT NULL,
    max_days_per_year NUMERIC(5,1),
    is_paid           BOOLEAN  NOT NULL DEFAULT TRUE,
    requires_approval BOOLEAN  NOT NULL DEFAULT TRUE,
    description       VARCHAR(500),
    is_active         BOOLEAN  NOT NULL DEFAULT TRUE
);

INSERT INTO roles (role_name, role_description) VALUES
    ('ADMIN',   'System administrator with full access'),
    ('MANAGER', 'Department manager'),
    ('STAFF',   'HR or office staff'),
    ('USER',    'Regular employee');

INSERT INTO leave_types (type_code, type_name, max_days_per_year, is_paid, requires_approval) VALUES
    ('ANNUAL',       'Annual Leave',     12.0, TRUE,  TRUE),
    ('SICK',         'Sick Leave',       10.0, TRUE,  FALSE),
    ('UNPAID',       'Unpaid Leave',     NULL, FALSE, TRUE),
    ('COMPENSATORY', 'Compensatory Leave', NULL, TRUE, TRUE),
    ('BEREAVEMENT',  'Bereavement Leave',  3.0, TRUE, TRUE);

INSERT INTO ot_types (type_name, description, ot_rate) VALUES
    ('Weekday OT',  'Overtime on a normal working day',     1.5),
    ('Weekend OT',  'Overtime on Saturday or Sunday',       2.0),
    ('Holiday OT',  'Overtime on a public holiday',         3.0);
