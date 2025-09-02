-- =====================================================
-- FINAL REVIEW GO-LIVE TEST SETUP
-- Setup test data for Phase 6: Final Schedule Review & System Go-Live
-- =====================================================

-- Insert test academic term (if not exists)
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) 
VALUES (
    '80000000-0000-0000-0000-000000000001', 
    'FRGOL_TEST_Term_2024A', 
    '2024-09-01', 
    '2024-12-31', 
    'PLANNING', 
    '2024-08-15'
) ON CONFLICT (id) DO NOTHING;

-- Insert test classes with FRGOL_TEST_ prefix
INSERT INTO class_groups (id, class_name, id_level, id_instructor, max_students, current_enrollment, schedule_day, schedule_time, id_term, status) VALUES
('97000000-0001-0000-0000-000000000001', 'FRGOL_TEST_Tahsin1_A', (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 10, 8, 'SENIN', 'PAGI', '80000000-0000-0000-0000-000000000001', 'DRAFT'),
('97000000-0001-0000-0000-000000000002', 'FRGOL_TEST_Tahsin2_B', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 10, 7, 'SELASA', 'SORE', '80000000-0000-0000-0000-000000000001', 'DRAFT'),
('97000000-0001-0000-0000-000000000003', 'FRGOL_TEST_Tahfidz_C', (SELECT id FROM levels WHERE name LIKE 'Tahfidz%' LIMIT 1), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 6, 5, 'RABU', 'MALAM', '80000000-0000-0000-0000-000000000001', 'DRAFT')
ON CONFLICT (id) DO NOTHING;

-- Insert test class sessions
INSERT INTO class_sessions (id, id_class, session_date, learning_objectives, teaching_materials, preparation_status, id_instructor, created_at, updated_at) VALUES
('98000000-0001-0000-0000-000000000001', '97000000-0001-0000-0000-000000000001', '2024-09-02', ARRAY['FRGOL_TEST Basic Arabic letters'], '{"materials": ["test_book.pdf"], "test": true}', 'READY', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()),
('98000000-0001-0000-0000-000000000002', '97000000-0001-0000-0000-000000000002', '2024-09-03', ARRAY['FRGOL_TEST Intermediate recitation'], '{"materials": ["test_audio.mp3"], "test": true}', 'IN_PROGRESS', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()),
('98000000-0001-0000-0000-000000000003', '97000000-0001-0000-0000-000000000003', '2024-09-04', ARRAY['FRGOL_TEST Memorization techniques'], '{"materials": ["test_guide.pdf"], "test": true}', 'DRAFT', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert test class preparation checklist
INSERT INTO class_preparation_checklist (id, id_session, checklist_item, is_completed, completed_at, completed_by) VALUES
('99000000-0001-0000-0000-000000000001', '98000000-0001-0000-0000-000000000001', 'FRGOL_TEST Student Profile Review', true, NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1)),
('99000000-0001-0000-0000-000000000002', '98000000-0001-0000-0000-000000000001', 'FRGOL_TEST Curriculum Planning', true, NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1)),
('99000000-0001-0000-0000-000000000003', '98000000-0001-0000-0000-000000000002', 'FRGOL_TEST Teaching Strategy Development', false, NULL, NULL),
('99000000-0001-0000-0000-000000000004', '98000000-0001-0000-0000-000000000003', 'FRGOL_TEST Assessment Preparation', false, NULL, NULL)
ON CONFLICT (id) DO NOTHING;