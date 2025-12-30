-- Operation Workflow Test Setup SQL
-- This script sets up test data for Academic Admin operation workflow tests
-- and Cross-Term Analytics tests, including Student Report validation test data

-- Insert additional academic terms for cross-term analytics testing
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline, created_at, updated_at) VALUES
-- Historical terms for testing (using dynamic dates)
('A1000000-0000-0000-0000-000000000001', 'Semester 1 2023/2024', CURRENT_DATE - INTERVAL '365 days', CURRENT_DATE - INTERVAL '230 days', 'COMPLETED', CURRENT_DATE - INTERVAL '372 days', NOW(), NOW()),
('A1000000-0000-0000-0000-000000000002', 'Semester 2 2023/2024', CURRENT_DATE - INTERVAL '229 days', CURRENT_DATE - INTERVAL '80 days', 'COMPLETED', CURRENT_DATE - INTERVAL '235 days', NOW(), NOW()),
('A1000000-0000-0000-0000-000000000003', 'Intensive 2023/2024', CURRENT_DATE - INTERVAL '79 days', CURRENT_DATE - INTERVAL '15 days', 'COMPLETED', CURRENT_DATE - INTERVAL '85 days', NOW(), NOW())
-- Note: Using existing 'Semester 1 2024/2025' from main migration (D0000000-0000-0000-0000-000000000001)
ON CONFLICT (id) DO NOTHING;

-- Insert test substitute teachers (ensure these teachers exist first)
INSERT INTO substitute_teachers (id, id_teacher, is_available, emergency_available, hourly_rate, rating, total_substitutions, contact_preference, notes, created_at, updated_at)
SELECT 
    '550e8400-e29b-41d4-a716-446655440001'::uuid, 
    u.id,
    true, true, 150000.00, 4.5, 10, 'WHATSAPP', 'Experienced substitute teacher', NOW(), NOW()
FROM users u 
WHERE u.id = '20000000-0000-0000-0000-000000000002'::uuid
AND NOT EXISTS (SELECT 1 FROM substitute_teachers st WHERE st.id_teacher = u.id)
UNION ALL
SELECT 
    '550e8400-e29b-41d4-a716-446655440002'::uuid,
    u.id,
    true, false, 175000.00, 4.3, 15, 'SMS', 'Senior teacher', NOW(), NOW()
FROM users u 
WHERE u.id = '20000000-0000-0000-0000-000000000003'::uuid
AND NOT EXISTS (SELECT 1 FROM substitute_teachers st WHERE st.id_teacher = u.id);

-- Insert test class sessions for monitoring (including specific session for emergency test)
-- First ensure we have a class group with proper instructor assignment
INSERT INTO class_groups (id, name, id_level, id_instructor, id_term, capacity, max_students, id_time_slot, location, is_active, created_at, updated_at)
SELECT
    '350e8400-e29b-41d4-a716-446655440001'::uuid,
    'Tahsin Class A - Test',
    (SELECT id FROM levels LIMIT 1),
    '20000000-0000-0000-0000-000000000001'::uuid,
    'D0000000-0000-0000-0000-000000000001'::uuid,
    20,
    20,
    (SELECT id FROM time_slot WHERE day_of_week = 'MONDAY' LIMIT 1),
    'Room A1',
    true,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM class_groups WHERE id = '350e8400-e29b-41d4-a716-446655440001'::uuid);

