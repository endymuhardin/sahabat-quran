-- =====================================================
-- MANAGEMENT WORKFLOW TEST DATA SETUP
-- Creates specialized test data for management workflow tests
-- =====================================================

-- Insert test teacher users for assignment workflow
INSERT INTO users (username, email, phone_number, full_name, is_active, created_at, updated_at)
VALUES 
(
    'ustadz.ahmad', 
    'ustadz.ahmad@test.com', 
    '081234567891', 
    'TEST Ustadz Ahmad',
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
),
(
    'ustadzah.fatimah', 
    'ustadzah.fatimah@test.com', 
    '081234567892', 
    'TEST Ustadzah Fatimah',
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Insert credentials for test teacher users
INSERT INTO user_credentials (id_user, password_hash)
SELECT u.id, '$2a$10$EIXKQkPv.RgaJ6CVFvpqHOxXz8J6aHLGcXMZ8A8k5ZZo3j6sLGHAG' -- Welcome@YSQ2024
FROM users u 
WHERE u.username IN ('ustadz.ahmad', 'ustadzah.fatimah')
ON CONFLICT (id_user) DO NOTHING;

-- Assign instructor roles to test users
INSERT INTO user_roles (id_user, id_role)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username IN ('ustadz.ahmad', 'ustadzah.fatimah') 
AND r.code = 'INSTRUCTOR'
ON CONFLICT (id_user, id_role) DO NOTHING;

-- Insert unassigned registrations for management assignment workflow
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status
) VALUES 
-- Unassigned Registration 1
(
    'c1234567-1111-2222-3333-000000000001'::uuid,
    CONCAT('TEST_MGW_Unassigned_1_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1990-04-12',
    'Jakarta Workflow Test',
    CONCAT('0821345', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('unassigned1.mgw.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Workflow Test 1 No. ', floor(random()*999 + 1)::text, ', Jakarta'),
    'TEST_MGW_Emergency_1',
    CONCAT('0821345', lpad(floor(random()*10000)::text, 4, '0')),
    'Ayah',
    'S1',
    'Universitas Workflow Test 1',
    'Basic Quran reading for workflow test',
    false,
    '80000000-0000-0000-0000-000000000001',
    'Test workflow assignment reason 1',
    'Test workflow learning goals 1',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '30 minutes',
    'A0000000-0000-0000-0000-000000000001',
    'PENDING'
),
-- Unassigned Registration 2
(
    'c2234567-2222-3333-4444-000000000002'::uuid,
    CONCAT('TEST_MGW_Unassigned_2_', substring(md5(random()::text), 1, 8)),
    'FEMALE',
    '1993-07-25',
    'Bandung Workflow Test',
    CONCAT('0822456', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('unassigned2.mgw.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Workflow Test 2 No. ', floor(random()*999 + 1)::text, ', Bandung'),
    'TEST_MGW_Emergency_2',
    CONCAT('0822456', lpad(floor(random()*10000)::text, 4, '0')),
    'Ibu',
    'S2',
    'Institut Workflow Test 2',
    'Intermediate Quran reading for workflow test',
    true,
    '80000000-0000-0000-0000-000000000002',
    'Test workflow assignment reason 2',
    'Test workflow learning goals 2',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["TUESDAY", "THURSDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '1 hour',
    'A0000000-0000-0000-0000-000000000002',
    'PENDING'
),
-- Already Assigned Registration (for workload analysis)
(
    'c3345678-3333-4444-5555-000000000003'::uuid,
    CONCAT('TEST_MGW_Assigned_1_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1991-11-08',
    'Surabaya Workflow Test',
    CONCAT('0823567', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('assigned1.mgw.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Workflow Test 3 No. ', floor(random()*999 + 1)::text, ', Surabaya'),
    'TEST_MGW_Emergency_3',
    CONCAT('0823567', lpad(floor(random()*10000)::text, 4, '0')),
    'Saudara',
    'D3',
    'Politeknik Workflow Test 3',
    'Advanced Quran reading for workflow test',
    false,
    '80000000-0000-0000-0000-000000000003',
    'Test workflow assignment reason 3',
    'Test workflow learning goals 3',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["FRIDAY", "SATURDAY"]}]',
    'ASSIGNED',
    CURRENT_TIMESTAMP - interval '2 hours',
    'A0000000-0000-0000-0000-000000000003',
    'EVALUATED'
);

-- Insert session preferences for workflow test registrations
INSERT INTO student_session_preferences (id, id_registration, id_time_slot, preference_priority)
VALUES 
-- Reg 1: Monday, Wednesday for SESI_2 (priority 1 and 2)
(gen_random_uuid(), 'c1234567-1111-2222-3333-000000000001'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'MONDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'c1234567-1111-2222-3333-000000000001'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'WEDNESDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 2),
-- Reg 2: Tuesday, Thursday for SESI_2
(gen_random_uuid(), 'c2234567-2222-3333-4444-000000000002'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'TUESDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'c2234567-2222-3333-4444-000000000002'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'THURSDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 2),
-- Reg 3: Friday, Saturday for SESI_2
(gen_random_uuid(), 'c3345678-3333-4444-5555-000000000003'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'FRIDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'c3345678-3333-4444-5555-000000000003'::uuid,
 (SELECT ts.id FROM time_slot ts WHERE ts.day_of_week = 'SATURDAY' AND ts.id_session = '90000000-0000-0000-0000-000000000002'), 2);