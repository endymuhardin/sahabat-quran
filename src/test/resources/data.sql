-- Create Admin User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000001', 'Admin User', 'admin', 'password', 'ADMIN', 'admin@sahabatquran.id', true);

-- Create Finance User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000002', 'Finance User', 'finance', 'password', 'FINANCE', 'finance@sahabatquran.id', true);

-- Create Teacher User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000003', 'Ustadz Fulan', 'ust.fulan', 'password', 'TEACHER', 'teacher.fulan@sahabatquran.id', true);

-- Create Student User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000004', 'Fulan bin Fulan', 'fulan.student', 'password', 'STUDENT', 'student.fulan@sahabatquran.id', true);
