-- =====================================================
-- TEACHER WORKFLOW TEST SETUP
-- Setup test data for Teacher Registration Evaluation Workflow
-- =====================================================

-- Step 1: Create student registrations for teacher evaluation
-- Using proper registration format and ensuring assignment to ustadz.ahmad
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    recording_drive_link, placement_test_status, id_placement_verse,
    registration_status, submitted_at, created_at,
    id_assigned_teacher, assigned_at, id_assigned_by, teacher_review_status
) VALUES
('aa001000-0000-0000-0000-000000000001', 'Fatimah Al-Zahra TEST', 'FEMALE', '2000-01-15', 'Jakarta',
 '081234567890', 'fatimah.teacher.test@example.com', 'Jl. Test 123, Jakarta',
 'Ibu Fatimah', '081234567899', 'IBU',
 'SMA', 'SMA Negeri 1 Jakarta', 'Sudah bisa membaca Quran dasar', true,
 '80000000-0000-0000-0000-000000000001', 'Ingin belajar tahsin', 'Memperbaiki bacaan Quran',
 '[{"sessionId": "90000000-0000-0000-0000-000000000001", "priority": 1, "days": ["SATURDAY", "SUNDAY"]}]',
 'https://drive.google.com/teacher-test-001', 'SUBMITTED', 'A0000000-0000-0000-0000-000000000001',
 'ASSIGNED', NOW() - INTERVAL '1 day', NOW() - INTERVAL '2 days',
 '20000000-0000-0000-0000-000000000001', NOW() - INTERVAL '1 hour', '00000000-0000-0000-0000-000000000001', 'PENDING'),

('aa001000-0000-0000-0000-000000000002', 'Ahmad Naufal TEST', 'MALE', '1998-05-20', 'Bandung',
 '081234567891', 'ahmad.teacher.test@example.com', 'Jl. Test 456, Bandung',
 'Bapak Ahmad', '081234567898', 'AYAH',
 'Sarjana', 'Universitas Padjajaran', 'Menghapal beberapa surat pendek', false,
 '80000000-0000-0000-0000-000000000002', 'Mendalami Quran', 'Tahsin dan Tahfidz',
 '[{"sessionId": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["SATURDAY", "SUNDAY"]}]',
 'https://drive.google.com/teacher-test-002', 'SUBMITTED', 'A0000000-0000-0000-0000-000000000001',
 'ASSIGNED', NOW() - INTERVAL '1 day', NOW() - INTERVAL '2 days',
 '20000000-0000-0000-0000-000000000001', NOW() - INTERVAL '1 hour', '00000000-0000-0000-0000-000000000001', 'PENDING')
ON CONFLICT (id) DO NOTHING;

-- Step 2: Insert session preferences for test registrations
INSERT INTO student_session_preferences (id, id_registration, id_session, preference_priority, preferred_days)
VALUES 
(gen_random_uuid(), 'aa001000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 1, '["SATURDAY", "SUNDAY"]'),
(gen_random_uuid(), 'aa001000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000002', 1, '["SATURDAY", "SUNDAY"]')
ON CONFLICT (id_registration, id_session) DO NOTHING;