-- =====================================================
-- TEACHER WORKFLOW TEST DATA SETUP
-- Creates randomized test data for teacher evaluation workflow tests
-- =====================================================

-- Insert test student registration ASSIGNED to teacher for review
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status,
    assigned_teacher_id, assigned_at, assigned_by_id, teacher_review_status
) VALUES (
    'a2345678-2222-3333-4444-000000000001'::uuid,
    CONCAT('TEST_TC_Student_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1995-03-15',
    'Jakarta Teacher Test',
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('teacher.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Teacher Test No. ', floor(random()*999 + 1)::text, ', Jakarta'),
    CONCAT('TEST_TC_Contact_', substring(md5(random()::text), 1, 8)),
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    'Istri',
    'S1',
    CONCAT('Universitas Teacher Test ', substring(md5(random()::text), 1, 8)),
    'Test experience for teacher evaluation workflow',
    true,
    '80000000-0000-0000-0000-000000000001',
    'Test registration for teacher evaluation',
    'Test learning goals for teacher assessment',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'ASSIGNED',
    CURRENT_TIMESTAMP - interval '2 hours' - interval '1 hour' * random(),
    'A0000000-0000-0000-0000-000000000001',
    'PENDING',
    '20000000-0000-0000-0000-000000000001', -- ustadz.ahmad
    CURRENT_TIMESTAMP - interval '1 hour' - interval '30 minutes' * random(),
    '40000000-0000-0000-0000-000000000001', -- staff.admin1
    'PENDING'
);

-- Insert session preferences for the test registration
INSERT INTO student_session_preferences (id, id_registration, id_session, preference_priority, preferred_days)
VALUES (
    gen_random_uuid(),
    'a2345678-2222-3333-4444-000000000001'::uuid,
    '90000000-0000-0000-0000-000000000002',
    1,
    '["MONDAY", "WEDNESDAY"]'
);

-- Add teacher assignment audit record
INSERT INTO teacher_assignment_audit (registration_id, assigned_teacher_id, assigned_by_id, action, notes)
VALUES (
    'a2345678-2222-3333-4444-000000000001'::uuid,
    '20000000-0000-0000-0000-000000000001', -- ustadz.ahmad
    '40000000-0000-0000-0000-000000000001', -- staff.admin1
    'ASSIGNED',
    'Test assignment for teacher workflow testing'
);

-- Create additional ASSIGNED registrations for comprehensive testing
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status,
    assigned_teacher_id, assigned_at, assigned_by_id, teacher_review_status
) VALUES 
(
    gen_random_uuid(),
    CONCAT('TEST_TC_Std_1_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1990-01-01'::date + (random() * 365 * 20)::int,
    'Jakarta Teacher Test 1',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    CONCAT('teacher.student.1.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Teacher Test 1 No. ', floor(random()*999 + 1)::text),
    'TEST_TC_Cont_1',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    'Ayah',
    'S1',
    'Sekolah Teacher Test 1',
    'Test Quran reading experience for teacher 1',
    false,
    '80000000-0000-0000-0000-000000000001',
    'Test registration reason for teacher 1',
    'Test learning goals for teacher 1',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'ASSIGNED',
    CURRENT_TIMESTAMP - interval '1 hour',
    'A0000000-0000-0000-0000-000000000001',
    'PENDING',
    '20000000-0000-0000-0000-000000000001', -- ustadz.ahmad
    CURRENT_TIMESTAMP - interval '30 minutes',
    '40000000-0000-0000-0000-000000000001', -- staff.admin1
    'PENDING'
),
(
    gen_random_uuid(),
    CONCAT('TEST_TC_Std_2_', substring(md5(random()::text), 1, 8)),
    'FEMALE',
    '1992-05-15'::date + (random() * 365 * 20)::int,
    'Jakarta Teacher Test 2',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    CONCAT('teacher.student.2.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Teacher Test 2 No. ', floor(random()*999 + 1)::text),
    'TEST_TC_Cont_2',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    'Ibu',
    'S2',
    'Sekolah Teacher Test 2',
    'Test Quran reading experience for teacher 2',
    true,
    '80000000-0000-0000-0000-000000000002',
    'Test registration reason for teacher 2',
    'Test learning goals for teacher 2',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'REVIEWED',
    CURRENT_TIMESTAMP - interval '2 hours',
    'A0000000-0000-0000-0000-000000000002',
    'PENDING',
    '20000000-0000-0000-0000-000000000001', -- ustadz.ahmad
    CURRENT_TIMESTAMP - interval '1 hour',
    '40000000-0000-0000-0000-000000000001', -- staff.admin1
    'IN_REVIEW'
);