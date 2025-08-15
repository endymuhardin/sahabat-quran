-- Create Student User
INSERT INTO users (id, fullname, username, password, role, email, is_active) VALUES
('a0000000-0000-0000-0000-000000000005', 'Student Test', 'student.test', 'password', 'STUDENT', 'student.test@sahabatquran.id', true);

-- Create Student
INSERT INTO students (id, id_user, address) VALUES
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000005', 'Student Address');

-- Create Invoices for the student
INSERT INTO invoices (id, id_student, amount, due_date, status, type) VALUES
('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 100.00, '2025-01-01', 'PENDING', 'TUITION'),
('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 200.00, '2025-02-01', 'PAID', 'TUITION'),
('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 50.00, '2025-03-01', 'PENDING', 'EVENT');