-- Insert test class sessions for monitoring (including specific session for emergency test)
INSERT INTO class_sessions (id, id_class_group, session_date, session_number, preparation_status, id_instructor, created_at, updated_at)
VALUES
    ('450e8400-e29b-41d4-a716-446655440001'::uuid,
     '350e8400-e29b-41d4-a716-446655440001'::uuid,  -- Use the specific test class group
     CURRENT_DATE,
     1,
     'IN_PROGRESS',  -- IN_PROGRESS status will trigger late session detection
     '20000000-0000-0000-0000-000000000001'::uuid,
     NOW(),
     NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert additional class sessions (only one per class group per date due to unique constraint)
-- Use a different date to avoid unique constraint violations with the main test session
INSERT INTO class_sessions (id, id_class_group, session_date, session_number, preparation_status, id_instructor, created_at, updated_at)
SELECT
    gen_random_uuid(),
    cg.id,
    CURRENT_DATE - INTERVAL '1 day',  -- Use yesterday to avoid unique constraint
    1,
    'READY',
    '20000000-0000-0000-0000-000000000001'::uuid,
    NOW(),
    NOW()
FROM class_groups cg
WHERE cg.id != (SELECT id FROM class_groups LIMIT 1)
AND NOT EXISTS (
    SELECT 1 FROM class_sessions cs
    WHERE cs.id_class_group = cg.id
    AND cs.session_date = CURRENT_DATE - INTERVAL '1 day'
)
LIMIT 2;

-- Insert test feedback campaigns (linked to academic terms for analytics)
INSERT INTO feedback_campaigns (id, campaign_name, campaign_type, target_audience, start_date, end_date, is_active, id_term, created_at, id_created_by)
VALUES
    ('650e8400-e29b-41d4-a716-446655440001'::uuid, 'Teacher Evaluation Q1', 'TEACHER_EVALUATION', 'STUDENTS', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '7 days', true, 'D0000000-0000-0000-0000-000000000001'::uuid, NOW(), '20000000-0000-0000-0000-000000000001'::uuid),
    ('650e8400-e29b-41d4-a716-446655440002'::uuid, 'Facility Assessment', 'FACILITY_ASSESSMENT', 'BOTH', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '14 days', true, 'D0000000-0000-0000-0000-000000000001'::uuid, NOW(), '20000000-0000-0000-0000-000000000001'::uuid),
    -- Additional campaigns for historical terms
    -- Note: S1 2023/2024 (A1000000-...-000000000001) intentionally has no teacher evaluation campaign
    -- to test missing teacher data warning in validation tests
    ('650e8400-e29b-41d4-a716-446655440004'::uuid, 'Teacher Evaluation S2', 'TEACHER_EVALUATION', 'STUDENTS', CURRENT_DATE - INTERVAL '229 days', CURRENT_DATE - INTERVAL '100 days', false, 'A1000000-0000-0000-0000-000000000002'::uuid, NOW(), '20000000-0000-0000-0000-000000000001'::uuid)
ON CONFLICT (id) DO NOTHING;

-- Insert test feedback questions (12 questions for Teacher Evaluation as expected by test)
INSERT INTO feedback_questions (id, id_campaign, question_number, question_text, question_type, is_required, options, created_at)
VALUES
    -- Teacher Evaluation Q1 Campaign (12 questions total)
    ('750e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 1, 'Teaching Quality - Explanation Clarity', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440002'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 2, 'Teaching Quality - Student Engagement', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440003'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 3, 'Teaching Quality - Material Preparation', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440004'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 4, 'Communication - Responsiveness', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440005'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 5, 'Communication - Clarity', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440006'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 6, 'Communication - Helpfulness', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440007'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 7, 'Punctuality - Class Start Time', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440008'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 8, 'Punctuality - Assignment Feedback', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440009'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 9, 'Fairness - Student Treatment', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440010'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 10, 'Fairness - Grading Consistency', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440011'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 11, 'Positive Comments', 'TEXT', false, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440012'::uuid, '650e8400-e29b-41d4-a716-446655440001'::uuid, 12, 'Suggestions for Improvement', 'TEXT', false, NULL, NOW()),
    -- Facility Assessment Campaign
    ('750e8400-e29b-41d4-a716-446655440021'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid, 1, 'Facility Cleanliness', 'RATING', true, NULL, NOW()),
    -- Note: S1 2023/2024 campaign removed to test missing teacher data warning
    -- Historical Teacher Evaluation S2 Campaign
    ('750e8400-e29b-41d4-a716-446655440041'::uuid, '650e8400-e29b-41d4-a716-446655440004'::uuid, 1, 'Teaching Quality S2', 'RATING', true, NULL, NOW()),
    ('750e8400-e29b-41d4-a716-446655440042'::uuid, '650e8400-e29b-41d4-a716-446655440004'::uuid, 2, 'Communication S2', 'RATING', true, NULL, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample feedback responses for current term
INSERT INTO feedback_responses (id, id_campaign, anonymous_token, submission_date, is_complete)
SELECT
    gen_random_uuid(),
    '650e8400-e29b-41d4-a716-446655440001'::uuid,
    'token_' || gs.n,
    NOW() - INTERVAL '1 day' * (RANDOM() * 10)::int,
    true
FROM generate_series(1, 45) gs(n)
ON CONFLICT DO NOTHING;

-- Insert sample feedback responses for historical terms
-- Note: S1 2023/2024 campaign responses removed to test missing teacher data warning

INSERT INTO feedback_responses (id, id_campaign, anonymous_token, submission_date, is_complete)
SELECT
    gen_random_uuid(),
    '650e8400-e29b-41d4-a716-446655440004'::uuid,
    'token_s2_' || gs.n,
    NOW() - INTERVAL '150 day' - INTERVAL '1 day' * (RANDOM() * 30)::int,
    true
FROM generate_series(1, 35) gs(n)
ON CONFLICT DO NOTHING;

-- Insert sample feedback answers for all campaigns
INSERT INTO feedback_answers (id, id_response, id_question, rating_value)
SELECT
    gen_random_uuid(),
    fr.id,
    fq.id,
    (4 + RANDOM())::int
FROM feedback_responses fr
JOIN feedback_campaigns fc ON fr.id_campaign = fc.id
JOIN feedback_questions fq ON fq.id_campaign = fc.id
WHERE fq.question_type = 'RATING'
ON CONFLICT DO NOTHING;

-- Insert attendance records (using enrollment-based structure)
INSERT INTO attendance (id, id_enrollment, attendance_date, is_present, notes, created_at, id_created_by)
SELECT 
    gen_random_uuid(),
    e.id,
    CURRENT_DATE,
    (RANDOM() > 0.15), -- 85% attendance rate
    NULL,
    NOW(),
    '20000000-0000-0000-0000-000000000001'::uuid
FROM enrollments e
INNER JOIN class_groups cg ON e.id_class_group = cg.id
INNER JOIN class_sessions cs ON cs.id_class_group = cg.id AND cs.session_date = CURRENT_DATE
LIMIT 10;

-- Insert teacher attendance
INSERT INTO teacher_attendance (id, id_class_session, id_scheduled_instructor, id_actual_instructor, arrival_time, departure_time, is_present, notes, created_at)
SELECT
    gen_random_uuid(),
    cs.id,
    cs.id_instructor,
    cs.id_instructor,
    -- For the specific test session (450e8400-e29b-41d4-a716-446655440001), ensure NO arrival_time (not checked in)
    -- For other sessions, use random attendance
    CASE
        WHEN cs.id = '450e8400-e29b-41d4-a716-446655440001'::uuid THEN NULL  -- Test session: not checked in
        WHEN RANDOM() > 0.2 THEN NOW() - INTERVAL '10 minutes'
        ELSE NULL
    END,
    NULL,
    -- Ensure the test instructor is marked as not present initially for testing late check-in scenario
    CASE
        WHEN cs.id = '450e8400-e29b-41d4-a716-446655440001'::uuid THEN false  -- Test session: not present
        ELSE (RANDOM() > 0.2)
    END,
    NULL,
    NOW()
FROM class_sessions cs
WHERE cs.session_date = CURRENT_DATE;

-- Insert system alerts
INSERT INTO system_alerts (id, alert_type, severity, id_class_session, id_teacher, alert_message, is_resolved, created_at)
VALUES 
    ('850e8400-e29b-41d4-a716-446655440001'::uuid, 'SUBSTITUTE_NEEDED', 'HIGH', '450e8400-e29b-41d4-a716-446655440001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'Teacher emergency absence - Ustadz Ahmad unable to attend Tahsin 1 class due to illness', false, NOW()),
    ('850e8400-e29b-41d4-a716-446655440002'::uuid, 'LOW_ATTENDANCE', 'LOW', NULL, NULL, 'Low attendance detected in Tahfizh 2 class (65% attendance rate)', false, NOW()),
    ('850e8400-e29b-41d4-a716-446655440003'::uuid, 'SESSION_NOT_STARTED', 'HIGH', NULL, NULL, 'Session Tajwid 1 not started on time (15 minutes delay)', false, NOW()),
    ('850e8400-e29b-41d4-a716-446655440004'::uuid, 'LATE_CHECK_IN', 'MEDIUM', NULL, NULL, 'Teacher late check-in for session', false, NOW());

-- Insert test students for student report validation tests
INSERT INTO users (id, username, email, full_name, phone_number, is_active, created_at, updated_at)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid, 'ahmad.fauzan.test', 'ahmad.fauzan.test@example.com', 'Ahmad Fauzan', '+628123456789', true, NOW(), NOW()),
    ('b2c3d4e5-f6a7-8901-bcde-f23456789012'::uuid, 'maria.santos.test', 'maria.santos.test@example.com', 'Maria Santos', '+628123456790', true, NOW(), NOW()),
    ('c3d4e5f6-a7b8-9012-cdef-345678901234'::uuid, 'ali.rahman.test', 'ali.rahman.test@example.com', 'Ali Rahman', '+628123456791', true, NOW(), NOW()),
    ('d4e5f6a7-b8c9-0123-def0-456789012345'::uuid, 'invalid.email.test', 'invalid-email-address', 'Invalid Email Student', '+628123456792', true, NOW(), NOW()),
    ('e5f6a7b8-c9d0-1234-ef01-567890123456'::uuid, 'ahmad.zaki.test', 'ahmad.zaki.test@example.com', 'Ahmad Zaki', '+628123456793', true, NOW(), NOW()),
    ('f6a7b8c9-d0e1-2345-f012-678901234567'::uuid, 'fatimah.zahra.test', 'fatimah.zahra.test@example.com', 'Fatimah Zahra', '+628123456794', true, NOW(), NOW()),
    ('a7b8c9d0-e1f2-3456-0123-789012345678'::uuid, 'siti.khadijah.test', 'siti.khadijah.test@example.com', 'Siti Khadijah', '+628123456795', true, NOW(), NOW()),
    -- Add additional test students for better coverage
    ('1111aaaa-2222-bbbb-3333-ccccddddeeee'::uuid, 'testing.student1', 'test.student1@example.com', 'Test Student 1', '+628123456796', true, NOW(), NOW()),
    ('2222bbbb-3333-cccc-4444-ddddeeeeaaaa'::uuid, 'testing.student2', 'test.student2@example.com', 'Test Student 2', '+628123456797', true, NOW(), NOW()),
    ('3333cccc-4444-dddd-5555-eeeeaaaabbbb'::uuid, 'testing.student3', 'test.student3@example.com', 'Test Student 3', '+628123456798', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Assign student role to test students
INSERT INTO user_roles (id, id_user, id_role, assigned_at, id_assigned_by)
SELECT
    gen_random_uuid(),
    u.id,
    r.id,
    NOW(),
    NULL
FROM users u
CROSS JOIN roles r
WHERE u.username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'invalid.email.test', 'ahmad.zaki.test', 'fatimah.zahra.test', 'siti.khadijah.test', 'testing.student1', 'testing.student2', 'testing.student3')
AND r.code = 'STUDENT'
ON CONFLICT (id_user, id_role) DO NOTHING;

-- Insert test class group for mixed data class
INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, id_time_slot, capacity, is_active, created_at, updated_at)
VALUES
    ('12345678-1234-1234-1234-123456789012'::uuid, 'Mixed Data Class',
     (SELECT id FROM levels LIMIT 1),
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025' from main migration
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'MONDAY' LIMIT 1),  -- Use existing Monday time slot
     20, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Add class groups for historical terms (for cross-term analytics happy path tests)
INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, id_time_slot, capacity, is_active, created_at, updated_at)
VALUES
    ('C1000001-0000-0000-0000-000000000001'::uuid, 'Tahsin Class S1-1',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000001'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'MONDAY' LIMIT 1),
     8, true, NOW(), NOW()),
    ('C1000001-0000-0000-0000-000000000002'::uuid, 'Tahsin Class S1-2',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000001'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'MONDAY' LIMIT 1),
     8, true, NOW(), NOW()),
    ('C1000001-0000-0000-0000-000000000003'::uuid, 'Tahsin Class S1-3',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000001'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'MONDAY' LIMIT 1),
     8, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, id_time_slot, capacity, is_active, created_at, updated_at)
