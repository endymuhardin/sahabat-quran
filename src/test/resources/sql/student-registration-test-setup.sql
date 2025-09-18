-- =====================================================
-- STUDENT REGISTRATION TEST DATA SETUP (MANAGEMENT)
-- Creates test data for management registration workflow tests
-- =====================================================

-- Insert test student registrations in various states for management oversight
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at, id_placement_verse, placement_test_status
) VALUES 
-- Registration 1: SUBMITTED - Ready for assignment
(
    'b1234567-1111-2222-3333-000000000001'::uuid,
    CONCAT('TEST_MG_Ahmad_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1995-03-15',
    'Jakarta Test',
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('ahmad.mg.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Management Test No. ', floor(random()*999 + 1)::text, ', Jakarta'),
    CONCAT('TEST_MG_Contact_', substring(md5(random()::text), 1, 8)),
    CONCAT('0812345', lpad(floor(random()*10000)::text, 4, '0')),
    'Istri',
    'S1',
    CONCAT('Universitas Test ', substring(md5(random()::text), 1, 8)),
    'Test Quran reading for management test',
    true,
    '80000000-0000-0000-0000-000000000001',
    'Test registration for management workflow',
    'Test learning goals for management',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - interval '1 hour',
    'A0000000-0000-0000-0000-000000000001',
    'PENDING'
),
-- Registration 2: ASSIGNED - In progress with teacher
(
    'b2234567-2222-3333-4444-000000000002'::uuid,
    CONCAT('TEST_MG_Fatimah_', substring(md5(random()::text), 1, 8)),
    'FEMALE',
    '1990-06-20',
    'Bandung Test',
    CONCAT('0813456', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('fatimah.mg.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Test Bandung No. ', floor(random()*999 + 1)::text, ', Bandung'),
    'TEST_MG_Emergency_Fatimah',
    CONCAT('0813456', lpad(floor(random()*10000)::text, 4, '0')),
    'Suami',
    'S2',
    'Institut Test Bandung',
    'Advanced Quran reading experience',
    false,
    '80000000-0000-0000-0000-000000000002',
    'Test registration reason 2',
    'Advanced learning goals',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["TUESDAY", "THURSDAY"]}]',
    'ASSIGNED',
    CURRENT_TIMESTAMP - interval '2 days',
    'A0000000-0000-0000-0000-000000000002',
    'EVALUATED'
),
-- Registration 3: REVIEWED - Completed by teacher, ready for approval
(
    'b3345678-3333-4444-5555-000000000003'::uuid,
    CONCAT('TEST_MG_Omar_', substring(md5(random()::text), 1, 8)),
    'MALE',
    '1988-12-10',
    'Surabaya Test',
    CONCAT('0814567', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('omar.mg.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Surabaya Test No. ', floor(random()*999 + 1)::text, ', Surabaya'),
    'TEST_MG_Emergency_Omar',
    CONCAT('0814567', lpad(floor(random()*10000)::text, 4, '0')),
    'Saudara',
    'D3',
    'Politeknik Test Surabaya',
    'Basic Quran reading',
    true,
    '80000000-0000-0000-0000-000000000003',
    'Test registration reason 3',
    'Basic learning goals',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["FRIDAY", "SATURDAY"]}]',
    'REVIEWED',
    CURRENT_TIMESTAMP - interval '1 week',
    'A0000000-0000-0000-0000-000000000003',
    'EVALUATED'
),
-- Registration 4: APPROVED - Successfully processed
(
    'b4456789-4444-5555-6666-000000000004'::uuid,
    CONCAT('TEST_MG_Khadijah_', substring(md5(random()::text), 1, 8)),
    'FEMALE',
    '1992-09-05',
    'Medan Test',
    CONCAT('0815678', lpad(floor(random()*10000)::text, 4, '0')),
    CONCAT('khadijah.mg.test.', substring(md5(random()::text), 1, 8), '@email.com'),
    CONCAT('Jl. Medan Test No. ', floor(random()*999 + 1)::text, ', Medan'),
    'TEST_MG_Emergency_Khadijah',
    CONCAT('0815678', lpad(floor(random()*10000)::text, 4, '0')),
    'Ayah',
    'S1',
    'Universitas Test Medan',
    'Intermediate Quran reading',
    false,
    '80000000-0000-0000-0000-000000000001',
    'Test registration reason 4',
    'Intermediate learning goals',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["SUNDAY", "MONDAY"]}]',
    'APPROVED',
    CURRENT_TIMESTAMP - interval '2 weeks',
    'A0000000-0000-0000-0000-000000000004',
    'EVALUATED'
);

-- Insert session preferences for test registrations
-- Update: preferences are now per time slot (day + session)
INSERT INTO student_session_preferences (id, id_registration, id_time_slot, preference_priority)
VALUES 
-- Registration 1: Monday and Wednesday for SESI_2
(gen_random_uuid(), 'b1234567-1111-2222-3333-000000000001'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'MONDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'b1234567-1111-2222-3333-000000000001'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'WEDNESDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 2),
-- Registration 2: Tuesday and Thursday for SESI_2
(gen_random_uuid(), 'b2234567-2222-3333-4444-000000000002'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'TUESDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'b2234567-2222-3333-4444-000000000002'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'THURSDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 2),
-- Registration 3: Friday and Saturday for SESI_2
(gen_random_uuid(), 'b3345678-3333-4444-5555-000000000003'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'FRIDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'b3345678-3333-4444-5555-000000000003'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'SATURDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 2),
-- Registration 4: Sunday and Monday for SESI_2
(gen_random_uuid(), 'b4456789-4444-5555-6666-000000000004'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'SUNDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 1),
(gen_random_uuid(), 'b4456789-4444-5555-6666-000000000004'::uuid,
 (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'MONDAY' AND s.id = '90000000-0000-0000-0000-000000000002'), 2);

-- Create test management user if it doesn't exist (for management workflow tests)
INSERT INTO users (username, email, phone_number, full_name, is_active, created_at, updated_at)
VALUES (
    'management.director', 
    'management.director@test.com', 
    '081234567890', 
    'TEST Management Director',
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Insert credentials for test management user
INSERT INTO user_credentials (id_user, password_hash)
SELECT u.id, '$2a$10$EIXKQkPv.RgaJ6CVFvpqHOxXz8J6aHLGcXMZ8A8k5ZZo3j6sLGHAG' -- Welcome@YSQ2024
FROM users u 
WHERE u.username = 'management.director'
ON CONFLICT (id_user) DO NOTHING;

-- Assign management role to test user
INSERT INTO user_roles (id_user, id_role)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'management.director' 
AND r.code = 'MANAGEMENT'
ON CONFLICT (id_user, id_role) DO NOTHING;