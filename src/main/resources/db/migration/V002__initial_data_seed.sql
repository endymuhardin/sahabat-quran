-- =====================================================
-- INITIAL DATA SEED FOR YAYASAN SAHABAT QURAN APPLICATION
-- =====================================================

-- =====================================================
-- 1. INSERT ROLES
-- =====================================================
INSERT INTO roles (id, code, name, description) VALUES
('10000000-0000-0000-0000-000000000001', 'SYSTEM_ADMINISTRATOR', 'System Administrator', 'Technical system administration and maintenance'),
('10000000-0000-0000-0000-000000000002', 'INSTRUCTOR', 'Pengajar', 'Akses untuk pengajar'),
('10000000-0000-0000-0000-000000000003', 'STUDENT', 'Siswa', 'Akses untuk siswa'),
('10000000-0000-0000-0000-000000000004', 'ACADEMIC_ADMIN', 'Academic Administrator', 'Academic operations and student administration'),
('10000000-0000-0000-0000-000000000005', 'FINANCE_STAFF', 'Staf Keuangan', 'Akses untuk staf keuangan'),
('10000000-0000-0000-0000-000000000006', 'MANAGEMENT', 'Manajemen', 'Akses untuk manajemen');

-- =====================================================
-- 2. INSERT PERMISSIONS
-- =====================================================
-- User Management Module
INSERT INTO permissions (code, name, module) VALUES
('USER_VIEW', 'View Users', 'USER_MANAGEMENT'),
('USER_CREATE', 'Create Users', 'USER_MANAGEMENT'),
('USER_EDIT', 'Edit Users', 'USER_MANAGEMENT'),
('USER_DELETE', 'Delete Users', 'USER_MANAGEMENT'),
('USER_ACTIVATE', 'Activate/Deactivate Users', 'USER_MANAGEMENT'),

-- Class Management Module
('CLASS_VIEW', 'View Classes', 'CLASS_MANAGEMENT'),
('CLASS_CREATE', 'Create Classes', 'CLASS_MANAGEMENT'),
('CLASS_EDIT', 'Edit Classes', 'CLASS_MANAGEMENT'),
('CLASS_DELETE', 'Delete Classes', 'CLASS_MANAGEMENT'),
('CLASS_SCHEDULE_MANAGE', 'Manage Class Schedules', 'CLASS_MANAGEMENT'),

-- Enrollment Module
('ENROLLMENT_VIEW', 'View Enrollments', 'ENROLLMENT'),
('ENROLLMENT_CREATE', 'Create Enrollments', 'ENROLLMENT'),
('ENROLLMENT_EDIT', 'Edit Enrollments', 'ENROLLMENT'),
('ENROLLMENT_APPROVE', 'Approve Enrollments', 'ENROLLMENT'),
('ENROLLMENT_CANCEL', 'Cancel Enrollments', 'ENROLLMENT'),

-- Attendance Module
('ATTENDANCE_VIEW', 'View Attendance', 'ATTENDANCE'),
('ATTENDANCE_MARK', 'Mark Attendance', 'ATTENDANCE'),
('ATTENDANCE_EDIT', 'Edit Attendance', 'ATTENDANCE'),
('ATTENDANCE_REPORT', 'View Attendance Reports', 'ATTENDANCE'),

-- Assessment Module
('ASSESSMENT_VIEW', 'View Assessments', 'ASSESSMENT'),
('ASSESSMENT_CREATE', 'Create Assessments', 'ASSESSMENT'),
('ASSESSMENT_EDIT', 'Edit Assessments', 'ASSESSMENT'),
('ASSESSMENT_GRADE', 'Grade Assessments', 'ASSESSMENT'),
('REPORT_CARD_VIEW', 'View Report Cards', 'ASSESSMENT'),
('REPORT_CARD_GENERATE', 'Generate Report Cards', 'ASSESSMENT'),

