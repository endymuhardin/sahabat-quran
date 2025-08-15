INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Admin User', 'admin', 'password', 'ADMIN', 'admin@sahabatquran.id', true),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Finance User', 'finance', 'password', 'FINANCE', 'finance@sahabatquran.id', true),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Ustadz Fulan', 'ust.fulan', 'password', 'TEACHER', 'teacher.fulan@sahabatquran.id', true),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Fulan bin Fulan', 'fulan.student', 'password', 'STUDENT', 'student.fulan@sahabatquran.id', true);

INSERT INTO teachers (id, id_user, address, bio) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', '123 Teacher St', 'Bio of Ustadz Fulan');

INSERT INTO students (id, id_user, address) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '456 Student Ave');
