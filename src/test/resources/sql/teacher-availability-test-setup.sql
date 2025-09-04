-- =====================================================
-- TEACHER AVAILABILITY TEST SETUP
-- Setup test data for Phase 3: Teacher Availability Monitoring
-- Complete workflow: Assessment → Preparation → Teacher Submission → Monitoring
-- =====================================================

-- Step 1: Create assessment foundation data to meet 80% threshold
-- Insert student assessments using existing student user IDs and proper level references
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_date, assessment_score, is_validated, assessment_notes, 
    determined_level, assessed_by, created_at
) VALUES
-- Using existing student user IDs from V002 seed data and existing level IDs
('B1000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', NOW() - INTERVAL '2 days', 85, true, 'TAVAIL_TEST_Good reading skills', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), '20000000-0000-0000-0000-000000000001', NOW() - INTERVAL '3 days'),
('B1000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', NOW() - INTERVAL '2 days', 75, true, 'TAVAIL_TEST_Fair reading skills', (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), '20000000-0000-0000-0000-000000000001', NOW() - INTERVAL '3 days'),
('B1000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', NOW() - INTERVAL '2 days', 90, true, 'TAVAIL_TEST_Excellent reading skills', (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), '20000000-0000-0000-0000-000000000002', NOW() - INTERVAL '3 days'),
('B1000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000004', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', NOW() - INTERVAL '2 days', 80, true, 'TAVAIL_TEST_Good progress', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), '20000000-0000-0000-0000-000000000002', NOW() - INTERVAL '3 days'),
('B1000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000005', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', NOW() - INTERVAL '2 days', 88, true, 'TAVAIL_TEST_Very good performance', (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), '20000000-0000-0000-0000-000000000003', NOW() - INTERVAL '3 days')
-- 5 out of 5 students assessed (100% completion rate to ensure threshold met)
ON CONFLICT (id) DO NOTHING;

-- Step 2: Update academic term status to PLANNING to allow teacher availability submission
-- This simulates academic staff completing assessment foundation and launching preparation
UPDATE academic_terms 
SET status = 'PLANNING', preparation_deadline = '2025-01-25'
WHERE id = 'D0000000-0000-0000-0000-000000000002';

-- Step 3: Insert test teacher availability data for existing instructors
-- Using existing instructor user IDs from V002 seed data
INSERT INTO teacher_availability (id, id_teacher, id_term, day_of_week, id_session, is_available, capacity, max_classes_per_week, preferences, submitted_at) VALUES
-- Teacher 1 (ustadz.ahmad) - has submitted availability
('91000000-0001-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'TAVAIL_TEST_Prefers morning classes', NOW()),
('91000000-0001-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'TAVAIL_TEST_Prefers morning classes', NOW()),
-- Teacher 2 (ustadzah.fatimah) - has submitted availability
('91000000-0001-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'TAVAIL_TEST_Evening availability', NOW()),
('91000000-0001-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'TAVAIL_TEST_Evening availability', NOW())
-- Teacher 3 (ustadz.ibrahim) - has NOT submitted availability (to test pending status)
ON CONFLICT (id) DO NOTHING;