-- Finance Module
('BILLING_VIEW', 'View Billing', 'FINANCE'),
('BILLING_CREATE', 'Create Billing', 'FINANCE'),
('BILLING_EDIT', 'Edit Billing', 'FINANCE'),
('PAYMENT_VIEW', 'View Payments', 'FINANCE'),
('PAYMENT_VERIFY', 'Verify Payments', 'FINANCE'),
('PAYMENT_RECORD', 'Record Payments', 'FINANCE'),
('FINANCE_REPORT', 'View Finance Reports', 'FINANCE'),
('SALARY_VIEW', 'View Salary Information', 'FINANCE'),
('SALARY_CALCULATE', 'Calculate Salaries', 'FINANCE'),
('SALARY_APPROVE', 'Approve Salaries', 'FINANCE'),

-- Event Management Module
('EVENT_VIEW', 'View Events', 'EVENT_MANAGEMENT'),
('EVENT_CREATE', 'Create Events', 'EVENT_MANAGEMENT'),
('EVENT_EDIT', 'Edit Events', 'EVENT_MANAGEMENT'),
('EVENT_DELETE', 'Delete Events', 'EVENT_MANAGEMENT'),
('EVENT_REGISTER', 'Register for Events', 'EVENT_MANAGEMENT'),
('EVENT_MANAGE_REGISTRATION', 'Manage Event Registrations', 'EVENT_MANAGEMENT'),

-- Reporting Module
('REPORT_OPERATIONAL', 'View Operational Reports', 'REPORTING'),
('REPORT_FINANCIAL', 'View Financial Reports', 'REPORTING'),
('REPORT_ACADEMIC', 'View Academic Reports', 'REPORTING'),
('REPORT_EXPORT', 'Export Reports', 'REPORTING'),
('DASHBOARD_VIEW', 'View Dashboard', 'REPORTING'),

-- System Configuration
('SYSTEM_CONFIG', 'System Configuration', 'SYSTEM'),
('BACKUP_RESTORE', 'Backup and Restore', 'SYSTEM'),
('AUDIT_LOG_VIEW', 'View Audit Logs', 'SYSTEM'),

-- Academic Planning Module
('ACADEMIC_TERM_MANAGE', 'Manage Academic Terms', 'ACADEMIC_PLANNING'),
('ACADEMIC_MONITORING', 'Real-time Academic Monitoring', 'ACADEMIC_PLANNING'),
('TEACHER_AVAILABILITY_VIEW', 'View Teacher Availability', 'ACADEMIC_PLANNING'),
('TEACHER_AVAILABILITY_SUBMIT', 'Submit Teacher Availability', 'ACADEMIC_PLANNING'),
('CLASS_GENERATION_RUN', 'Run Class Generation', 'ACADEMIC_PLANNING'),
('CLASS_GENERATION_REVIEW', 'Review Class Generation', 'ACADEMIC_PLANNING'),
('SCHEDULE_APPROVE', 'Approve Schedules', 'ACADEMIC_PLANNING'),
('SYSTEM_GOLIVE_MANAGE', 'Manage System Go-Live', 'ACADEMIC_PLANNING'),
('TEACHER_LEVEL_ASSIGN', 'Assign Teachers to Levels', 'ACADEMIC_PLANNING'),

-- Feedback System Module
('STUDENT_FEEDBACK_SUBMIT', 'Submit Anonymous Feedback', 'FEEDBACK_SYSTEM'),
('STUDENT_PROGRESS_VIEW', 'View Student Progress', 'STUDENT_PORTAL');

-- =====================================================
-- 3. ASSIGN PERMISSIONS TO ROLES
-- =====================================================
-- SYSTEM_ADMINISTRATOR gets system-level permissions only
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions
WHERE code IN (
    -- System administration
    'SYSTEM_CONFIG', 'BACKUP_RESTORE', 'AUDIT_LOG_VIEW',
    -- User management for system purposes
    'USER_VIEW', 'USER_CREATE', 'USER_EDIT', 'USER_DELETE', 'USER_ACTIVATE',
    -- View-only permissions for monitoring
    'DASHBOARD_VIEW', 'REPORT_OPERATIONAL', 'CLASS_VIEW', 'ENROLLMENT_VIEW'
);

-- INSTRUCTOR permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN (
    'CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_VIEW', 'ATTENDANCE_MARK', 
    'ATTENDANCE_EDIT', 'ASSESSMENT_VIEW', 'ASSESSMENT_CREATE', 'ASSESSMENT_EDIT',
    'ASSESSMENT_GRADE', 'REPORT_CARD_VIEW', 'EVENT_VIEW', 'EVENT_REGISTER',
    'TEACHER_AVAILABILITY_SUBMIT'
);

