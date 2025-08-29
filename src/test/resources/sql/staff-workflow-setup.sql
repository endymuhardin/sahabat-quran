-- =====================================================
-- STAFF WORKFLOW TEST DATA SETUP
-- Creates randomized test data for staff assignment workflow tests
-- =====================================================

-- Insert test student registration in SUBMITTED status
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status
) VALUES (
    'a1234567-1111-2222-3333-000000000001'::uuid,
    CONCAT('TEST_ST_Ahmad_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1995-03-15',
    'Jakarta Test',
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('ahmad.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Test No. ', floor(random()*999 + 1)::text, ', Jakarta'),
    CONCAT('TEST_ST_Contact_', substring(md5(random()::text), 1, 8)),
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    'Istri',
    'S1',
    CONCAT('Universitas Test ', substring(md5(random()::text), 1, 8)),
    'Test experience for registration',
    true,
    '80000000-0000-0000-0000-000000000001',
    'Test registration reason for workflow',
    'Test learning goals',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '1 hour' * random(),
    'A0000000-0000-0000-0000-000000000001',
    'PENDING'
);

-- Insert session preferences for the test registration
INSERT INTO student_session_preferences (id, id_registration, id_session, preference_priority, preferred_days)
VALUES (
    gen_random_uuid(),
    'a1234567-1111-2222-3333-000000000001'::uuid,
    '90000000-0000-0000-0000-000000000002',
    1,
    '["MONDAY", "WEDNESDAY"]'
);

-- Create additional SUBMITTED registrations for comprehensive testing
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status
) VALUES 
(
    gen_random_uuid(),
    CONCAT('TEST_ST_Std_1_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1990-01-01'::date + (random() * 365 * 20)::int,
    'Jakarta Test 1',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    CONCAT('student.test.1.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Test 1 No. ', floor(random()*999 + 1)::text),
    'TEST_ST_Cont_1',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    'Ayah',
    'S1',
    'Sekolah Test 1',
    'Test Quran reading experience 1',
    false,
    '80000000-0000-0000-0000-000000000001',
    'Test registration reason 1',
    'Test learning goals 1',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '1 hour',
    'A0000000-0000-0000-0000-000000000001',
    'PENDING'
),
(
    gen_random_uuid(),
    CONCAT('TEST_ST_Std_2_', substring(md5(random()::text), 1, 8)),
    'FEMALE',
    '1992-05-15'::date + (random() * 365 * 20)::int,
    'Jakarta Test 2',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    CONCAT('student.test.2.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Test 2 No. ', floor(random()*999 + 1)::text),
    'TEST_ST_Cont_2',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    'Ibu',
    'S2',
    'Sekolah Test 2',
    'Test Quran reading experience 2',
    true,
    '80000000-0000-0000-0000-000000000002',
    'Test registration reason 2',
    'Test learning goals 2',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '2 hours',
    'A0000000-0000-0000-0000-000000000002',
    'PENDING'
),
(
    gen_random_uuid(),
    CONCAT('TEST_ST_Std_3_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1994-08-20'::date + (random() * 365 * 20)::int,
    'Jakarta Test 3',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    CONCAT('student.test.3.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Test 3 No. ', floor(random()*999 + 1)::text),
    'TEST_ST_Cont_3',
    CONCAT('0812', lpad(floor(random()*100000000)::text, 8, '0')),
    'Saudara',
    'D3',
    'Sekolah Test 3',
    'Test Quran reading experience 3',
    false,
    '80000000-0000-0000-0000-000000000003',
    'Test registration reason 3',
    'Test learning goals 3',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '3 hours',
    'A0000000-0000-0000-0000-000000000003',
    'PENDING'
);