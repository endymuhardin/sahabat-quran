-- =======================================================
-- TEST DATA CLEANUP SCRIPT
-- This script cleans up test data to ensure test isolation
-- =======================================================

-- TestContainers PostgreSQL doesn't support session_replication_role
-- Use CASCADE deletes and proper order instead

-- Clean up student registration related data FIRST (proper dependency order)
-- 1. Delete student_session_preferences (references student_registrations) - exclude setup data
DELETE FROM student_session_preferences WHERE id_registration IN (
    SELECT id FROM student_registrations WHERE id::text NOT LIKE 'a0000000%' AND id::text NOT LIKE 'b0000000%' AND id::text NOT LIKE 'c0000000%'
);

-- 2. Delete student_registration_audit (references student_registrations) - exclude setup data  
DELETE FROM student_registration_audit WHERE id_registration IN (
    SELECT id FROM student_registrations WHERE id::text NOT LIKE 'a0000000%' AND id::text NOT LIKE 'b0000000%' AND id::text NOT LIKE 'c0000000%'
);

-- 3. Delete student_registrations (references programs and placement_test_verses) - exclude setup data
DELETE FROM student_registrations WHERE id::text NOT LIKE 'a0000000%' AND id::text NOT LIKE 'b0000000%' AND id::text NOT LIKE 'c0000000%';

-- 4. Clean up placement_test_verses created by tests (exclude both migration data AND setup data)
-- NOTE: Only delete placement verses that are NOT referenced by any remaining student registrations
DELETE FROM placement_test_verses 
WHERE id::text NOT LIKE 'a0000000%' 
  AND id::text NOT LIKE 'b0000000%'
  AND id NOT IN (
    SELECT DISTINCT id_placement_verse 
    FROM student_registrations 
    WHERE id_placement_verse IS NOT NULL
  );

-- NOTE: DO NOT delete sessions and programs - they are setup data needed for tests
-- Setup data in sessions (TEST_SESSION_*) and programs (TEST_PROGRAM_*) should persist across tests
-- Only clean dynamic test-generated records (registrations, preferences, audit logs)

-- NOTE: Do not delete users - service layer no longer creates users (audit functionality removed)
-- Users are created by migration data and should persist across tests
-- All user references in student_registrations are set to null by service layer

-- Cleanup complete - no need to restore session_replication_role in TestContainers