-- =====================================================
-- CLASS GENERATION TEST CLEANUP
-- Cleanup test data for Phase 5: Class Generation & Refinement
-- =====================================================

-- Delete test generated class proposals
DELETE FROM generated_class_proposals WHERE id_term = '80000000-0000-0000-0000-000000000001' AND generation_run = 1;

-- Delete test class size configuration
DELETE FROM class_size_configuration WHERE config_key LIKE 'CGEN_TEST_%';

-- Delete test user roles for students
DELETE FROM user_roles WHERE id_user IN (
    SELECT id FROM users WHERE username LIKE 'CGEN_TEST_%'
);

-- Delete test students
DELETE FROM users WHERE username LIKE 'CGEN_TEST_%';

-- Note: We don't delete levels or academic terms as they might be used by other tests