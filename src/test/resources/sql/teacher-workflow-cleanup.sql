-- =====================================================
-- TEACHER WORKFLOW TEST DATA CLEANUP
-- Removes test data created during teacher workflow tests
-- =====================================================

-- Clean up session preferences first (foreign key constraint)
DELETE FROM student_session_preferences 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE '%TEST' OR email LIKE '%.teacher.test@%'
);

-- Clean up audit records
DELETE FROM student_registration_audit 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE '%TEST' OR email LIKE '%.teacher.test@%'
);

-- Clean up test registrations
DELETE FROM student_registrations 
WHERE full_name LIKE '%TEST' OR email LIKE '%.teacher.test@%';