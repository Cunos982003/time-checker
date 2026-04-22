-- V4: Working day configuration
CREATE TABLE working_days (
    day           DATE        PRIMARY KEY,
    checkin_time  TIME,
    checkout_time TIME,
    ot_rate       NUMERIC(5,2)
);

-- Seed standard working schedule for current year
INSERT INTO working_days (day, checkin_time, checkout_time, ot_rate)
SELECT
    d::DATE,
    '08:30'::TIME,
    '17:30'::TIME,
    1.5
FROM generate_series(
    date_trunc('year', CURRENT_DATE),
    date_trunc('year', CURRENT_DATE) + INTERVAL '1 year' - INTERVAL '1 day',
    '1 day'
) d
WHERE EXTRACT(DOW FROM d) BETWEEN 1 AND 5; -- Mon-Fri only
