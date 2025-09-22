-- Setup for Teacher Availability Change Request Test
-- This script ensures test data is available for TA-HP-003 scenario

-- Insert test academic term if not exists
INSERT INTO academic_terms (id, term_name, status, start_date, end_date, preparation_deadline, created_at, updated_at)
VALUES ('D0000000-0000-0000-0000-000000000002', 'Test Term 2024-1', 'PLANNING', 
        '2024-09-01', '2024-12-31', '2024-08-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET 
    status = 'PLANNING',
    preparation_deadline = '2024-08-15';

-- Use existing users from seed data:
-- ustadz.ahmad (ID: 20000000-0000-0000-0000-000000000001) - instructor
-- academic.admin1 (ID: 40000000-0000-0000-0000-000000000001) - admin

-- Insert test sessions if not exists
INSERT INTO sessions (id, code, name, start_time, end_time, is_active, created_at, updated_at)
VALUES
    ('D0000000-0000-0000-0000-000000000010', 'PAGI', 'Pagi', '08:00', '10:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('D0000000-0000-0000-0000-000000000011', 'SIANG', 'Siang', '10:00', '12:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('D0000000-0000-0000-0000-000000000012', 'SORE', 'Sore', '16:00', '18:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('D0000000-0000-0000-0000-000000000013', 'MALAM', 'Malam', '19:00', '21:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    start_time = EXCLUDED.start_time,
    end_time = EXCLUDED.end_time,
    is_active = true;

-- Insert time_slots for each day and session combination
INSERT INTO time_slot (id, day_of_week, id_session)
VALUES
    -- Monday slots
    (gen_random_uuid(), 'MONDAY', 'D0000000-0000-0000-0000-000000000010'),
    (gen_random_uuid(), 'MONDAY', 'D0000000-0000-0000-0000-000000000011'),
    (gen_random_uuid(), 'MONDAY', 'D0000000-0000-0000-0000-000000000012'),
    (gen_random_uuid(), 'MONDAY', 'D0000000-0000-0000-0000-000000000013'),
    -- Tuesday slots
    (gen_random_uuid(), 'TUESDAY', 'D0000000-0000-0000-0000-000000000010'),
    (gen_random_uuid(), 'TUESDAY', 'D0000000-0000-0000-0000-000000000011'),
    (gen_random_uuid(), 'TUESDAY', 'D0000000-0000-0000-0000-000000000012'),
    (gen_random_uuid(), 'TUESDAY', 'D0000000-0000-0000-0000-000000000013'),
    -- Wednesday slots
    (gen_random_uuid(), 'WEDNESDAY', 'D0000000-0000-0000-0000-000000000010'),
    (gen_random_uuid(), 'WEDNESDAY', 'D0000000-0000-0000-0000-000000000011'),
    (gen_random_uuid(), 'WEDNESDAY', 'D0000000-0000-0000-0000-000000000012'),
    (gen_random_uuid(), 'WEDNESDAY', 'D0000000-0000-0000-0000-000000000013')
ON CONFLICT (day_of_week, id_session) DO NOTHING;

-- Insert existing teacher availability (prerequisite for change request)
-- This simulates that the teacher has already submitted their availability
DELETE FROM teacher_availability 
WHERE id_teacher = '20000000-0000-0000-0000-000000000001' 
AND id_term = 'D0000000-0000-0000-0000-000000000002';

INSERT INTO teacher_availability (id, id_teacher, id_term, id_time_slot, is_available, max_classes_per_week, preferences, submitted_at, created_at, updated_at)
VALUES 
    -- Monday availability
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'MONDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000010'), true, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'MONDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000011'), true, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'MONDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000012'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'MONDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000013'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Tuesday availability  
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'TUESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000010'), true, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'TUESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000011'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'TUESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000012'), true, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'TUESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000013'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Wednesday availability (initially no availability)
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'WEDNESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000010'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'WEDNESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000011'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'WEDNESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000012'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'WEDNESDAY' AND ts.id_session = 'D0000000-0000-0000-0000-000000000013'), false, 5, 'Prefer morning sessions', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Clear any existing change requests for clean test
DELETE FROM teacher_availability_change_request 
WHERE id_teacher = '20000000-0000-0000-0000-000000000001' 
AND id_term = 'D0000000-0000-0000-0000-000000000002';

-- Set up user roles for testing
-- Ensure instructor has appropriate permissions
INSERT INTO user_roles (id_user, id_role)
SELECT '20000000-0000-0000-0000-000000000001', r.id 
FROM roles r WHERE r.name = 'INSTRUCTOR'
ON CONFLICT DO NOTHING;

-- Ensure admin has appropriate permissions  
INSERT INTO user_roles (id_user, id_role)
SELECT '40000000-0000-0000-0000-000000000001', r.id 
FROM roles r WHERE r.name = 'ACADEMIC_ADMIN'
ON CONFLICT DO NOTHING;