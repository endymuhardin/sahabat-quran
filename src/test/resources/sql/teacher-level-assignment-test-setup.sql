-- =====================================================
-- TEACHER LEVEL ASSIGNMENT TEST SETUP
-- Setup test data for Phase 4: Management Level Assignment
-- =====================================================

-- Insert test academic term (if not exists)
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) 
VALUES (
    '80000000-0000-0000-0000-000000000001', 
    'TLEVEL_TEST_Term_2024A', 
    '2024-09-01', 
    '2024-12-31', 
    'PLANNING', 
    '2024-08-15'
) ON CONFLICT (id) DO NOTHING;

-- Insert test teachers with TLEVEL_TEST_ prefix
INSERT INTO users (id, username, email, full_name, is_active) VALUES
('92000000-0001-0000-0000-000000000001', 'TLEVEL_TEST_teacher1', 'tlevel_test_t1@ysq.test', 'TLEVEL_TEST Teacher Sarah Ahmed', true),
('92000000-0001-0000-0000-000000000002', 'TLEVEL_TEST_teacher2', 'tlevel_test_t2@ysq.test', 'TLEVEL_TEST Teacher Ibrahim Malik', true),
('92000000-0001-0000-0000-000000000003', 'TLEVEL_TEST_teacher3', 'tlevel_test_t3@ysq.test', 'TLEVEL_TEST Teacher Aisha Rahman', true),
('92000000-0001-0000-0000-000000000004', 'TLEVEL_TEST_teacher4', 'tlevel_test_t4@ysq.test', 'TLEVEL_TEST Teacher Ustaz Mahmud', true)
ON CONFLICT (id) DO NOTHING;

-- Assign instructor role to test teachers
INSERT INTO user_roles (id_user, id_role) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username LIKE 'TLEVEL_TEST_%' AND r.name = 'INSTRUCTOR'
ON CONFLICT DO NOTHING;

-- Insert test teacher level assignments
INSERT INTO teacher_level_assignments (id, id_teacher, id_level, id_term, competency_level, max_classes_for_level, specialization, assigned_by, assigned_at) VALUES
('93000000-0001-0000-0000-000000000001', '92000000-0001-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), '80000000-0000-0000-0000-000000000001', 'SENIOR', 6, 'FOUNDATION', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()),
('93000000-0001-0000-0000-000000000002', '92000000-0001-0000-0000-000000000002', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), '80000000-0000-0000-0000-000000000001', 'EXPERT', 5, 'MIXED', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()),
('93000000-0001-0000-0000-000000000003', '92000000-0001-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), '80000000-0000-0000-0000-000000000001', 'EXPERT', 4, 'ADVANCED', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW())
ON CONFLICT (id) DO NOTHING;