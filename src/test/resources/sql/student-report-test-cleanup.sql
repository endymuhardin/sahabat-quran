-- Student Report Test Cleanup SQL
-- This script cleans up test data after Student Report functional tests

-- ==============================================
-- CLEAN UP REPORT GENERATION DATA FIRST
-- ==============================================

-- Delete report generation items (must be deleted before batches due to foreign key)
DELETE FROM report_generation_items
WHERE id_student IN (
  '30000000-0000-0000-0000-000000000001',  -- Ali Rahman
  '30000000-0000-0000-0000-000000000002',  -- Sarah Abdullah
  '30000000-0000-0000-0000-000000000003',  -- Muhammad Yusuf
  '30000000-0000-0000-0000-000000000004',  -- Aminah Binti Ahmad
  '30000000-0000-0000-0000-000000000005',  -- Hassan Ibrahim
  '30000000-0000-0000-0000-000000000006',  -- Fatimah Zahra
  '30000000-0000-0000-0000-000000000007',  -- Ahmad Zaki
  '30000000-0000-0000-0000-000000000008'   -- Siti Khadijah
);

-- Delete report generation batches created during tests
DELETE FROM report_generation_batches
WHERE id_term = 'D0000000-0000-0000-0000-000000000001';

-- ==============================================
-- RESTORE CLASS GROUP TERM ASSOCIATION
-- ==============================================

-- Reset the class group term association (set to null as it was originally)
UPDATE class_groups
SET id_term = NULL
WHERE id = '70000000-0000-0000-0000-000000000004';

-- ==============================================
-- 1. CLEAN UP STUDENT ASSESSMENTS
-- ==============================================

-- Delete all test assessments for the current term
DELETE FROM student_assessments
WHERE id_term = 'D0000000-0000-0000-0000-000000000001'
  AND id_student IN (
    '30000000-0000-0000-0000-000000000001',  -- Ali Rahman
    '30000000-0000-0000-0000-000000000002',  -- Sarah Abdullah
    '30000000-0000-0000-0000-000000000006',  -- Fatimah Zahra
    '30000000-0000-0000-0000-000000000007',  -- Ahmad Zaki
    '30000000-0000-0000-0000-000000000008'   -- Siti Khadijah
  );

-- ==============================================
-- 2. CLEAN UP ADDITIONAL TEST STUDENTS
-- ==============================================

-- Remove enrollments for additional test students
DELETE FROM enrollments
WHERE id_student IN (
  '30000000-0000-0000-0000-000000000007',
  '30000000-0000-0000-0000-000000000008'
);

-- Remove user roles for additional test students
DELETE FROM user_roles
WHERE id_user IN (
  '30000000-0000-0000-0000-000000000007',
  '30000000-0000-0000-0000-000000000008'
);

-- Remove credentials for additional test students
DELETE FROM user_credentials
WHERE id_user IN (
  '30000000-0000-0000-0000-000000000007',
  '30000000-0000-0000-0000-000000000008'
);

-- Remove additional test students (these were added by this test)
DELETE FROM users
WHERE id IN (
  '30000000-0000-0000-0000-000000000007',
  '30000000-0000-0000-0000-000000000008'
);

-- Note: We don't delete the core seed students (Ali Rahman, Sarah Abdullah, Fatimah Zahra)
-- as they are part of the base seed data and may be used by other tests