-- STUDENT permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000003', id FROM permissions
WHERE code IN (
    'CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_VIEW', 'ASSESSMENT_VIEW',
    'REPORT_CARD_VIEW', 'BILLING_VIEW', 'PAYMENT_VIEW', 'EVENT_VIEW', 'EVENT_REGISTER',
    'STUDENT_FEEDBACK_SUBMIT', 'STUDENT_PROGRESS_VIEW'
);

-- ACADEMIC_ADMIN permissions (comprehensive academic operations)
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    -- User management for students and instructors
    'USER_VIEW', 'USER_CREATE', 'USER_EDIT', 'USER_ACTIVATE',
    -- Class management
    'CLASS_VIEW', 'CLASS_CREATE', 'CLASS_EDIT', 'CLASS_DELETE', 'CLASS_SCHEDULE_MANAGE',
    -- Enrollment management
    'ENROLLMENT_VIEW', 'ENROLLMENT_CREATE', 'ENROLLMENT_EDIT', 'ENROLLMENT_APPROVE', 'ENROLLMENT_CANCEL',
    -- Attendance management
    'ATTENDANCE_VIEW', 'ATTENDANCE_REPORT', 'ATTENDANCE_EDIT',
    -- Assessment oversight
    'ASSESSMENT_VIEW', 'ASSESSMENT_CREATE', 'ASSESSMENT_EDIT', 'REPORT_CARD_VIEW', 'REPORT_CARD_GENERATE',
    -- Event management
    'EVENT_VIEW', 'EVENT_CREATE', 'EVENT_EDIT', 'EVENT_DELETE', 'EVENT_MANAGE_REGISTRATION',
    -- Academic planning
    'ACADEMIC_TERM_MANAGE', 'ACADEMIC_MONITORING', 'TEACHER_AVAILABILITY_VIEW', 'CLASS_GENERATION_RUN', 
    'CLASS_GENERATION_REVIEW', 'SCHEDULE_APPROVE', 'SYSTEM_GOLIVE_MANAGE', 'TEACHER_LEVEL_ASSIGN',
    -- Reporting
    'DASHBOARD_VIEW', 'REPORT_ACADEMIC', 'REPORT_OPERATIONAL'
);

-- FINANCE_STAFF permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000005', id FROM permissions
WHERE code IN (
    'BILLING_VIEW', 'BILLING_CREATE', 'BILLING_EDIT', 'PAYMENT_VIEW', 
    'PAYMENT_VERIFY', 'PAYMENT_RECORD', 'FINANCE_REPORT', 'SALARY_VIEW',
    'SALARY_CALCULATE', 'EVENT_VIEW', 'REPORT_FINANCIAL', 'DASHBOARD_VIEW'
);

-- MANAGEMENT permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN (
    'USER_VIEW', 'CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_REPORT',
    'ASSESSMENT_VIEW', 'REPORT_CARD_VIEW', 'BILLING_VIEW', 'PAYMENT_VIEW',
    'FINANCE_REPORT', 'SALARY_VIEW', 'SALARY_APPROVE', 'EVENT_VIEW',
    'REPORT_OPERATIONAL', 'REPORT_FINANCIAL', 'REPORT_ACADEMIC', 'REPORT_EXPORT',
    'DASHBOARD_VIEW', 'AUDIT_LOG_VIEW',
    -- Academic Planning permissions for MANAGEMENT role
    'ACADEMIC_TERM_MANAGE', 'ACADEMIC_MONITORING', 'TEACHER_AVAILABILITY_VIEW', 'CLASS_GENERATION_RUN',
    'CLASS_GENERATION_REVIEW', 'SCHEDULE_APPROVE', 'SYSTEM_GOLIVE_MANAGE',
    'TEACHER_LEVEL_ASSIGN'
);

-- =====================================================
-- 4. INSERT USERS
-- =====================================================
-- System Administrator user
INSERT INTO users (id, username, email, full_name) 
VALUES ('00000000-0000-0000-0000-000000000001', 'sysadmin', 'sysadmin@sahabatquran.com', 'System Administrator');

