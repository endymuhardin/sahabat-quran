-- =====================================================
-- CLASS PREPARATION WORKFLOW TEST CLEANUP
-- Remove all test data created for class preparation workflow tests
-- =====================================================

-- Clean up in reverse dependency order using business keys

-- Step 1: Remove class preparation checklists (using session references)
DELETE FROM class_preparation_checklist 
WHERE id_session IN (
    SELECT id FROM class_sessions 
    WHERE session_date = '2025-02-03' 
    AND id_class_group IN ('70000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000003')
);

-- Step 2: Remove session materials (using session references)
DELETE FROM session_materials 
WHERE id_session IN (
    SELECT id FROM class_sessions 
    WHERE session_date = '2025-02-03' 
    AND id_class_group IN ('70000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000003')
);

-- Step 3: Remove class sessions (using business date and class references)
DELETE FROM class_sessions 
WHERE session_date = '2025-02-03' 
AND id_class_group IN ('70000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000003');

-- Step 4: Remove generated class proposals (using term and generation_run)
DELETE FROM generated_class_proposals 
WHERE id_term = 'D0000000-0000-0000-0000-000000000002' 
AND generation_run IN (1, 2);

-- Step 5: Remove class size configuration (using config keys)
DELETE FROM class_size_configuration 
WHERE config_key IN ('default.min', 'default.max', 'tahsin1.min', 'tahsin1.max', 'tahsin2.min', 'tahsin2.max')
AND updated_by = '40000000-0000-0000-0000-000000000001';

-- Step 6: Remove teacher level assignments (using term and teachers)
DELETE FROM teacher_level_assignments 
WHERE id_term = 'D0000000-0000-0000-0000-000000000002'
AND id_teacher IN ('20000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003');

-- Step 7: Remove teacher availability data (using preferences containing test marker)
-- Also remove any availability data for ustadz.ahmad to avoid conflicts with validation tests
DELETE FROM teacher_availability 
WHERE (preferences LIKE '%CPW_TEST_%' OR id_teacher = '20000000-0000-0000-0000-000000000001')
AND id_term = 'D0000000-0000-0000-0000-000000000002';

-- Step 8: Remove student assessments (using assessment_notes containing test marker)
DELETE FROM student_assessments 
WHERE assessment_notes LIKE '%CPW_TEST_%' 
AND id_term = 'D0000000-0000-0000-0000-000000000002';

-- Step 9: Reset academic term status
UPDATE academic_terms 
SET status = 'ACTIVE', preparation_deadline = '2024-08-25'
WHERE id = 'D0000000-0000-0000-0000-000000000002';