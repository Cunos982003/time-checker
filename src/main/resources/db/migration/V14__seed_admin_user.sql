-- V14: Seed default admin user account
-- Password: Admin@123  (BCrypt, cost 10)
INSERT INTO employees (emp_id, fullname, email, status)
VALUES ('EMP001', 'System Administrator', 'admin@timekeeping.com', 'ACTIVE')
ON CONFLICT DO NOTHING;

UPDATE employees e
SET role_id = (SELECT role_id FROM roles WHERE role_name = 'ADMIN' LIMIT 1)
WHERE e.emp_id = 'EMP001' AND e.role_id IS NULL;

INSERT INTO user_accounts (username, password, user_id)
SELECT 'admin',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
       user_id
FROM employees WHERE emp_id = 'EMP001'
ON CONFLICT DO NOTHING;
