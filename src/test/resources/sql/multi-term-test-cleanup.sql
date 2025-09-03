-- =====================================================
-- MULTI-TERM MANAGEMENT TEST CLEANUP
-- Cleanup test data created for multi-term management scenarios
-- Removes all test-specific data to ensure clean test environment
-- =====================================================

-- STEP 1: REMOVE TEST CLASSES
DELETE FROM classes WHERE id IN (
    'MTM-CLASS-COMPLETED-01',
    'MTM-CLASS-COMPLETED-02', 
    'MTM-CLASS-ACTIVE-01',
    'MTM-CLASS-ACTIVE-02',
    'MTM-CLASS-PLANNING-01'
);

-- STEP 2: REMOVE TEACHER AVAILABILITIES
DELETE FROM teacher_availabilities WHERE id IN (
    'MTM-AVAIL-001',
    'MTM-AVAIL-002'
);

-- STEP 3: REMOVE STUDENT ASSESSMENTS
DELETE FROM student_assessments WHERE id IN (
    'MTM-COMPLETED-001',
    'MTM-COMPLETED-002',
    'MTM-ACTIVE-001', 
    'MTM-ACTIVE-002',
    'MTM-PLANNING-001'
);

-- STEP 4: RESET ACADEMIC TERMS TO ORIGINAL STATE
-- Note: We preserve existing terms but reset any test modifications
UPDATE academic_terms 
SET status = CASE 
    WHEN id = 'D0000000-0000-0000-0000-000000000001' THEN 'ACTIVE'
    WHEN id = 'D0000000-0000-0000-0000-000000000002' THEN 'PLANNING' 
    ELSE status
END
WHERE id IN (
    'D0000000-0000-0000-0000-000000000001',
    'D0000000-0000-0000-0000-000000000002',
    'D0000000-0000-0000-0000-000000000003',
    'D0000000-0000-0000-0000-000000000004'
);

-- STEP 5: CLEANUP ANY ORPHANED TEST DATA
-- Remove any other test data that might have been created during test execution
DELETE FROM class_sessions WHERE class_id IN (
    SELECT id FROM classes WHERE name LIKE '%MTM_%'
);

COMMIT;