-- Student Report Test Setup SQL
-- This script adds comprehensive assessment data for existing seed students
-- to enable complete report generation testing

-- ==============================================
-- ENSURE CLASS GROUPS ARE LINKED TO CURRENT TERM
-- ==============================================

-- Update the Tahfidz 2 class group to be associated with the current term
UPDATE class_groups
SET id_term = 'D0000000-0000-0000-0000-000000000001'
WHERE id = '70000000-0000-0000-0000-000000000004';

-- Note: Ali Rahman is already enrolled in class group 70000000-0000-0000-0000-000000000004 (Tahfidz 2 - Kelas Pagi) from seed data

-- ==============================================
-- ADD STUDENT ASSESSMENTS FOR CURRENT TERM
-- ==============================================

-- Ali Rahman (30000000-0000-0000-0000-000000000001) - Complete assessment data
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
(gen_random_uuid(), '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 85.0, 'B+', CURRENT_DATE - INTERVAL '50 days',
 'Strong foundation in Quranic recitation', '20000000-0000-0000-0000-000000000001', true, NOW()),

(gen_random_uuid(), '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'MIDTERM', 88.5, 'A-', CURRENT_DATE - INTERVAL '30 days',
 'Excellent progress in memorization', '20000000-0000-0000-0000-000000000001', true, NOW()),

(gen_random_uuid(), '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'FINAL', 87.5, 'A-', CURRENT_DATE - INTERVAL '5 days',
 'Consistent performance throughout semester', '20000000-0000-0000-0000-000000000001', true, NOW());

-- Sarah Abdullah (30000000-0000-0000-0000-000000000002) - Complete assessment data
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
 'Steady improvement observed', '20000000-0000-0000-0000-000000000001', true, NOW()),

(gen_random_uuid(), '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'FINAL', 86.5, 'A-', CURRENT_DATE - INTERVAL '5 days',
 'Strong finish to the semester', '20000000-0000-0000-0000-000000000001', true, NOW());

-- Fatimah Zahra (30000000-0000-0000-0000-000000000006) - Complete assessment data
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
(gen_random_uuid(), '30000000-0000-0000-0000-000000000006', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 92.0, 'A', CURRENT_DATE - INTERVAL '50 days',
 'Exceptional recitation and memorization skills', '20000000-0000-0000-0000-000000000002', true, NOW()),

(gen_random_uuid(), '30000000-0000-0000-0000-000000000006', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'MIDTERM', 94.5, 'A', CURRENT_DATE - INTERVAL '30 days',
 'Outstanding midterm performance', '20000000-0000-0000-0000-000000000002', true, NOW()),

(gen_random_uuid(), '30000000-0000-0000-0000-000000000006', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'FINAL', 93.0, 'A', CURRENT_DATE - INTERVAL '5 days',
 'Excellent final assessment', '20000000-0000-0000-0000-000000000002', true, NOW());

-- Additional test students for comprehensive testing
INSERT INTO users (id, username, email, full_name, phone_number, address, is_active) VALUES
('30000000-0000-0000-0000-000000000007', 'siswa.ahmad.zaki', 'ahmad.zaki@gmail.com', 'Ahmad Zaki', '081234567107', 'Jl. Flamboyan No. 40, Jakarta', true),
('30000000-0000-0000-0000-000000000008', 'siswa.siti.khadijah', 'siti.khadijah@gmail.com', 'Siti Khadijah', '081234567108', 'Jl. Bougenvil No. 45, Jakarta', true)
ON CONFLICT (id) DO NOTHING;

-- Add credentials for new test students
INSERT INTO user_credentials (id_user, password_hash) VALUES
('30000000-0000-0000-0000-000000000007', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000008', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i')
ON CONFLICT (id_user) DO NOTHING;

-- Add student roles for new test students
INSERT INTO user_roles (id_user, id_role) VALUES
('30000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000003')
ON CONFLICT (id_user, id_role) DO NOTHING;

-- Add enrollments for new test students
INSERT INTO enrollments (id_student, id_class_group, enrollment_date, status) VALUES
('30000000-0000-0000-0000-000000000007', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000008', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE')
ON CONFLICT (id_student, id_class_group) DO NOTHING;

-- Add assessment data for new test students
INSERT INTO student_assessments (
    id, id_student, id_term,
    student_category, assessment_type, assessment_score, assessment_grade,
    assessment_date, assessment_notes, assessed_by, is_validated, created_at
) VALUES
-- Ahmad Zaki assessments
(gen_random_uuid(), '30000000-0000-0000-0000-000000000007', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 78.0, 'B', CURRENT_DATE - INTERVAL '50 days',
 'Good foundation, needs improvement', '20000000-0000-0000-0000-000000000001', true, NOW()),
(gen_random_uuid(), '30000000-0000-0000-0000-000000000007', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'MIDTERM', 82.0, 'B+', CURRENT_DATE - INTERVAL '30 days',
 'Showing improvement', '20000000-0000-0000-0000-000000000001', true, NOW()),
(gen_random_uuid(), '30000000-0000-0000-0000-000000000007', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'FINAL', 80.5, 'B+', CURRENT_DATE - INTERVAL '5 days',
 'Consistent effort throughout semester', '20000000-0000-0000-0000-000000000001', true, NOW()),

-- Siti Khadijah assessments
(gen_random_uuid(), '30000000-0000-0000-0000-000000000008', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'PLACEMENT', 95.0, 'A+', CURRENT_DATE - INTERVAL '50 days',
 'Exceptional abilities from start', '20000000-0000-0000-0000-000000000002', true, NOW()),
(gen_random_uuid(), '30000000-0000-0000-0000-000000000008', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'MIDTERM', 96.5, 'A+', CURRENT_DATE - INTERVAL '30 days',
 'Outstanding midterm performance', '20000000-0000-0000-0000-000000000002', true, NOW()),
(gen_random_uuid(), '30000000-0000-0000-0000-000000000008', 'D0000000-0000-0000-0000-000000000001',
 'EXISTING', 'FINAL', 97.0, 'A+', CURRENT_DATE - INTERVAL '5 days',
 'Exemplary final assessment', '20000000-0000-0000-0000-000000000002', true, NOW());