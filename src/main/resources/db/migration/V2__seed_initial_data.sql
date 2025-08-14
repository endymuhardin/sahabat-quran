-- Note: Passwords are plain text ('password') and should be hashed by the application before first use.
-- The UUIDs are hardcoded to ensure stable relationships between entities in the seed data.

-- Create Admin User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000001', 'Admin User', 'admin', 'password', 'ADMIN', 'admin@sahabatquran.id', true);

-- Create Finance User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000002', 'Finance User', 'finance', 'password', 'FINANCE', 'finance@sahabatquran.id', true);

-- Create Teacher User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000003', 'Ustadz Fulan', 'ust.fulan', 'password', 'TEACHER', 'teacher.fulan@sahabatquran.id', true);
-- Create corresponding teacher record
INSERT INTO teachers (id, id_user, address, bio) VALUES
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003', 'Jalan Kenangan No. 10', 'Pengajar Tahsin dan Tahfidz');

-- Create Student User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000004', 'Fulan bin Fulan', 'fulan.student', 'password', 'STUDENT', 'student.fulan@sahabatquran.id', true);
-- Create corresponding student record
INSERT INTO students (id, id_user, address) VALUES
('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000004', 'Jalan Impian No. 20');

-- Seed Master Data: Curriculums and Rooms
INSERT INTO curriculums (id, name, description, level) VALUES
('d0000000-0000-0000-0000-000000000001', 'Tahsin Dasar', 'Perbaikan bacaan Al-Quran dari dasar.', 'Dasar');
INSERT INTO curriculums (id, name, description, level) VALUES
('d0000000-0000-0000-0000-000000000002', 'Tahfidz Juz 30', 'Menghafal Al-Quran Juz 30.', 'Menengah');

INSERT INTO rooms (id, name, location, capacity) VALUES
('e0000000-0000-0000-0000-000000000001', 'Ruang A1', 'Lantai 1, Gedung A', 20);
INSERT INTO rooms (id, name, location, capacity) VALUES
('e0000000-0000-0000-0000-000000000002', 'Ruang B1', 'Lantai 1, Gedung B', 15);

-- Seed Class
INSERT INTO classes (id, name, id_curriculum, id_room) VALUES
('f0000000-0000-0000-0000-000000000001', 'Kelas Tahsin Pagi', 'd0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001');

-- Seed Class Schedule
INSERT INTO class_schedules (id, id_class, id_teacher, day_of_week, start_time, end_time) VALUES
('a1000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Senin', '08:00:00', '09:30:00');
