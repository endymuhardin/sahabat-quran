-- Operation Workflow Test Setup SQL
-- This script sets up test data for Academic Admin operation workflow tests
-- and Cross-Term Analytics tests

-- Insert additional academic terms for cross-term analytics testing
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline, created_at, updated_at) VALUES
-- Historical terms for testing (using dynamic dates)
('A1000000-0000-0000-0000-000000000001', 'Semester 1 2023/2024', CURRENT_DATE - INTERVAL '365 days', CURRENT_DATE - INTERVAL '230 days', 'COMPLETED', CURRENT_DATE - INTERVAL '372 days', NOW(), NOW()),
('A1000000-0000-0000-0000-000000000002', 'Semester 2 2023/2024', CURRENT_DATE - INTERVAL '229 days', CURRENT_DATE - INTERVAL '80 days', 'COMPLETED', CURRENT_DATE - INTERVAL '235 days', NOW(), NOW()),
('A1000000-0000-0000-0000-000000000003', 'Intensive 2023/2024', CURRENT_DATE - INTERVAL '79 days', CURRENT_DATE - INTERVAL '15 days', 'COMPLETED', CURRENT_DATE - INTERVAL '85 days', NOW(), NOW())
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
INSERT INTO class_sessions (id, id_class_group, session_date, session_number, preparation_status, id_instructor, created_at, updated_at)
VALUES
    ('450e8400-e29b-41d4-a716-446655440001'::uuid,
     (SELECT id FROM class_groups LIMIT 1),
     CURRENT_DATE,
     1,
     'IN_PROGRESS',  -- IN_PROGRESS status will trigger late session detection
     '20000000-0000-0000-0000-000000000001'::uuid,
     NOW(),
     NOW());

-- Insert additional class sessions (only one per class group per date due to unique constraint)
INSERT INTO class_sessions (id, id_class_group, session_date, session_number, preparation_status, id_instructor, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    cg.id,
    CURRENT_DATE,
    1,
    'READY',
    '20000000-0000-0000-0000-000000000001'::uuid,
    NOW(),
    NOW()
FROM class_groups cg
WHERE cg.id != (SELECT id FROM class_groups LIMIT 1)
LIMIT 2;

-- Insert test feedback campaigns
INSERT INTO feedback_campaigns (id, campaign_name, campaign_type, target_audience, start_date, end_date, is_active, created_at, id_created_by)
VALUES 
    ('650e8400-e29b-41d4-a716-446655440001'::uuid, 'Teacher Evaluation Q1', 'TEACHER_EVALUATION', 'STUDENTS', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '7 days', true, NOW(), '20000000-0000-0000-0000-000000000001'::uuid),
    ('650e8400-e29b-41d4-a716-446655440002'::uuid, 'Facility Assessment', 'FACILITY_ASSESSMENT', 'BOTH', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '14 days', true, NOW(), '20000000-0000-0000-0000-000000000001'::uuid);

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
    ('750e8400-e29b-41d4-a716-446655440021'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid, 1, 'Facility Cleanliness', 'RATING', true, NULL, NOW());

-- Insert sample feedback responses
INSERT INTO feedback_responses (id, id_campaign, anonymous_token, submission_date, is_complete)
SELECT 
    gen_random_uuid(),
    '650e8400-e29b-41d4-a716-446655440001'::uuid,
    'token_' || gs.n,
    NOW() - INTERVAL '1 day' * (RANDOM() * 10)::int,
    true
FROM generate_series(1, 45) gs(n);

-- Insert sample feedback answers
INSERT INTO feedback_answers (id, id_response, id_question, rating_value)
SELECT 
    gen_random_uuid(),
    fr.id,
    fq.id,
    (4 + RANDOM())::int
FROM feedback_responses fr
CROSS JOIN feedback_questions fq
WHERE fq.question_type = 'RATING';

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