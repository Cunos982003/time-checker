-- V5: Attendance (time entries and checks)
CREATE TABLE time_entries (
    time_entry_id         BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL REFERENCES employees(user_id),
    checkin_date          DATE   NOT NULL,
    checkin_time          TIME,
    checkout_date         DATE,
    checkout_time         TIME,
    number_minutes_late   INTEGER DEFAULT 0,
    number_minutes_quit_early INTEGER DEFAULT 0,
    UNIQUE (user_id, checkin_date)
);

CREATE TABLE attendance_checks (
    check_id      BIGSERIAL PRIMARY KEY,
    a_date        DATE    NOT NULL,
    ct_quit_early INTEGER DEFAULT 0,
    ct_checkin_late INTEGER DEFAULT 0,
    confirm_date  DATE,
    time_entry_id BIGINT  NOT NULL UNIQUE REFERENCES time_entries(time_entry_id) ON DELETE CASCADE
);

CREATE INDEX idx_te_user_date ON time_entries(user_id, checkin_date);
CREATE INDEX idx_te_date      ON time_entries(checkin_date);
