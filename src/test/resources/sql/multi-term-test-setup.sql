-- =====================================================
-- MULTI-TERM MANAGEMENT TEST SETUP
-- Setup test data for multi-term management scenarios
-- Creates multiple terms with different statuses for navigation testing
-- =====================================================

-- STEP 1: CREATE MULTIPLE ACADEMIC TERMS WITH DIFFERENT STATUSES
-- Ensure we have terms in all statuses for comprehensive testing

-- COMPLETED Term (Historical data)
INSERT INTO academic_terms (
    id, name, start_date, end_date, registration_start, registration_end, 
    status, preparation_deadline, created_at
) VALUES (
    'D0000000-0000-0000-0000-000000000003',
    'Semester 1 2023/2024',
    '2023-09-01',
    '2024-01-31',
    '2023-07-01',
    '2023-08-15',
    'COMPLETED',
    '2023-08-20',
    NOW() - INTERVAL '1 year'
) ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    name = EXCLUDED.name;

-- ACTIVE Term (Current operations)
INSERT INTO academic_terms (
    id, name, start_date, end_date, registration_start, registration_end, 
    status, preparation_deadline, created_at
) VALUES (
    'D0000000-0000-0000-0000-000000000001',
    'Semester 1 2024/2025',
    '2024-09-01',
    '2025-01-31',
    '2024-07-01',
    '2024-08-15',
    'ACTIVE',
    '2024-08-20',
    NOW() - INTERVAL '6 months'
) ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    name = EXCLUDED.name;

-- PLANNING Term (Next semester preparation)
INSERT INTO academic_terms (
    id, name, start_date, end_date, registration_start, registration_end, 
    status, preparation_deadline, created_at
) VALUES (
    'D0000000-0000-0000-0000-000000000002',
    'Semester 2 2024/2025',
    '2025-02-01',
    '2025-06-30',
    '2024-12-01',
    '2025-01-15',
    'PLANNING',
    '2025-01-25',
    NOW() - INTERVAL '3 months'
) ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    name = EXCLUDED.name;

-- PLANNING Term (Future intensive)
INSERT INTO academic_terms (
    id, name, start_date, end_date, registration_start, registration_end, 
    status, preparation_deadline, created_at
) VALUES (
    'D0000000-0000-0000-0000-000000000004',
    'Intensive 2024/2025',
    '2025-07-01',
    '2025-08-31',
    '2025-05-01',
    '2025-06-15',
    'PLANNING',
    '2025-06-20',
    NOW() - INTERVAL '1 month'
) ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    name = EXCLUDED.name;

-- STEP 2: CREATE SAMPLE DATA FOR EACH TERM TO SIMULATE REAL SCENARIOS

-- Sample students for COMPLETED term (historical data)
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_score, determined_level, assessment_date, 
    assessment_notes, assessed_by, is_validated, created_at
) VALUES
('MTM-COMPLETED-001', '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000003', 'CONTINUING', 'SEMESTER_END', 85, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '8 months', 'MTM_TEST_Historical assessment data', '20000000-0000-0000-0000-000000000001', true, NOW() - INTERVAL '8 months'),
('MTM-COMPLETED-002', '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000003', 'CONTINUING', 'SEMESTER_END', 78, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), NOW() - INTERVAL '8 months', 'MTM_TEST_Historical assessment data', '20000000-0000-0000-0000-000000000001', true, NOW() - INTERVAL '8 months')
ON CONFLICT (id) DO NOTHING;

-- Sample data for ACTIVE term (current operations)
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_score, determined_level, assessment_date, 
    assessment_notes, assessed_by, is_validated, created_at
) VALUES
('MTM-ACTIVE-001', '30000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000001', 'CONTINUING', 'MID_SEMESTER', 82, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '2 months', 'MTM_TEST_Active term current data', '20000000-0000-0000-0000-000000000002', true, NOW() - INTERVAL '2 months'),
('MTM-ACTIVE-002', '30000000-0000-0000-0000-000000000004', 'D0000000-0000-0000-0000-000000000001', 'CONTINUING', 'MID_SEMESTER', 79, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), NOW() - INTERVAL '2 months', 'MTM_TEST_Active term current data', '20000000-0000-0000-0000-000000000002', true, NOW() - INTERVAL '2 months')
ON CONFLICT (id) DO NOTHING;

