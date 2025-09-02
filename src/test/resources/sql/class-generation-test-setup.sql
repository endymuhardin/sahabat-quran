-- =====================================================
-- CLASS GENERATION TEST SETUP
-- Setup test data for Phase 5: Class Generation & Refinement
-- =====================================================

-- Insert test academic term (if not exists)
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) 
VALUES (
    '80000000-0000-0000-0000-000000000001', 
    'CGEN_TEST_Term_2024A', 
    '2024-09-01', 
    '2024-12-31', 
    'PLANNING', 
    '2024-08-15'
) ON CONFLICT (id) DO NOTHING;

-- Insert test students with CGEN_TEST_ prefix
INSERT INTO users (id, username, email, full_name, is_active) VALUES
('94000000-0001-0000-0000-000000000001', 'CGEN_TEST_student1', 'cgen_test_s1@ysq.test', 'CGEN_TEST Student Ali Hassan', true),
('94000000-0001-0000-0000-000000000002', 'CGEN_TEST_student2', 'cgen_test_s2@ysq.test', 'CGEN_TEST Student Maryam Ahmed', true),
('94000000-0001-0000-0000-000000000003', 'CGEN_TEST_student3', 'cgen_test_s3@ysq.test', 'CGEN_TEST Student Yusuf Ibrahim', true),
('94000000-0001-0000-0000-000000000004', 'CGEN_TEST_student4', 'cgen_test_s4@ysq.test', 'CGEN_TEST Student Khadijah Omar', true)
ON CONFLICT (id) DO NOTHING;

-- Assign student role to test users
INSERT INTO user_roles (id_user, id_role) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username LIKE 'CGEN_TEST_%' AND r.name = 'STUDENT'
ON CONFLICT DO NOTHING;

-- Insert test class size configuration
INSERT INTO class_size_configuration (id, config_key, config_value, level_id, updated_by, updated_at) VALUES
('95000000-0001-0000-0000-000000000001', 'CGEN_TEST_default.min', 7, NULL, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()),
('95000000-0001-0000-0000-000000000002', 'CGEN_TEST_default.max', 10, NULL, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()),
('95000000-0001-0000-0000-000000000003', 'CGEN_TEST_tahsin1.min', 8, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()),
('95000000-0001-0000-0000-000000000004', 'CGEN_TEST_tahsin1.max', 12, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert test generated class proposal
INSERT INTO generated_class_proposals (id, id_term, generation_run, proposal_data, optimization_score, conflict_count, generated_by, generated_at, is_approved) VALUES
('96000000-0001-0000-0000-000000000001', 
 '80000000-0000-0000-0000-000000000001', 
 1, 
 '{"classes": [{"id": "class1", "name": "CGEN_TEST Class A", "level": "Tahsin 1", "students": 8}], "generation_params": {"test": true}}', 
 85.5, 
 0, 
 (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 
 NOW(), 
 false
)
ON CONFLICT (id) DO NOTHING;