-- =====================================================
-- STAFF WORKFLOW TEST DATA CLEANUP
-- Removes test data created during staff workflow tests
-- =====================================================

-- Clean up session preferences first (foreign key constraint)
DELETE FROM student_session_preferences 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE 'TEST_ST_%'
);

-- Clean up teacher assignment audit records
DELETE FROM teacher_assignment_audit 
WHERE registration_id IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE 'TEST_ST_%'
);

-- Clean up test registrations
DELETE FROM student_registrations 
WHERE full_name LIKE 'TEST_STAFF_%';