-- Student Report Incomplete Data Setup SQL
-- This script creates students with incomplete/missing data to test system resilience
-- Used for testing report generation with data quality issues

-- ==============================================
-- ENSURE CLASS GROUPS ARE LINKED TO CURRENT TERM
-- ==============================================

-- Update the Tahfidz 2 class group to be associated with the current term
UPDATE class_groups
SET id_term = 'D0000000-0000-0000-0000-000000000001'
WHERE id = '70000000-0000-0000-0000-000000000004';

-- ==============================================
-- STUDENTS WITH INCOMPLETE ASSESSMENT DATA
-- ==============================================

-- Ali Rahman (30000000-0000-0000-0000-000000000001) - Only placement assessment, missing midterm/final
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
(gen_random_uuid(), '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 85.0, 'B+', CURRENT_DATE - INTERVAL '50 days',
 'Strong foundation in Quranic recitation', '20000000-0000-0000-0000-000000000001', true, NOW())
ON CONFLICT DO NOTHING;

-- Sarah Abdullah (30000000-0000-0000-0000-000000000002) - Missing final assessment
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
(gen_random_uuid(), '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 82.0, 'B+', CURRENT_DATE - INTERVAL '50 days',
 'Good recitation skills', '20000000-0000-0000-0000-000000000001', true, NOW()),
(gen_random_uuid(), '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'MIDTERM', 85.0, 'B+', CURRENT_DATE - INTERVAL '30 days',
 'Steady improvement observed', '20000000-0000-0000-0000-000000000001', true, NOW())
ON CONFLICT DO NOTHING;

-- Fatimah Zahra (30000000-0000-0000-0000-000000000006) - No assessments at all
-- (No assessment data inserted - will test handling of completely missing data)

-- ==============================================
-- STUDENTS WITH INCOMPLETE PROFILE DATA
-- ==============================================

-- Create additional test students with incomplete profile information
INSERT INTO users (id, username, email, full_name, phone_number, address, is_active) VALUES
('30000000-0000-0000-0000-000000000009', 'siswa.incomplete1', 'incomplete1@gmail.com', 'Student Incomplete One', NULL, NULL, true),
('30000000-0000-0000-0000-000000000010', 'siswa.incomplete2', 'incomplete2@gmail.com', 'Student Incomplete Two', '081234567110', 'Partial Address', true)
ON CONFLICT (id) DO NOTHING;

-- Add credentials for new test students
INSERT INTO user_credentials (id_user, password_hash) VALUES
('30000000-0000-0000-0000-000000000009', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000010', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i')
ON CONFLICT (id_user) DO NOTHING;

-- Add student roles for new test students
INSERT INTO user_roles (id_user, id_role) VALUES
('30000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000003')
ON CONFLICT (id_user, id_role) DO NOTHING;

-- Add enrollments for new test students
INSERT INTO enrollments (id_student, id_class_group, enrollment_date, status) VALUES
('30000000-0000-0000-0000-000000000009', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000010', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE')
ON CONFLICT (id_student, id_class_group) DO NOTHING;

-- Student with incomplete profile - only placement assessment
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
(gen_random_uuid(), '30000000-0000-0000-0000-000000000009', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 70.0, 'C+', CURRENT_DATE - INTERVAL '50 days',
 'Basic skills, needs development', '20000000-0000-0000-0000-000000000001', true, NOW())
ON CONFLICT DO NOTHING;

-- Student with no email - no assessments
-- (Student 30000000-0000-0000-0000-000000000010 has no assessment data)