-- Feedback Campaign Test Data Setup
-- Used for AKH-HP-002, AKH-AP-002, AKH-AP-007 test scenarios

-- Clear existing test data
DELETE FROM feedback_answers WHERE id IN (SELECT id FROM feedback_responses WHERE anonymous_token LIKE 'TEST_%');
DELETE FROM feedback_responses WHERE anonymous_token LIKE 'TEST_%';
DELETE FROM feedback_questions WHERE id_campaign IN (SELECT id FROM feedback_campaigns WHERE campaign_name LIKE 'TEST_%');
DELETE FROM feedback_campaigns WHERE campaign_name LIKE 'TEST_%';

-- Insert test academic term if not exists
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, created_at, updated_at)
SELECT 
    'a1111111-1111-1111-1111-111111111111'::uuid,
    'Test Semester 2024/2025',
    '2024-01-01',
    '2024-06-30',
    'ACTIVE',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM academic_terms WHERE id = 'a1111111-1111-1111-1111-111111111111'::uuid
);

-- Use existing teacher ustadz.ahmad (already exists in system with role)
-- No need to insert - user already exists with credentials and role

-- Use existing student siswa.ali (already exists in system with role)
-- No need to insert - user already exists with credentials and role

-- Create Teacher Evaluation Campaign
INSERT INTO feedback_campaigns (
    id,
    campaign_name,
    campaign_type,
    target_audience,
    id_term,
    start_date,
    end_date,
    is_anonymous,
    is_active,
    min_responses_required,
    current_responses,
    created_at,
    id_created_by
) VALUES (
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'TEST_Teacher Evaluation - Ustadz Ahmad',
    'TEACHER_EVALUATION',
    'STUDENTS',
    'a1111111-1111-1111-1111-111111111111'::uuid,
    CURRENT_DATE - INTERVAL '5 days',
    CURRENT_DATE + INTERVAL '10 days',
    true,
    true,
    5,
    0,
    NOW(),
    (SELECT u.id FROM users u 
     JOIN user_roles ur ON u.id = ur.id_user 
     JOIN roles r ON ur.id_role = r.id 
     WHERE r.code IN ('SYSTEM_ADMINISTRATOR', 'ACADEMIC_ADMIN') LIMIT 1)
);

-- Create Feedback Questions
-- Question 1: Teaching Quality Rating
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    created_at
) VALUES (
    'e5555551-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Bagaimana kualitas pengajaran ustadz?',
    'RATING',
    1,
    true,
    NOW()
);

-- Question 2: Communication Skills Rating
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    created_at
) VALUES (
    'e5555552-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Apakah materi disampaikan dengan jelas?',
    'RATING',
    2,
    true,
    NOW()
);

-- Question 3: Punctuality Yes/No
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    created_at
) VALUES (
    'e5555553-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Apakah ustadz hadir tepat waktu?',
    'YES_NO',
    3,
    true,
    NOW()
);

-- Question 4: Teaching Methods Multiple Choice
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    options,
    created_at
) VALUES (
    'e5555554-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Bagaimana metode pengajaran ustadz?',
    'MULTIPLE_CHOICE',
    4,
    true,
    '["Sangat Baik", "Baik", "Cukup", "Perlu Perbaikan"]',
    NOW()
);

-- Question 5: Positive Feedback Text
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    created_at
) VALUES (
    'e5555555-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Apa yang paling Anda sukai dari pengajaran ustadz?',
    'TEXT',
    5,
    false,
    NOW()
);

-- Question 6: Suggestions Text
INSERT INTO feedback_questions (
    id,
    id_campaign,
    question_text,
    question_type,
    question_number,
    is_required,
    created_at
) VALUES (
    'e5555556-5555-5555-5555-555555555555'::uuid,
    'd4444444-4444-4444-4444-444444444444'::uuid,
    'Saran untuk perbaikan?',
    'TEXT',
    6,
    false,
    NOW()
);

-- Questions 7-12: Additional questions for comprehensive testing
INSERT INTO feedback_questions (id, id_campaign, question_text, question_type, question_number, is_required, created_at)
VALUES
    ('e5555557-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Kemampuan menjawab pertanyaan?', 'RATING', 7, true, NOW()),
    ('e5555558-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Bersikap sabar dan ramah?', 'RATING', 8, true, NOW()),
    ('e5555559-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Siap dengan materi?', 'YES_NO', 9, true, NOW()),
    ('e555555a-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Berlaku adil kepada semua siswa?', 'RATING', 10, true, NOW()),
    ('e555555b-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Suasana kelas kondusif?', 'RATING', 11, false, NOW()),
    ('e555555c-5555-5555-5555-555555555555'::uuid, 'd4444444-4444-4444-4444-444444444444'::uuid, 
     'Komentar tambahan', 'TEXT', 12, false, NOW());

