-- V7: Leave balances and requests
CREATE TABLE leave_balances (
    balance_id     BIGSERIAL PRIMARY KEY,
    user_id        BIGINT  NOT NULL REFERENCES employees(user_id),
    leave_type_id  BIGINT  NOT NULL REFERENCES leave_types(leave_type_id),
    year           INTEGER NOT NULL,
    entitled_days  NUMERIC(5,1) NOT NULL DEFAULT 0,
    used_days      NUMERIC(5,1) NOT NULL DEFAULT 0,
    pending_days   NUMERIC(5,1) NOT NULL DEFAULT 0,
    adjusted_days  NUMERIC(5,1) NOT NULL DEFAULT 0,
    UNIQUE (user_id, leave_type_id, year)
);

CREATE TABLE leave_requests (
    request_id       BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES employees(user_id),
    leave_type_id    BIGINT NOT NULL REFERENCES leave_types(leave_type_id),
    start_date       DATE   NOT NULL,
    end_date         DATE   NOT NULL,
    total_days       NUMERIC(4,1),
    reason           TEXT,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by      BIGINT REFERENCES employees(user_id),
    rejection_reason TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_leave_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_lr_user   ON leave_requests(user_id);
CREATE INDEX idx_lr_status ON leave_requests(status);
CREATE INDEX idx_lb_user   ON leave_balances(user_id, year);
