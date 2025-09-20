-- Cleanup script for Semester Closure tests
-- Removes test data created for semester closure testing

-- Clean up report generation data
DELETE FROM report_generation_items WHERE id_batch IN (
    SELECT id FROM report_generation_batches WHERE id_term = '550e8400-e29b-41d4-a716-446655440001'
);

DELETE FROM report_generation_batches WHERE id_term = '550e8400-e29b-41d4-a716-446655440001';

-- Clean up attendance data (only for our test term)
DELETE FROM attendance WHERE id_enrollment IN (
    SELECT e.id FROM enrollments e
    JOIN class_groups cg ON cg.id = e.id_class_group
    WHERE cg.id_term = '550e8400-e29b-41d4-a716-446655440001'
);

DELETE FROM teacher_attendance WHERE id_class_session IN (
    SELECT cs.id FROM class_sessions cs
    JOIN class_groups cg ON cg.id = cs.id_class_group
    WHERE cg.id_term = '550e8400-e29b-41d4-a716-446655440001'
);

-- Clean up class sessions
DELETE FROM class_sessions WHERE id_class_group IN (
    SELECT id FROM class_groups WHERE id_term = '550e8400-e29b-41d4-a716-446655440001'
);

-- Clean up student assessments
DELETE FROM student_assessments WHERE id_term = '550e8400-e29b-41d4-a716-446655440001';

-- Clean up enrollments
DELETE FROM enrollments WHERE id_class_group IN (
    SELECT id FROM class_groups WHERE id_term = '550e8400-e29b-41d4-a716-446655440001'
);

-- Clean up class groups
DELETE FROM class_groups WHERE id_term = '550e8400-e29b-41d4-a716-446655440001';

-- No need to clean up users or user_roles as we're using existing seed data users

-- No need to clean up levels as we're using existing seed data levels

-- Clean up test term
DELETE FROM academic_terms WHERE id = '550e8400-e29b-41d4-a716-446655440001';

-- Refresh statistics
ANALYZE users;
ANALYZE enrollments;
ANALYZE student_assessments;
ANALYZE attendance;
ANALYZE class_sessions;