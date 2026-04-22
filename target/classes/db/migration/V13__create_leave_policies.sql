-- V13: Leave policies per department (override global defaults)
CREATE TABLE leave_policies (
    policy_id       BIGSERIAL PRIMARY KEY,
    depart_id       BIGINT       REFERENCES departments(depart_id) ON DELETE CASCADE,
    leave_type_id   BIGINT       NOT NULL REFERENCES leave_types(leave_type_id),
    entitled_days   NUMERIC(5,1) NOT NULL,
    carry_over_days NUMERIC(5,1) NOT NULL DEFAULT 0,
    effective_year  INTEGER      NOT NULL,
    UNIQUE (depart_id, leave_type_id, effective_year)
);

CREATE INDEX idx_policy_dept ON leave_policies(depart_id, effective_year);
