-- =======================================================
-- SESSION TEST DATA SETUP
-- This script sets up clean test data for Session tests
-- =======================================================

-- Clean existing test data first
DELETE FROM sessions WHERE code LIKE 'TEST_%';

-- Insert test sessions (avoid conflict with migration data)
INSERT INTO sessions (id, code, name, start_time, end_time, is_active, created_at, updated_at) VALUES
('d0000000-0000-0000-0000-000000000001', 'TEST_SESSION_1', 'Test Session 1', '08:00:00', '10:00:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('d0000000-0000-0000-0000-000000000002', 'TEST_SESSION_2', 'Test Session 2', '10:00:00', '12:00:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('d0000000-0000-0000-0000-000000000003', 'TEST_SESSION_3', 'Test Session 3', '14:00:00', '16:00:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);