VALUES
    ('C2000001-0000-0000-0000-000000000001'::uuid, 'Tahsin Class S2-1',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000002'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'TUESDAY' LIMIT 1),
     8, true, NOW(), NOW()),
    ('C2000001-0000-0000-0000-000000000002'::uuid, 'Tahsin Class S2-2',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000002'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'TUESDAY' LIMIT 1),
     8, true, NOW(), NOW()),
    ('C2000001-0000-0000-0000-000000000003'::uuid, 'Tahsin Class S2-3',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000002'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'TUESDAY' LIMIT 1),
     8, true, NOW(), NOW()),
    ('C2000001-0000-0000-0000-000000000004'::uuid, 'Tahsin Class S2-4',
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000002'::uuid,
     (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM time_slot WHERE day_of_week = 'TUESDAY' LIMIT 1),
     8, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Add teacher level assignments for historical terms
INSERT INTO teacher_level_assignments (id, id_teacher, id_level, id_term, competency_level, specialization, created_at, updated_at)
VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000001'::uuid, 'SENIOR', 'ADVANCED', NOW(), NOW()),
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'A1000000-0000-0000-0000-000000000002'::uuid, 'SENIOR', 'ADVANCED', NOW(), NOW()),
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1),
     (SELECT id FROM levels WHERE name LIKE '%Tahsin%' LIMIT 1),
     'D0000000-0000-0000-0000-000000000001'::uuid, 'SENIOR', 'ADVANCED', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert enrollments for test students in current term (only 5 students to trigger partialData flag: 1-9 range)
