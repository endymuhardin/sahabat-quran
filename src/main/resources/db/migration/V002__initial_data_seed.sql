-- =====================================================
-- INITIAL DATA SEED FOR YAYASAN SAHABAT QURAN APPLICATION
-- =====================================================

-- =====================================================
-- 1. INSERT ROLES
-- =====================================================
INSERT INTO roles (id, code, name, description) VALUES
('10000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrator', 'Full system access'),
('10000000-0000-0000-0000-000000000002', 'INSTRUCTOR', 'Pengajar', 'Akses untuk pengajar'),
('10000000-0000-0000-0000-000000000003', 'STUDENT', 'Siswa', 'Akses untuk siswa'),
('10000000-0000-0000-0000-000000000004', 'ADMIN_STAFF', 'Staf Administrasi', 'Akses untuk staf administrasi'),
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
('AUDIT_LOG_VIEW', 'View Audit Logs', 'SYSTEM');

-- =====================================================
-- 3. ASSIGN PERMISSIONS TO ROLES
-- =====================================================
-- ADMIN gets all permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions;

-- INSTRUCTOR permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN (
    'CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_VIEW', 'ATTENDANCE_MARK', 
    'ATTENDANCE_EDIT', 'ASSESSMENT_VIEW', 'ASSESSMENT_CREATE', 'ASSESSMENT_EDIT',
    'ASSESSMENT_GRADE', 'REPORT_CARD_VIEW', 'EVENT_VIEW', 'EVENT_REGISTER'
);

-- STUDENT permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000003', id FROM permissions
WHERE code IN (
    'CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_VIEW', 'ASSESSMENT_VIEW',
    'REPORT_CARD_VIEW', 'BILLING_VIEW', 'PAYMENT_VIEW', 'EVENT_VIEW', 'EVENT_REGISTER'
);

-- ADMIN_STAFF permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    'USER_VIEW', 'USER_CREATE', 'USER_EDIT', 'USER_ACTIVATE',
    'CLASS_VIEW', 'CLASS_CREATE', 'CLASS_EDIT', 'CLASS_SCHEDULE_MANAGE',
    'ENROLLMENT_VIEW', 'ENROLLMENT_CREATE', 'ENROLLMENT_EDIT', 'ENROLLMENT_APPROVE',
    'ATTENDANCE_VIEW', 'ATTENDANCE_REPORT', 'ASSESSMENT_VIEW', 'REPORT_CARD_VIEW',
    'EVENT_VIEW', 'EVENT_CREATE', 'EVENT_EDIT', 'EVENT_MANAGE_REGISTRATION',
    'DASHBOARD_VIEW'
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
    'DASHBOARD_VIEW', 'AUDIT_LOG_VIEW'
);

-- =====================================================
-- 4. INSERT USERS
-- =====================================================
-- Admin user
INSERT INTO users (id, username, email, full_name) 
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@sahabatquran.com', 'Administrator');

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

-- Admin Staff
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('40000000-0000-0000-0000-000000000001', 'staff.admin1', 'admin1@sahabatquran.com', 'Nur Hidayah', '081234567201', true),
('40000000-0000-0000-0000-000000000002', 'staff.admin2', 'admin2@sahabatquran.com', 'Dewi Sartika', '081234567202', true);

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
-- Admin credentials (password: AdminYSQ@2024)
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

-- Admin Staff
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
INSERT INTO levels (name, description, order_number) VALUES
('Tahsin 1', 'Level dasar untuk pemula', 1),
('Tahsin 2', 'Level lanjutan tahsin', 2),
('Tahsin 3', 'Level mahir tahsin', 3),
('Tahfidz Pemula', 'Level awal tahfidz', 4),
('Tahfidz Lanjutan', 'Level lanjutan tahfidz', 5);

-- =====================================================
-- 8. INSERT SAMPLE CLASSES
-- =====================================================
INSERT INTO classes (id, name, id_level, id_instructor, capacity, schedule, location, is_active) VALUES
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
INSERT INTO enrollments (id_student, id_class, enrollment_date, status) VALUES
('30000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000001', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000003', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000004', '70000000-0000-0000-0000-000000000002', CURRENT_DATE, 'ACTIVE'),
('30000000-0000-0000-0000-000000000005', '70000000-0000-0000-0000-000000000003', CURRENT_DATE, 'ACTIVE');