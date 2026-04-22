-- V11: Public holidays (used for holiday OT rate and leave calculation)
CREATE TABLE public_holidays (
    holiday_id  BIGSERIAL PRIMARY KEY,
    holiday_date DATE        NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    is_recurring BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Seed common Vietnamese public holidays for current year
INSERT INTO public_holidays (holiday_date, name, is_recurring) VALUES
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '0 days',    'New Year''s Day',          TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '29 days',   'Hung Kings Festival',      TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '29 days',   'Liberation Day',           TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '119 days',  'International Labour Day', TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '246 days',  'National Day',             TRUE);

CREATE INDEX idx_holiday_date ON public_holidays(holiday_date);