-- Instructors
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('20000000-0000-0000-0000-000000000001', 'ustadz.ahmad', 'ahmad@sahabatquran.com', 'Ustadz Ahmad Fauzi', '081234567001', true),
('20000000-0000-0000-0000-000000000002', 'ustadzah.fatimah', 'fatimah@sahabatquran.com', 'Ustadzah Fatimah Zahra', '081234567002', true),
('20000000-0000-0000-0000-000000000003', 'ustadz.ibrahim', 'ibrahim@sahabatquran.com', 'Ustadz Ibrahim Al-Hafidz', '081234567003', true);

-- Students
INSERT INTO users (id, username, email, full_name, phone_number, address, is_active) VALUES
('30000000-0000-0000-0000-000000000001', 'siswa.ali', 'ali@gmail.com', 'Ali Rahman', '081234567101', 'Jl. Mawar No. 10, Jakarta', true),
('30000000-0000-0000-0000-000000000002', 'siswa.sarah', 'sarah@gmail.com', 'Sarah Abdullah', '081234567102', 'Jl. Melati No. 15, Jakarta', true),
('30000000-0000-0000-0000-000000000003', 'siswa.umar', 'umar@gmail.com', 'Umar Faruq', '081234567103', 'Jl. Dahlia No. 20, Jakarta', true),
('30000000-0000-0000-0000-000000000004', 'siswa.aisyah', 'aisyah@gmail.com', 'Aisyah Putri', '081234567104', 'Jl. Anggrek No. 25, Jakarta', true),
('30000000-0000-0000-0000-000000000005', 'siswa.bilal', 'bilal@gmail.com', 'Bilal Hamzah', '081234567105', 'Jl. Kenanga No. 30, Jakarta', true);

-- Academic Admin Staff
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('40000000-0000-0000-0000-000000000001', 'academic.admin1', 'academic1@sahabatquran.com', 'Nur Hidayah', '081234567201', true),
('40000000-0000-0000-0000-000000000002', 'academic.admin2', 'academic2@sahabatquran.com', 'Dewi Sartika', '081234567202', true);

-- Finance Staff
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('50000000-0000-0000-0000-000000000001', 'staff.finance1', 'finance1@sahabatquran.com', 'Rahmat Hidayat', '081234567301', true),
('50000000-0000-0000-0000-000000000002', 'staff.finance2', 'finance2@sahabatquran.com', 'Siti Nurjanah', '081234567302', true);

-- Management
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('60000000-0000-0000-0000-000000000001', 'management.director', 'director@sahabatquran.com', 'Dr. H. Muhammad Yusuf, M.A.', '081234567401', true),
('60000000-0000-0000-0000-000000000002', 'management.secretary', 'secretary@sahabatquran.com', 'Hj. Maryam Solehah, S.Pd.I', '081234567402', true);

-- =====================================================
-- 5. INSERT USER CREDENTIALS
-- =====================================================
-- System Administrator credentials 
-- NOTE: Currently using AdminYSQ@2024 hash - needs to be updated to SysAdmin@YSQ2024 hash for production
-- Hash '$2a$10$EvmQlMPJzlnakwVdvMhBo.kpRD4fNkGUQlCwDcB/Hm4UQqddb6tP6' = AdminYSQ@2024
INSERT INTO user_credentials (id_user, password_hash)
VALUES ('00000000-0000-0000-0000-000000000001', '$2a$10$EvmQlMPJzlnakwVdvMhBo.kpRD4fNkGUQlCwDcB/Hm4UQqddb6tP6');

