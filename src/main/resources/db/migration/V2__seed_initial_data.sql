-- Note: Passwords are plain text ('password') and should be hashed by the application before first use.
-- The UUIDs are hardcoded to ensure stable relationships between entities in the seed data.

-- Create Admin User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Admin User', 'admin', 'password', 'ADMIN', 'admin@sahabatquran.id', true);

-- Create Finance User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Finance User', 'finance', 'password', 'FINANCE', 'finance@sahabatquran.id', true);

-- Create Teacher User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Ustadz Fulan', 'ust.fulan', 'password', 'TEACHER', 'teacher.fulan@sahabatquran.id', true);
-- Create corresponding teacher record
INSERT INTO teachers (id, id_user, address, bio) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Jalan Kenangan No. 10', 'Pengajar Tahsin dan Tahfidz');

-- Create Student User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Fulan bin Fulan', 'fulan.student', 'password', 'STUDENT', 'student.fulan@sahabatquran.id', true);
-- Create corresponding student record
INSERT INTO students (id, id_user, address) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Jalan Impian No. 20');

-- Seed Master Data: Curriculums and Rooms
INSERT INTO curriculums (id, name, description, level) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Tahsin Dasar', 'Perbaikan bacaan Al-Quran dari dasar.', 'Dasar');
INSERT INTO curriculums (id, name, description, level) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Tahfidz Juz 30', 'Menghafal Al-Quran Juz 30.', 'Menengah');

INSERT INTO rooms (id, name, location, capacity) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Ruang A1', 'Lantai 1, Gedung A', 20);
INSERT INTO rooms (id, name, location, capacity) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Ruang B1', 'Lantai 1, Gedung B', 15);

-- Seed Class
INSERT INTO classes (id, name, id_curriculum, id_room) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Kelas Tahsin Pagi', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Seed Class Schedule
INSERT INTO class_schedules (id, id_class, id_teacher, day_of_week, start_time, end_time) VALUES
('g0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Senin', '08:00:00', '09:30:00');
