-- V11: Public holidays (used for holiday OT rate and leave calculation)
CREATE TABLE public_holidays (
    holiday_id  BIGSERIAL PRIMARY KEY,
    holiday_date DATE        NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    is_recurring BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Seed common Vietnamese public holidays for current year
-- Vietnamese holidays (fixed offsets from Jan 1):
--   Jan  1  (+0)   New Year's Day
--   Apr 18  (+107) Hung Kings Festival (10th day of 3rd lunar month — approximated)
--   Apr 30  (+119) Liberation Day
--   May  1  (+120) International Labour Day
--   Sep  2  (+244) National Day
INSERT INTO public_holidays (holiday_date, name, is_recurring) VALUES
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '0 days',    'New Year''s Day',          TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '107 days',  'Hung Kings Festival',      TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '119 days',  'Liberation Day',           TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '120 days',  'International Labour Day', TRUE),
    (date_trunc('year', CURRENT_DATE)::DATE + INTERVAL '244 days',  'National Day',             TRUE)
ON CONFLICT (holiday_date) DO NOTHING;

CREATE INDEX idx_holiday_date ON public_holidays(holiday_date);
