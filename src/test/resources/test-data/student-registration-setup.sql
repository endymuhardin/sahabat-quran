-- =======================================================
-- STUDENT REGISTRATION TEST DATA SETUP
-- This script sets up clean test data for StudentRegistration tests
-- =======================================================

-- Clean existing test data first (only registrations, keep programs for reuse)
DELETE FROM student_session_preferences WHERE id_registration IN (
    SELECT id FROM student_registrations WHERE full_name LIKE 'Test%' OR email LIKE 'test%@%'
);
DELETE FROM student_registrations WHERE full_name LIKE 'Test%' OR email LIKE 'test%@%';

-- Insert test program (avoid conflict with existing data) - use ON CONFLICT DO NOTHING
INSERT INTO programs (id, code, name, description, level_order, is_active, created_at, updated_at) VALUES
('c0000000-0000-0000-0000-000000000001', 'TEST_PROGRAM', 'Test Program', 'Test program for unit tests', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Sample test registrations - Create multiple for testing pagination and filtering
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    placement_test_status, registration_status, submitted_at, created_at, updated_at
) VALUES (
    'c0000000-0000-0000-0000-000000000002',
    'Test Student',
    'MALE',
    '1990-01-01',
    'Jakarta',
    '081234567890',
    'test@example.com',
    'Test Address',
    'Test Emergency Contact',
    '081234567800',
    'Parent',
    'S1',
    'Test University',
    'Basic reading ability',
    false,
    'c0000000-0000-0000-0000-000000000001',
    'Want to improve Quran reading',
    'Master tajweed rules',
    '[{"days": ["MONDAY", "WEDNESDAY"], "priority": 1, "sessionId": "test-session-id"}]',
    'PENDING',
    'SUBMITTED',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Add another SUBMITTED registration for test count expectations
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    placement_test_status, registration_status, submitted_at, created_at, updated_at
) VALUES (
    'c0000000-0000-0000-0000-000000000003',
    'Test Student Two',
    'FEMALE',
    '1992-05-15',
    'Bandung',
    '081234567891',
    'test2@example.com',
    'Test Address 2',
    'Test Emergency Contact 2',
    '081234567801',
    'Sibling',
    'S2',
    'Test University 2',
    'Intermediate reading ability',
    true,
    'c0000000-0000-0000-0000-000000000001',
    'Improve tajweed and fluency',
    'Perfect Quran recitation',
    '[{"days": ["TUESDAY", "THURSDAY"], "priority": 1, "sessionId": "test-session-id2"}]',
    'PENDING',
    'SUBMITTED',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);