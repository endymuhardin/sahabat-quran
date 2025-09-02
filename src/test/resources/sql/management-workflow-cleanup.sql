-- =====================================================
-- MANAGEMENT WORKFLOW TEST DATA CLEANUP
-- Removes test data created during management workflow tests
-- =====================================================

-- Clean up session preferences first (foreign key constraint)
DELETE FROM student_session_preferences 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE 'TEST_MGW_%' 
    OR id IN (
        'c1234567-1111-2222-3333-000000000001'::uuid,
        'c2234567-2222-3333-4444-000000000002'::uuid,
        'c3345678-3333-4444-5555-000000000003'::uuid
    )
);

-- Clean up test registrations
DELETE FROM student_registrations 
WHERE full_name LIKE 'TEST_MGW_%' 
OR id IN (
    'c1234567-1111-2222-3333-000000000001'::uuid,
    'c2234567-2222-3333-4444-000000000002'::uuid,
    'c3345678-3333-4444-5555-000000000003'::uuid
);

-- Clean up test user roles
DELETE FROM user_roles 
WHERE id_user IN (
    SELECT id FROM users WHERE username IN ('ustadz.ahmad', 'ustadzah.fatimah')
);

-- Clean up test user credentials
DELETE FROM user_credentials 
WHERE id_user IN (
    SELECT id FROM users WHERE username IN ('ustadz.ahmad', 'ustadzah.fatimah')
);

-- Clean up test teacher users
DELETE FROM users 
WHERE username IN ('ustadz.ahmad', 'ustadzah.fatimah') 
AND full_name LIKE 'TEST Ustadz%';