INSERT INTO enrollments (id, id_student, id_class_group, enrollment_date, status, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    '12345678-1234-1234-1234-123456789012'::uuid,
    CURRENT_DATE - INTERVAL '30 days',
    'ACTIVE',
    NOW()
FROM users u
WHERE u.username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'ahmad.zaki.test', 'fatimah.zahra.test')
ON CONFLICT (id_student, id_class_group) DO NOTHING;

-- Insert completed enrollments for historical terms (Semester 1 2023/2024)
INSERT INTO enrollments (id, id_student, id_class_group, enrollment_date, status, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    cg.id,
    CURRENT_DATE - INTERVAL '365 days',
    'COMPLETED',
    NOW()
FROM users u
CROSS JOIN class_groups cg
WHERE u.username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test')
AND cg.id_term = 'A1000000-0000-0000-0000-000000000001'::uuid
ON CONFLICT (id_student, id_class_group) DO NOTHING;

-- Insert completed enrollments for Semester 2 2023/2024
INSERT INTO enrollments (id, id_student, id_class_group, enrollment_date, status, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    cg.id,
    CURRENT_DATE - INTERVAL '200 days',
    'COMPLETED',
    NOW()
FROM users u
CROSS JOIN class_groups cg
WHERE u.username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'ahmad.zaki.test')
AND cg.id_term = 'A1000000-0000-0000-0000-000000000002'::uuid
ON CONFLICT (id_student, id_class_group) DO NOTHING;

-- Insert some student assessments for testing data completeness scenarios
INSERT INTO student_assessments (id, id_student, id_term, student_category, assessment_type, assessment_score, assessment_grade, assessment_date, created_at)
VALUES
    -- Ahmad Fauzan - incomplete data (missing midterm and final) for Semester 1 2024/2025
    (gen_random_uuid(), 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'PLACEMENT', 85.0, 'B+', CURRENT_DATE - INTERVAL '60 days', NOW()),

    -- Ahmad Fauzan - also add incomplete data for Intensive 2023/2024 (in case wrong term is selected)
    (gen_random_uuid(), 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
     'A1000000-0000-0000-0000-000000000003'::uuid,  -- Intensive 2023/2024
     'EXISTING', 'PLACEMENT', 85.0, 'B+', CURRENT_DATE - INTERVAL '50 days', NOW()),

    -- Ali Rahman - complete data
    (gen_random_uuid(), 'c3d4e5f6-a7b8-9012-cdef-345678901234'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'PLACEMENT', 85.0, 'B+', CURRENT_DATE - INTERVAL '60 days', NOW()),
    (gen_random_uuid(), 'c3d4e5f6-a7b8-9012-cdef-345678901234'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'MIDTERM', 90.0, 'A-', CURRENT_DATE - INTERVAL '30 days', NOW()),
    (gen_random_uuid(), 'c3d4e5f6-a7b8-9012-cdef-345678901234'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'FINAL', 87.0, 'A-', CURRENT_DATE - INTERVAL '10 days', NOW()),

    -- Ahmad Zaki - partial data (missing final)
    (gen_random_uuid(), 'e5f6a7b8-c9d0-1234-ef01-567890123456'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'PLACEMENT', 78.0, 'B', CURRENT_DATE - INTERVAL '60 days', NOW()),
    (gen_random_uuid(), 'e5f6a7b8-c9d0-1234-ef01-567890123456'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'MIDTERM', 82.0, 'B+', CURRENT_DATE - INTERVAL '30 days', NOW()),

    -- Maria Santos - complete data for Semester 1 2024/2025 (will be enrolled but test will select wrong term)
    (gen_random_uuid(), 'b2c3d4e5-f6a7-8901-bcde-f23456789012'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'PLACEMENT', 88.0, 'A-', CURRENT_DATE - INTERVAL '60 days', NOW()),
    (gen_random_uuid(), 'b2c3d4e5-f6a7-8901-bcde-f23456789012'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'MIDTERM', 90.0, 'A', CURRENT_DATE - INTERVAL '30 days', NOW()),
    (gen_random_uuid(), 'b2c3d4e5-f6a7-8901-bcde-f23456789012'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'FINAL', 92.0, 'A', CURRENT_DATE - INTERVAL '10 days', NOW()),

    -- Invalid Email Student - complete data for Semester 1 2024/2025 (for email testing)
    (gen_random_uuid(), 'd4e5f6a7-b8c9-0123-def0-456789012345'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'PLACEMENT', 80.0, 'B', CURRENT_DATE - INTERVAL '60 days', NOW()),
    (gen_random_uuid(), 'd4e5f6a7-b8c9-0123-def0-456789012345'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'MIDTERM', 82.0, 'B+', CURRENT_DATE - INTERVAL '30 days', NOW()),
    (gen_random_uuid(), 'd4e5f6a7-b8c9-0123-def0-456789012345'::uuid,
     'D0000000-0000-0000-0000-000000000001'::uuid,  -- Use existing 'Semester 1 2024/2025'
     'EXISTING', 'FINAL', 84.0, 'B+', CURRENT_DATE - INTERVAL '10 days', NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert test report generation batch and items for PDF download testing
-- This simulates a completed report generation batch with pre-generated PDFs
INSERT INTO report_generation_batches (
    id,
    id_term,
    id_initiated_by,
    batch_name,
    status,
    report_type,
    total_reports,
    completed_reports,
    failed_reports,
    estimated_duration_minutes,
    actual_duration_minutes,
    report_configuration,
    initiated_at,
    started_at,
    completed_at,
    distribution_completed,
    created_at,
    updated_at
) VALUES (
    '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
    'D0000000-0000-0000-0000-000000000001'::uuid,  -- Semester 1 2024/2025
    (SELECT id FROM users WHERE username = 'academic.admin1' LIMIT 1),  -- Academic admin user
    'Student Reports - Semester 1 2024/2025',
    'COMPLETED',
    'SEMESTER_END_STUDENT_REPORTS',
    5,  -- Total reports for our test students
    5,  -- All completed
    0,  -- No failures
    15,  -- Estimated 15 minutes
    12,  -- Actually took 12 minutes
    '{"includeGrades": true, "includeAttendance": true, "includeComments": true}'::json,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '1 hour 48 minutes',
    true,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '1 hour 48 minutes'
) ON CONFLICT (id) DO NOTHING;

-- Insert individual report generation items with pre-generated PDF paths
INSERT INTO report_generation_items (
    id,
    id_batch,
    id_student,
    report_subject,
    report_description,
    report_type,
    priority,
    status,
    file_path,
    file_size_bytes,
    file_format,
    started_at,
    completed_at,
    processing_duration_seconds,
    distributed,
    distributed_at,
    distribution_method,
    created_at,
    updated_at
) VALUES
    -- Ahmad Fauzan report
    (
        gen_random_uuid(),
        '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
        'Student Report Card - Ahmad Fauzan',
        'Semester 1 2024/2025 Report Card for Ahmad Fauzan',
        'INDIVIDUAL_REPORT_CARD',
        5,
        'COMPLETED',
        '/reports/generated/semester-1-2024-2025/ahmad-fauzan-report-card.pdf',
        245760,  -- ~240KB file
        'PDF',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 55 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 52 minutes',
        180,  -- 3 minutes processing
        true,
        CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes',
        'DOWNLOAD',
        CURRENT_TIMESTAMP - INTERVAL '2 hours',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes'
    ),

    -- Maria Santos report
    (
        gen_random_uuid(),
        '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
        'b2c3d4e5-f6a7-8901-bcde-f23456789012'::uuid,
        'Student Report Card - Maria Santos',
        'Semester 1 2024/2025 Report Card for Maria Santos',
        'INDIVIDUAL_REPORT_CARD',
        5,
        'COMPLETED',
        '/reports/generated/semester-1-2024-2025/maria-santos-report-card.pdf',
        251904,  -- ~246KB file
        'PDF',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 54 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 51 minutes',
        175,  -- Processing time
        true,
        CURRENT_TIMESTAMP - INTERVAL '1 hour 49 minutes',
        'DOWNLOAD',
        CURRENT_TIMESTAMP - INTERVAL '2 hours',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 49 minutes'
    ),

    -- Ali Rahman report
    (
        gen_random_uuid(),
        '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
        'c3d4e5f6-a7b8-9012-cdef-345678901234'::uuid,
        'Student Report Card - Ali Rahman',
        'Semester 1 2024/2025 Report Card for Ali Rahman',
        'INDIVIDUAL_REPORT_CARD',
        5,
        'COMPLETED',
        '/reports/generated/semester-1-2024-2025/ali-rahman-report-card.pdf',
        248832,  -- ~243KB file
        'PDF',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 53 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes',
        185,  -- Processing time
        true,
        CURRENT_TIMESTAMP - INTERVAL '1 hour 48 minutes',
        'DOWNLOAD',
        CURRENT_TIMESTAMP - INTERVAL '2 hours',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 48 minutes'
    ),

    -- Ahmad Zaki report (partial data, but PDF generated with disclaimers)
    (
        gen_random_uuid(),
        '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
        'e5f6a7b8-c9d0-1234-ef01-567890123456'::uuid,
        'Student Report Card - Ahmad Zaki',
        'Semester 1 2024/2025 Report Card for Ahmad Zaki (Partial Data)',
        'INDIVIDUAL_REPORT_CARD',
        5,
        'COMPLETED',
        '/reports/generated/semester-1-2024-2025/ahmad-zaki-report-card.pdf',
        242688,  -- ~237KB file
        'PDF',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 52 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 49 minutes',
        190,  -- Processing time
        true,
        CURRENT_TIMESTAMP - INTERVAL '1 hour 47 minutes',
        'DOWNLOAD',
        CURRENT_TIMESTAMP - INTERVAL '2 hours',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 47 minutes'
    ),

    -- Invalid Email Student report
    (
        gen_random_uuid(),
        '75e2f1cf-f071-426f-a26a-d714809b8a63'::uuid,
        'd4e5f6a7-b8c9-0123-def0-456789012345'::uuid,
        'Student Report Card - Invalid Email Student',
        'Semester 1 2024/2025 Report Card for Invalid Email Student',
        'INDIVIDUAL_REPORT_CARD',
        5,
        'COMPLETED',
        '/reports/generated/semester-1-2024-2025/invalid-email-student-report-card.pdf',
        247808,  -- ~242KB file
        'PDF',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 51 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 48 minutes',
        170,  -- Processing time
        true,
        CURRENT_TIMESTAMP - INTERVAL '1 hour 46 minutes',
        'DOWNLOAD',
        CURRENT_TIMESTAMP - INTERVAL '2 hours',
        CURRENT_TIMESTAMP - INTERVAL '1 hour 46 minutes'
    )
ON CONFLICT (id) DO NOTHING;