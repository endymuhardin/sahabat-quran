-- =====================================================
-- STUDENT REPORT TEST DATA ADDITIONS
-- =====================================================

-- Add missing student: Fatimah Zahra
INSERT INTO users (id, username, email, full_name, phone_number, address, is_active) VALUES
('30000000-0000-0000-0000-000000000006', 'siswa.fatimah', 'fatimah.zahra@gmail.com', 'Fatimah Zahra', '081234567106', 'Jl. Cempaka No. 35, Jakarta', true);

-- Add user credentials for Fatimah Zahra (password: Welcome@YSQ2024)
INSERT INTO user_credentials (id_user, password_hash) VALUES
('30000000-0000-0000-0000-000000000006', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i');

-- Assign student role to Fatimah Zahra
INSERT INTO user_roles (id_user, id_role) VALUES
('30000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000003');

-- Add missing level: Tahfidz 2
INSERT INTO levels (name, description, order_number, competency_level) VALUES
('Tahfidz 2', 'Level tahfidz menengah - target 6-10 juz', 6, 'ADVANCED');

-- Add class for Tahfidz 2 level
INSERT INTO class_groups (id, name, id_level, id_instructor, capacity, schedule, location, is_active) VALUES
('70000000-0000-0000-0000-000000000004', 'Tahfidz 2 - Kelas Pagi',
    (SELECT id FROM levels WHERE name = 'Tahfidz 2'),
    '20000000-0000-0000-0000-000000000002', 15, 'Selasa & Kamis, 09:00-11:00', 'Ruang C1', true);

-- Enroll Fatimah Zahra in Tahfidz 2 class
INSERT INTO enrollments (id_student, id_class_group, enrollment_date, status) VALUES
('30000000-0000-0000-0000-000000000006', '70000000-0000-0000-0000-000000000004', CURRENT_DATE, 'ACTIVE');

-- Add program for Tahfidz 2 level
INSERT INTO programs (id, code, name, description, id_level, is_active) VALUES
('80000000-0000-0000-0000-000000000007', 'TAHFIDZ_2', 'Tahfidz Level 2', 'Program menghafal Al-Quran tingkat menengah, target 6-10 juz', (SELECT id FROM levels WHERE name = 'Tahfidz 2'), true);

-- Add some additional sample enrollments for testing filters
INSERT INTO enrollments (id_student, id_class_group, enrollment_date, status) VALUES
-- Add Ali Rahman to Tahfidz 2 class as well for testing
('30000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000004', CURRENT_DATE, 'ACTIVE'),
-- Add Sarah Abdullah to Tahsin 2 class for better test coverage
('30000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000003', CURRENT_DATE, 'ACTIVE');