-- Create Facility Assessment Campaign (for testing multiple campaigns)
INSERT INTO feedback_campaigns (
    id,
    campaign_name,
    campaign_type,
    target_audience,
    id_term,
    start_date,
    end_date,
    is_anonymous,
    is_active,
    min_responses_required,
    current_responses,
    created_at,
    id_created_by
) VALUES (
    'd4444445-4444-4444-4444-444444444445'::uuid,
    'TEST_Facility Assessment',
    'FACILITY_ASSESSMENT',
    'BOTH',
    'a1111111-1111-1111-1111-111111111111'::uuid,
    CURRENT_DATE - INTERVAL '3 days',
    CURRENT_DATE + INTERVAL '7 days',
    true,
    true,
    10,
    0,
    NOW(),
    (SELECT u.id FROM users u 
     JOIN user_roles ur ON u.id = ur.id_user 
     JOIN roles r ON ur.id_role = r.id 
     WHERE r.code IN ('SYSTEM_ADMINISTRATOR', 'ACADEMIC_ADMIN') LIMIT 1)
);

-- Add a few questions for facility assessment
INSERT INTO feedback_questions (id, id_campaign, question_text, question_type, question_number, is_required, created_at)
VALUES
    ('f6666661-6666-6666-6666-666666666666'::uuid, 'd4444445-4444-4444-4444-444444444445'::uuid,
     'Kebersihan ruang kelas?', 'RATING', 1, true, NOW()),
    ('f6666662-6666-6666-6666-666666666666'::uuid, 'd4444445-4444-4444-4444-444444444445'::uuid,
     'Kelengkapan fasilitas pembelajaran?', 'RATING', 2, true, NOW()),
    ('f6666663-6666-6666-6666-666666666666'::uuid, 'd4444445-4444-4444-4444-444444444445'::uuid,
     'Saran untuk fasilitas', 'TEXT', 3, false, NOW());

-- Create a completed campaign (for testing history)
INSERT INTO feedback_campaigns (
    id,
    campaign_name,
    campaign_type,
    target_audience,
    id_term,
    start_date,
    end_date,
    is_anonymous,
    is_active,
    min_responses_required,
    current_responses,
    created_at,
    id_created_by
) VALUES (
    'd4444446-4444-4444-4444-444444444446'::uuid,
    'TEST_Previous Teacher Evaluation',
    'TEACHER_EVALUATION',
    'STUDENTS',
    'a1111111-1111-1111-1111-111111111111'::uuid,
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE - INTERVAL '10 days',
    true,
    false,
    5,
    8,
    NOW() - INTERVAL '35 days',
    (SELECT u.id FROM users u 
     JOIN user_roles ur ON u.id = ur.id_user 
     JOIN roles r ON ur.id_role = r.id 
     WHERE r.code IN ('SYSTEM_ADMINISTRATOR', 'ACADEMIC_ADMIN') LIMIT 1)
);

-- Add a sample completed response for history testing
INSERT INTO feedback_responses (
    id,
    id_campaign,
    anonymous_token,
    submission_date,
    is_complete
) VALUES (
    'a7777777-7777-7777-7777-777777777777'::uuid,
    'd4444446-4444-4444-4444-444444444446'::uuid,
    'TEST_COMPLETED_TOKEN_001',
    NOW() - INTERVAL '15 days',
    true
);

-- Note: Permissions are granted through roles in this system.
-- Test users receive permissions through their assigned roles (STUDENT, INSTRUCTOR)

-- Create class group and enrollment for context
INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, capacity, created_at, updated_at)
SELECT
    'a8888888-8888-8888-8888-888888888888'::uuid,
    'TEST_Tahsin 1 - Senin Pagi',
    (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
    'a1111111-1111-1111-1111-111111111111'::uuid,
    (SELECT id FROM users WHERE username = 'ustadz.ahmad'),
    10,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM class_groups WHERE id = 'a8888888-8888-8888-8888-888888888888'::uuid
);

-- Enroll student in class
INSERT INTO enrollments (id, id_student, id_class_group, enrollment_date, status, created_at)
SELECT
    'a9999999-9999-9999-9999-999999999999'::uuid,
    (SELECT id FROM users WHERE username = 'siswa.ali'),
    'a8888888-8888-8888-8888-888888888888'::uuid,
    CURRENT_DATE - INTERVAL '60 days',
    'ACTIVE',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM enrollments WHERE id = 'a9999999-9999-9999-9999-999999999999'::uuid
);

-- Note: Teacher linking is handled through feedback responses and class groups