-- All other users (password: Welcome@YSQ2024)
-- BCrypt hash for 'Welcome@YSQ2024': $2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i
INSERT INTO user_credentials (id_user, password_hash) VALUES
-- Instructors
('20000000-0000-0000-0000-000000000001', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('20000000-0000-0000-0000-000000000002', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('20000000-0000-0000-0000-000000000003', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
-- Students
('30000000-0000-0000-0000-000000000001', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000002', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000003', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000004', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('30000000-0000-0000-0000-000000000005', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
-- Admin Staff
('40000000-0000-0000-0000-000000000001', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('40000000-0000-0000-0000-000000000002', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
-- Finance Staff
('50000000-0000-0000-0000-000000000001', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('50000000-0000-0000-0000-000000000002', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
-- Management
('60000000-0000-0000-0000-000000000001', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i'),
('60000000-0000-0000-0000-000000000002', '$2a$10$xHd9h.uiIDUNXEpHjBTQxer7WOjTUCBmZxsXVS3aq5lzXej8G9M/i');

-- =====================================================
-- 6. ASSIGN ROLES TO USERS
-- =====================================================
-- Admin role to admin user
INSERT INTO user_roles (id_user, id_role) VALUES
('00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001');

-- Instructors
INSERT INTO user_roles (id_user, id_role) VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002'),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002');

-- Students
INSERT INTO user_roles (id_user, id_role) VALUES
('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000003');

-- Academic Admin Staff
INSERT INTO user_roles (id_user, id_role) VALUES
('40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000004'),
('40000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000004');

-- Finance Staff
INSERT INTO user_roles (id_user, id_role) VALUES
('50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000005'),
('50000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000005');

-- Management
INSERT INTO user_roles (id_user, id_role) VALUES
('60000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000006'),
('60000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000006');

-- =====================================================
-- 7. INSERT LEVELS
-- =====================================================
INSERT INTO levels (name, description, order_number, competency_level) VALUES
('Tahsin 1', 'Level dasar untuk pemula', 1, 'FOUNDATION'),
('Tahsin 2', 'Level lanjutan tahsin', 2, 'BASIC'),
('Tahsin 3', 'Level mahir tahsin', 3, 'INTERMEDIATE'),
('Tahfidz Pemula', 'Level awal tahfidz', 4, 'ADVANCED'),
('Tahfidz Lanjutan', 'Level lanjutan tahfidz', 5, 'ADVANCED');

-- =====================================================
-- 8. INSERT SAMPLE CLASSES
-- =====================================================
INSERT INTO class_groups (id, name, id_level, id_instructor, capacity, schedule, location, is_active) VALUES
('70000000-0000-0000-0000-000000000001', 'Tahsin 1 - Kelas Pagi A', 
    (SELECT id FROM levels WHERE name = 'Tahsin 1'), 
    '20000000-0000-0000-0000-000000000001', 20, 'Senin & Rabu, 08:00-10:00', 'Ruang A1', true),
('70000000-0000-0000-0000-000000000002', 'Tahsin 1 - Kelas Sore B', 
    (SELECT id FROM levels WHERE name = 'Tahsin 1'), 
    '20000000-0000-0000-0000-000000000002', 20, 'Selasa & Kamis, 16:00-18:00', 'Ruang A2', true),
('70000000-0000-0000-0000-000000000003', 'Tahsin 2 - Kelas Pagi', 
    (SELECT id FROM levels WHERE name = 'Tahsin 2'), 
    '20000000-0000-0000-0000-000000000003', 15, 'Senin & Rabu, 10:00-12:00', 'Ruang B1', true);

-- =====================================================
-- 9. INSERT SAMPLE ENROLLMENTS
-- =====================================================
INSERT INTO enrollments (id_student, id_class_group, enrollment_date, status) VALUES
('30000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000003', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000004', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000005', '70000000-0000-0000-0000-000000000003', CURRENT_DATE, 'ACTIVE');

-- =====================================================
-- STUDENT REGISTRATION SYSTEM SEED DATA
-- =====================================================

-- INSERT PROGRAMS (using level IDs from levels table)
INSERT INTO programs (id, code, name, description, id_level, is_active) VALUES
('80000000-0000-0000-0000-000000000001', 'TAHSIN_1', 'Tahsin Level 1', 'Program tahsin dasar untuk pemula yang belum bisa membaca Al-Quran dengan baik', (SELECT id FROM levels WHERE name = 'Tahsin 1'), true),
('80000000-0000-0000-0000-000000000002', 'TAHSIN_2', 'Tahsin Level 2', 'Program tahsin lanjutan untuk yang sudah bisa membaca Al-Quran namun perlu perbaikan makhorijul huruf', (SELECT id FROM levels WHERE name = 'Tahsin 2'), true),
('80000000-0000-0000-0000-000000000003', 'TAHSIN_3', 'Tahsin Level 3', 'Program tahsin mahir untuk penyempurnaan bacaan dan penguasaan tajwid', (SELECT id FROM levels WHERE name = 'Tahsin 3'), true),
('80000000-0000-0000-0000-000000000004', 'TAHFIDZ_PEMULA', 'Tahfidz Pemula', 'Program menghafal Al-Quran untuk pemula, target 1-5 juz', (SELECT id FROM levels WHERE name = 'Tahfidz Pemula'), true),
('80000000-0000-0000-0000-000000000005', 'TAHFIDZ_LANJUTAN', 'Tahfidz Lanjutan', 'Program menghafal Al-Quran lanjutan, target 6-15 juz', (SELECT id FROM levels WHERE name = 'Tahfidz Lanjutan'), true),
('80000000-0000-0000-0000-000000000006', 'TAHFIDZ_TINGGI', 'Tahfidz Tinggi', 'Program menghafal Al-Quran tingkat tinggi, target 16-30 juz', (SELECT id FROM levels WHERE name = 'Tahfidz Lanjutan'), true);

-- INSERT SESSIONS (SESI 1-7)
INSERT INTO sessions (id, code, name, start_time, end_time, is_active) VALUES
('90000000-0000-0000-0000-000000000001', 'SESI_1', 'Sesi 1 (07-09)', '07:00:00', '09:00:00', true),
('90000000-0000-0000-0000-000000000002', 'SESI_2', 'Sesi 2 (08-10)', '08:00:00', '10:00:00', true),
('90000000-0000-0000-0000-000000000003', 'SESI_3', 'Sesi 3 (09-11)', '09:00:00', '11:00:00', true),
('90000000-0000-0000-0000-000000000004', 'SESI_4', 'Sesi 4 (10-12)', '10:00:00', '12:00:00', true),
('90000000-0000-0000-0000-000000000005', 'SESI_5', 'Sesi 5 (13-15)', '13:00:00', '15:00:00', true),
('90000000-0000-0000-0000-000000000006', 'SESI_6', 'Sesi 6 (14-16)', '14:00:00', '16:00:00', true),
('90000000-0000-0000-0000-000000000007', 'SESI_7', 'Sesi 7 (15-17)', '15:00:00', '17:00:00', true);

-- INSERT PLACEMENT TEST VERSES  
-- Easy verses (Difficulty Level 1)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000001', 1, 'Al-Fatihah', 1, 7, 
'بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ﴿١﴾ ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ ﴿٢﴾ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ﴿٣﴾ مَٰلِكِ يَوْمِ ٱلدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ ﴿٦﴾ صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ ﴿٧﴾',
1, true),

('A0000000-0000-0000-0000-000000000002', 112, 'Al-Ikhlas', 1, 4, 
'قُلْ هُوَ ٱللَّهُ أَحَدٌ ﴿١﴾ ٱللَّهُ ٱلصَّمَدُ ﴿٢﴾ لَمْ يَلِدْ وَلَمْ يُولَدْ ﴿٣﴾ وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ ﴿٤﴾',
1, true),

('A0000000-0000-0000-0000-000000000003', 113, 'Al-Falaq', 1, 5, 
'قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ ﴿١﴾ مِن شَرِّ مَا خَلَقَ ﴿٢﴾ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ﴿٣﴾ وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ ﴿٤﴾ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ﴿٥﴾',
1, true);

-- Medium verses (Difficulty Level 2)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000004', 2, 'Al-Baqarah', 1, 5, 
'ٱلٓمٓ ﴿١﴾ ذَٰلِكَ ٱلْكِتَٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ ﴿٢﴾ ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَٰهُمْ يُنفِقُونَ ﴿٣﴾ وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْءَاخِرَةِ هُمْ يُوقِنُونَ ﴿٤﴾ أُولَٰٓئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ ﴿٥﴾',
2, true),

('A0000000-0000-0000-0000-000000000005', 36, 'Ya-Sin', 1, 5, 
'يٓسٓ ﴿١﴾ وَٱلْقُرْءَانِ ٱلْحَكِيمِ ﴿٢﴾ إِنَّكَ لَمِنَ ٱلْمُرْسَلِينَ ﴿٣﴾ عَلَىٰ صِرَٰطٍ مُّسْتَقِيمٍ ﴿٤﴾ تَنزِيلَ ٱلْعَزِيزِ ٱلرَّحِيمِ ﴿٥﴾',
2, true);

-- Hard verses (Difficulty Level 3)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000006', 2, 'Al-Baqarah', 255, 255, 
'ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ ۚ لَا تَأْخُذُهُۥ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُۥ مَا فِى ٱلسَّمَٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ۗ مَن ذَا ٱلَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَىْءٍ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ ۚ وَسِعَ كُرْسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلْأَرْضَ ۖ وَلَا يَـُٔودُهُۥ حِفْظُهُمَا ۚ وَهُوَ ٱلْعَلِىُّ ٱلْعَظِيمُ',
3, true),

('A0000000-0000-0000-0000-000000000007', 18, 'Al-Kahf', 1, 3, 
'ٱلْحَمْدُ لِلَّهِ ٱلَّذِىٓ أَنزَلَ عَلَىٰ عَبْدِهِ ٱلْكِتَٰبَ وَلَمْ يَجْعَل لَّهُۥ عِوَجًا ۜ ﴿١﴾ قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ ٱلْمُؤْمِنِينَ ٱلَّذِينَ يَعْمَلُونَ ٱلصَّٰلِحَٰتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا ﴿٢﴾ مَّٰكِثِينَ فِيهِ أَبَدًا ﴿ك',
3, true);

-- Very Hard verses (Difficulty Level 4)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000008', 17, 'Al-Isra', 110, 111, 
'قُلِ ٱدْعُوا۟ ٱللَّهَ أَوِ ٱدْعُوا۟ ٱلرَّحْمَٰنَ ۖ أَيًّا مَّا تَدْعُوا۟ فَلَهُ ٱلْأَسْمَآءُ ٱلْحُسْنَىٰ ۚ وَلَا تَجْهَرْ بِصَلَاتِكَ وَلَا تُخَافِتْ بِهَا وَٱبْتَغِ بَيْنَ ذَٰلِكَ سَبِيلًا ﴿١١٠﴾ وَقُلِ ٱلْحَمْدُ لِلَّهِ ٱلَّذِى لَمْ يَتَّخِذْ وَلَدًا وَلَمْ يَكُن لَّهُۥ شَرِيكٌ فِى ٱلْمُلْكِ وَلَمْ يَكُن لَّهُۥ وَلِىٌّ مِّنَ ٱلذُّلِّ ۖ وَكَبِّرْهُ تَكْبِيرًا ﴿١١١﴾',
4, true);

-- Expert verses (Difficulty Level 5)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000009', 33, 'Al-Ahzab', 35, 35, 
'إِنَّ ٱلْمُسْلِمِينَ وَٱلْمُسْلِمَٰتِ وَٱلْمُؤْمِنِينَ وَٱلْمُؤْمِنَٰتِ وَٱلْقَٰنِتِينَ وَٱلْقَٰنِتَٰتِ وَٱلصَّٰدِقِينَ وَٱلصَّٰدِقَٰتِ وَٱلصَّٰبِرِينَ وَٱلصَّٰبِرَٰتِ وَٱلْخَٰشِعِينَ وَٱلْخَٰشِعَٰتِ وَٱلْمُتَصَدِّقِينَ وَٱلْمُتَصَدِّقَٰتِ وَٱلصَّٰٓئِمِينَ وَٱلصَّٰٓئِمَٰتِ وَٱلْحَٰفِظِينَ فُرُوجَهُمْ وَٱلْحَٰفِظَٰتِ وَٱلذَّٰكِرِينَ ٱللَّهَ كَثِيرًا وَٱلذَّٰكِرَٰتِ أَعَدَّ ٱللَّهُ لَهُم مَّغْفِرَةً وَأَجْرًا عَظِيمًا',
5, true);

-- ADD STUDENT REGISTRATION PERMISSIONS
INSERT INTO permissions (code, name, module) VALUES
('STUDENT_REG_VIEW', 'View Student Registrations', 'STUDENT_REGISTRATION'),
('STUDENT_REG_CREATE', 'Create Student Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_EDIT', 'Edit Student Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_ASSIGN_TEACHER', 'Assign Teacher to Review Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_REVIEW', 'Review Student Registration (Teacher)', 'STUDENT_REGISTRATION'),
('STUDENT_REG_EVALUATE', 'Evaluate and Set Level (Teacher)', 'STUDENT_REGISTRATION'),
('PLACEMENT_TEST_EVALUATE', 'Evaluate Placement Test', 'STUDENT_REGISTRATION'),
('PLACEMENT_TEST_MANAGE', 'Manage Placement Test Content', 'STUDENT_REGISTRATION'),
('STUDENT_REG_REPORT', 'View Registration Reports', 'STUDENT_REGISTRATION'),

-- Exam Management Module
('EXAM_VIEW', 'View exams and exam results', 'EXAM_MANAGEMENT'),
('EXAM_CREATE', 'Create new exams', 'EXAM_MANAGEMENT'),
('EXAM_EDIT', 'Edit exam details and questions', 'EXAM_MANAGEMENT'),
('EXAM_DELETE', 'Delete exams (with restrictions)', 'EXAM_MANAGEMENT'),
('EXAM_MANAGE', 'Schedule, activate, complete exams', 'EXAM_MANAGEMENT'),
('EXAM_TAKE', 'Take exams as student', 'EXAM_MANAGEMENT'),
('EXAM_GRADE', 'Grade exam submissions', 'EXAM_MANAGEMENT'),
('EXAM_RESULTS_VIEW', 'View exam statistics and analytics', 'EXAM_MANAGEMENT');

-- ASSIGN STUDENT REGISTRATION PERMISSIONS TO ROLES
-- INSTRUCTOR/TEACHER gets review and evaluation permissions for assigned registrations
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN ('STUDENT_REG_VIEW', 'STUDENT_REG_REVIEW', 'STUDENT_REG_EVALUATE', 'PLACEMENT_TEST_EVALUATE');

-- ADMIN_STAFF gets registration management and teacher assignment permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    'STUDENT_REG_VIEW', 'STUDENT_REG_CREATE', 'STUDENT_REG_EDIT', 
    'STUDENT_REG_ASSIGN_TEACHER', 'STUDENT_REG_REPORT'
);

-- MANAGEMENT gets view, reporting, and teacher assignment permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN ('STUDENT_REG_VIEW', 'STUDENT_REG_ASSIGN_TEACHER', 'STUDENT_REG_REPORT');

-- ASSIGN EXAM PERMISSIONS TO ROLES

-- STUDENTS can take exams
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions
WHERE code IN ('EXAM_TAKE');

-- INSTRUCTORS can manage exams, create questions, grade submissions, view results
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN ('EXAM_VIEW', 'EXAM_CREATE', 'EXAM_EDIT', 'EXAM_MANAGE', 'EXAM_GRADE', 'EXAM_RESULTS_VIEW');

-- ACADEMIC_ADMIN can do everything except take exams
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN ('EXAM_VIEW', 'EXAM_CREATE', 'EXAM_EDIT', 'EXAM_DELETE', 'EXAM_MANAGE', 'EXAM_GRADE', 'EXAM_RESULTS_VIEW');

-- MANAGEMENT can view exams and results for oversight
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN ('EXAM_VIEW', 'EXAM_RESULTS_VIEW');

-- SYSTEM_ADMINISTRATOR gets all exam permissions for system administration
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000005', id FROM permissions
WHERE code IN ('EXAM_VIEW', 'EXAM_CREATE', 'EXAM_EDIT', 'EXAM_DELETE', 'EXAM_MANAGE', 'EXAM_GRADE', 'EXAM_RESULTS_VIEW');

-- =====================================================
-- CLASS PREPARATION SEED DATA
-- =====================================================

-- Insert Academic Terms (using dynamic dates)
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) VALUES
('D0000000-0000-0000-0000-000000000001', 'Semester 1 2024/2025', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '120 days', 'ACTIVE', CURRENT_DATE - INTERVAL '37 days'),
('D0000000-0000-0000-0000-000000000002', 'Semester 2 2024/2025', CURRENT_DATE + INTERVAL '121 days', CURRENT_DATE + INTERVAL '270 days', 'PLANNING', CURRENT_DATE + INTERVAL '115 days'),
('D0000000-0000-0000-0000-000000000003', 'Intensive 2024/2025', CURRENT_DATE + INTERVAL '271 days', CURRENT_DATE + INTERVAL '331 days', 'PLANNING', CURRENT_DATE + INTERVAL '265 days');

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