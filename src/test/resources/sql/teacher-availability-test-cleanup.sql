-- =====================================================
-- TEACHER AVAILABILITY TEST CLEANUP
-- Cleanup test data for Phase 3: Teacher Availability Monitoring
-- =====================================================

-- Step 1: Clean up teacher availability data (test data only)
DELETE FROM teacher_availability WHERE preferences LIKE '%TAVAIL_TEST_%';

-- Step 2: Clean up student assessments (test data only)
DELETE FROM student_assessments WHERE assessment_notes LIKE '%TAVAIL_TEST_%';

-- Step 3: Restore original academic term status
-- Reset to PLANNING (valid status) to avoid affecting other tests
UPDATE academic_terms 
SET status = 'PLANNING', preparation_deadline = NULL
WHERE id = 'D0000000-0000-0000-0000-000000000002';

-- Note: We don't delete existing users/instructors as they are seeded data used across tests
-- We only clean up the test-specific data (teacher availability and student assessments)