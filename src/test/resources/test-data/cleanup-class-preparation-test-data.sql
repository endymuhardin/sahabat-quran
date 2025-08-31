-- =====================================================
-- CLEANUP TEST DATA FOR CLASS PREPARATION TESTS
-- =====================================================

-- Clean up test data in reverse dependency order
DELETE FROM class_generation_log WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM class_preparation_checklist 
WHERE id_session IN (
    SELECT cs.id FROM class_sessions cs 
    JOIN class_groups c ON cs.id_class_group = c.id 
    WHERE c.id_term = '90000000-0000-0000-0000-000000000001'
);

DELETE FROM session_materials 
WHERE id_session IN (
    SELECT cs.id FROM class_sessions cs 
    JOIN class_groups c ON cs.id_class_group = c.id 
    WHERE c.id_term = '90000000-0000-0000-0000-000000000001'
);

DELETE FROM class_sessions 
WHERE id_class_group IN (
    SELECT id FROM class_groups WHERE id_term = '90000000-0000-0000-0000-000000000001'
);

DELETE FROM enrollments 
WHERE id_class_group IN (
    SELECT id FROM class_groups WHERE id_term = '90000000-0000-0000-0000-000000000001'
);

-- Delete student assessments before class groups due to foreign key constraint
DELETE FROM student_assessments WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM class_groups WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM generated_class_proposals WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM teacher_level_assignments WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM teacher_availability WHERE id_term = '90000000-0000-0000-0000-000000000001';

DELETE FROM class_size_configuration WHERE config_key LIKE 'test.%';

DELETE FROM user_roles WHERE id_user::text LIKE '92000000-0000-0000-0000-%';

DELETE FROM user_credentials WHERE id_user::text LIKE '92000000-0000-0000-0000-%';

DELETE FROM users WHERE id::text LIKE '92000000-0000-0000-0000-%';

DELETE FROM levels WHERE id::text LIKE '91000000-0000-0000-0000-%';

DELETE FROM academic_terms WHERE id = '90000000-0000-0000-0000-000000000001';

-- Clean up any entities with TEST_DATA_ marker
DELETE FROM class_groups WHERE name LIKE '%TEST_DATA_%';
DELETE FROM users WHERE username LIKE '%TEST_DATA_%';
DELETE FROM users WHERE email LIKE '%TEST_DATA_%';
DELETE FROM levels WHERE name LIKE '%TEST_DATA_%';
DELETE FROM academic_terms WHERE term_name LIKE '%TEST_DATA_%';