-- Sample data for PLANNING term (preparation data)
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_score, determined_level, assessment_date, 
    assessment_notes, assessed_by, is_validated, created_at
) VALUES
('MTM-PLANNING-001', '30000000-0000-0000-0000-000000000005', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 88, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '1 month', 'MTM_TEST_Planning term placement test', '20000000-0000-0000-0000-000000000003', true, NOW() - INTERVAL '1 month')
ON CONFLICT (id) DO NOTHING;

-- STEP 3: TEACHER AVAILABILITY FOR PLANNING TERM
-- Add some teacher availability data to simulate planning term preparation
INSERT INTO teacher_availabilities (
    id, id_teacher, id_term, monday_morning, monday_afternoon, monday_evening,
    tuesday_morning, tuesday_afternoon, tuesday_evening,
    wednesday_morning, wednesday_afternoon, wednesday_evening,
    thursday_morning, thursday_afternoon, thursday_evening,
    friday_morning, friday_afternoon, friday_evening,
    saturday_morning, saturday_afternoon, saturday_evening,
    sunday_morning, sunday_afternoon, sunday_evening,
    submitted_at, created_at
) VALUES
('MTM-AVAIL-001', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 
 true, false, true, true, false, true, true, false, false,
 false, true, true, true, false, false, false, false, false,
 false, false, false, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks'),
('MTM-AVAIL-002', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002',
 false, true, false, false, true, false, true, true, true,
 true, false, false, false, true, true, true, false, false,
 false, false, false, NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 week')
ON CONFLICT (id) DO NOTHING;

-- STEP 4: SAMPLE CLASSES FOR DIFFERENT TERMS
-- Create some classes to simulate different term contexts
INSERT INTO classes (
    id, id_term, name, level_name, target_capacity, current_enrollment,
    class_status, schedule_summary, created_at
) VALUES
-- COMPLETED term classes (historical)
('MTM-CLASS-COMPLETED-01', 'D0000000-0000-0000-0000-000000000003', 'Tahsin 1 - Morning A (S1 2023/24)', 'Tahsin 1', 15, 15, 'COMPLETED', 'Mon/Wed/Fri 08:00-09:00', NOW() - INTERVAL '8 months'),
('MTM-CLASS-COMPLETED-02', 'D0000000-0000-0000-0000-000000000003', 'Tahsin 2 - Evening B (S1 2023/24)', 'Tahsin 2', 12, 12, 'COMPLETED', 'Tue/Thu 19:00-20:00', NOW() - INTERVAL '8 months'),

-- ACTIVE term classes (current operations)
('MTM-CLASS-ACTIVE-01', 'D0000000-0000-0000-0000-000000000001', 'Tahsin 1 - Morning Current', 'Tahsin 1', 15, 14, 'ACTIVE', 'Mon/Wed/Fri 08:00-09:00', NOW() - INTERVAL '3 months'),
('MTM-CLASS-ACTIVE-02', 'D0000000-0000-0000-0000-000000000001', 'Tahsin 2 - Evening Current', 'Tahsin 2', 12, 11, 'ACTIVE', 'Tue/Thu 19:00-20:00', NOW() - INTERVAL '3 months'),

-- PLANNING term classes (future, in preparation)
('MTM-CLASS-PLANNING-01', 'D0000000-0000-0000-0000-000000000002', 'Tahsin 1 - Planning Next', 'Tahsin 1', 15, 0, 'PLANNING', 'TBD - In Preparation', NOW() - INTERVAL '1 month')
ON CONFLICT (id) DO NOTHING;

-- STEP 5: ENSURE TERM ORDERING DATA
-- Make sure created_at dates support proper term ordering (newest first)
UPDATE academic_terms SET created_at = NOW() - INTERVAL '1 year' WHERE id = 'D0000000-0000-0000-0000-000000000003';
UPDATE academic_terms SET created_at = NOW() - INTERVAL '6 months' WHERE id = 'D0000000-0000-0000-0000-000000000001';  
UPDATE academic_terms SET created_at = NOW() - INTERVAL '3 months' WHERE id = 'D0000000-0000-0000-0000-000000000002';
UPDATE academic_terms SET created_at = NOW() - INTERVAL '1 month' WHERE id = 'D0000000-0000-0000-0000-000000000004';

COMMIT;