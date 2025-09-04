-- =====================================================
-- TEST DATA SETUP FOR CLASS PREPARATION INTEGRATION TESTS
-- =====================================================

-- Create test academic term
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) 
VALUES ('90000000-0000-0000-0000-000000000001', 'TEST_TERM_2024', '2024-06-01', '2024-11-30', 'PLANNING', '2024-05-15');

-- Create additional test levels if not exist
INSERT INTO levels (id, name, description, order_number) 
VALUES 
('91000000-0000-0000-0000-000000000001', 'TEST_LEVEL_BASIC', 'Test basic level', 1),
('91000000-0000-0000-0000-000000000002', 'TEST_LEVEL_INTERMEDIATE', 'Test intermediate level', 2),
('91000000-0000-0000-0000-000000000003', 'TEST_LEVEL_ADVANCED', 'Test advanced level', 3)
ON CONFLICT (name) DO NOTHING;

-- Create test users for different roles
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
-- Test teachers
('92000000-0000-0000-0000-000000000001', 'TEST_TEACHER_1', 'test.teacher1@example.com', 'Test Teacher One', '081234567890', true),
('92000000-0000-0000-0000-000000000002', 'TEST_TEACHER_2', 'test.teacher2@example.com', 'Test Teacher Two', '081234567891', true),
('92000000-0000-0000-0000-000000000003', 'TEST_TEACHER_3', 'test.teacher3@example.com', 'Test Teacher Three', '081234567892', true),
-- Test students
('92000000-0000-0000-0000-000000000101', 'TEST_STUDENT_1', 'test.student1@example.com', 'Test Student One', '081234567901', true),
('92000000-0000-0000-0000-000000000102', 'TEST_STUDENT_2', 'test.student2@example.com', 'Test Student Two', '081234567902', true),
('92000000-0000-0000-0000-000000000103', 'TEST_STUDENT_3', 'test.student3@example.com', 'Test Student Three', '081234567903', true),
('92000000-0000-0000-0000-000000000104', 'TEST_STUDENT_4', 'test.student4@example.com', 'Test Student Four', '081234567904', true),
('92000000-0000-0000-0000-000000000105', 'TEST_STUDENT_5', 'test.student5@example.com', 'Test Student Five', '081234567905', true),
-- Test admin staff
('92000000-0000-0000-0000-000000000201', 'TEST_ADMIN_1', 'test.admin1@example.com', 'Test Admin One', '081234567910', true)
ON CONFLICT (username) DO NOTHING;

-- Create user credentials for test users (password: TestPass123!)
-- BCrypt hash for 'TestPass123!': $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
INSERT INTO user_credentials (id_user, password_hash) VALUES
('92000000-0000-0000-0000-000000000001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000101', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000102', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000103', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000104', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000105', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('92000000-0000-0000-0000-000000000201', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.')
ON CONFLICT (id_user) DO NOTHING;

-- Assign roles to test users
INSERT INTO user_roles (id_user, id_role) VALUES
-- Teachers
('92000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002'),
('92000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002'),
('92000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002'),
-- Students  
('92000000-0000-0000-0000-000000000101', '10000000-0000-0000-0000-000000000003'),
('92000000-0000-0000-0000-000000000102', '10000000-0000-0000-0000-000000000003'),
('92000000-0000-0000-0000-000000000103', '10000000-0000-0000-0000-000000000003'),
('92000000-0000-0000-0000-000000000104', '10000000-0000-0000-0000-000000000003'),
('92000000-0000-0000-0000-000000000105', '10000000-0000-0000-0000-000000000003'),
-- Academic Admin
('92000000-0000-0000-0000-000000000201', '10000000-0000-0000-0000-000000000004')
ON CONFLICT (id_user, id_role) DO NOTHING;

-- Create test class size configurations
INSERT INTO class_size_configuration (config_key, config_value, level_id, description) VALUES
('test.basic.min', 5, '91000000-0000-0000-0000-000000000001', 'Test basic level minimum'),
('test.basic.max', 15, '91000000-0000-0000-0000-000000000001', 'Test basic level maximum'),
('test.intermediate.min', 7, '91000000-0000-0000-0000-000000000002', 'Test intermediate level minimum'),
('test.intermediate.max', 12, '91000000-0000-0000-0000-000000000002', 'Test intermediate level maximum'),
('test.advanced.min', 4, '91000000-0000-0000-0000-000000000003', 'Test advanced level minimum'),
('test.advanced.max', 8, '91000000-0000-0000-0000-000000000003', 'Test advanced level maximum')
ON CONFLICT (config_key) DO NOTHING;

-- Create test teacher availability using session IDs instead of enum
INSERT INTO teacher_availability (id_teacher, id_term, day_of_week, id_session, is_available, capacity, max_classes_per_week, preferences) VALUES
-- Teacher 1 - Morning person
('92000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'Prefers morning sessions'),
('92000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_4'), true, 2, 6, 'Prefers morning sessions'),
('92000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'Prefers morning sessions'),
('92000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'Prefers morning sessions'),
('92000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'THURSDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'Prefers morning sessions'),
-- Teacher 2 - Flexible  
('92000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 1, 5, 'Flexible schedule'),
('92000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_7'), true, 1, 5, 'Flexible schedule'),
('92000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'Flexible schedule'),
('92000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_7'), true, 1, 5, 'Flexible schedule'),
-- Teacher 3 - Evening person
('92000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000001', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_7'), true, 1, 4, 'Prefers evening sessions'),
('92000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000001', 'THURSDAY', (SELECT id FROM sessions WHERE code = 'SESI_7'), true, 1, 4, 'Prefers evening sessions'),
('92000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000001', 'SATURDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 1, 4, 'Prefers evening sessions'),
('92000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000001', 'SATURDAY', (SELECT id FROM sessions WHERE code = 'SESI_7'), true, 1, 4, 'Prefers evening sessions')
ON CONFLICT (id_teacher, id_term, day_of_week, id_session) DO NOTHING;

-- Create test teacher level assignments
INSERT INTO teacher_level_assignments (id_teacher, id_level, id_term, competency_level, max_classes_for_level, specialization, assigned_by, notes) VALUES
('92000000-0000-0000-0000-000000000001', '91000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'SENIOR', 3, 'FOUNDATION', '92000000-0000-0000-0000-000000000201', 'Excellent with beginners'),
('92000000-0000-0000-0000-000000000001', '91000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'SENIOR', 2, 'ADVANCED', '92000000-0000-0000-0000-000000000201', 'Good progression teaching'),
('92000000-0000-0000-0000-000000000002', '91000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'JUNIOR', 2, 'MIXED', '92000000-0000-0000-0000-000000000201', 'Developing mixed class skills'),
('92000000-0000-0000-0000-000000000002', '91000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'JUNIOR', 3, 'MIXED', '92000000-0000-0000-0000-000000000201', 'Standard intermediate teaching'),
('92000000-0000-0000-0000-000000000003', '91000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', 'EXPERT', 2, 'REMEDIAL', '92000000-0000-0000-0000-000000000201', 'Specializes in remedial teaching'),
('92000000-0000-0000-0000-000000000003', '91000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000001', 'EXPERT', 2, 'ADVANCED', '92000000-0000-0000-0000-000000000201', 'Advanced level expertise')
ON CONFLICT (id_teacher, id_level, id_term) DO NOTHING;