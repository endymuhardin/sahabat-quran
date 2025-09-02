-- =====================================================
-- TEACHER LEVEL ASSIGNMENT TEST CLEANUP
-- Cleanup test data for Phase 4: Management Level Assignment
-- =====================================================

-- Delete test teacher level assignments
DELETE FROM teacher_level_assignments WHERE id_teacher IN (
    SELECT id FROM users WHERE username LIKE 'TLEVEL_TEST_%'
);

-- Delete test user roles for teachers
DELETE FROM user_roles WHERE id_user IN (
    SELECT id FROM users WHERE username LIKE 'TLEVEL_TEST_%'
);

-- Delete test teachers
DELETE FROM users WHERE username LIKE 'TLEVEL_TEST_%';

-- Note: We don't delete levels or academic terms as they might be used by other tests