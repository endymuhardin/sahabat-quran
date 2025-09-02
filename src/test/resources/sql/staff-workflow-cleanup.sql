-- =====================================================
-- STAFF WORKFLOW TEST DATA CLEANUP
-- Removes test data created during staff workflow tests
-- =====================================================

-- Clean up session preferences first (foreign key constraint)
DELETE FROM student_session_preferences 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE 'TEST_ST_%' OR id = 'a1234567-1111-2222-3333-000000000001'::uuid
);

-- Clean up test registrations (both patterns used in setup)
DELETE FROM student_registrations 
WHERE full_name LIKE 'TEST_ST_%' OR id = 'a1234567-1111-2222-3333-000000000001'::uuid;