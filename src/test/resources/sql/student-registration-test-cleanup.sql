-- =====================================================
-- STUDENT REGISTRATION TEST DATA CLEANUP (MANAGEMENT)
-- Removes test data created during management registration workflow tests
-- =====================================================

-- Clean up session preferences first (foreign key constraint)
DELETE FROM student_session_preferences 
WHERE id_registration IN (
    SELECT id FROM student_registrations 
    WHERE full_name LIKE 'TEST_MG_%' 
    OR id IN (
        'b1234567-1111-2222-3333-000000000001'::uuid,
        'b2234567-2222-3333-4444-000000000002'::uuid,
        'b3345678-3333-4444-5555-000000000003'::uuid,
        'b4456789-4444-5555-6666-000000000004'::uuid
    )
);

-- Clean up test registrations
DELETE FROM student_registrations 
WHERE full_name LIKE 'TEST_MG_%' 
OR id IN (
    'b1234567-1111-2222-3333-000000000001'::uuid,
    'b2234567-2222-3333-4444-000000000002'::uuid,
    'b3345678-3333-4444-5555-000000000003'::uuid,
    'b4456789-4444-5555-6666-000000000004'::uuid
);

-- Clean up test user roles
DELETE FROM user_roles 
WHERE id_user IN (
    SELECT id FROM users WHERE username = 'management.director'
);

-- Clean up test user credentials
DELETE FROM user_credentials 
WHERE id_user IN (
    SELECT id FROM users WHERE username = 'management.director' AND full_name = 'TEST Management Director'
);

-- Clean up test management user
DELETE FROM users 
WHERE username = 'management.director' AND full_name = 'TEST Management Director';