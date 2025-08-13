-- Generate UUIDs for permissions
-- Using a deterministic approach for predictability if needed, but here just random
-- In a real project, you might use a script to generate these and check them in
-- For this example, we'll use hardcoded UUIDs.

-- Seed Permissions
INSERT INTO permissions (id, name) VALUES
('00000000-0000-0000-0000-000000000001', 'user:read'),
('00000000-0000-0000-0000-000000000002', 'user:write'),
('00000000-0000-0000-0000-000000000003', 'course:read'),
('00000000-0000-0000-0000-000000000004', 'course:write'),
('00000000-0000-0000-0000-000000000005', 'finance:read'),
('00000000-0000-0000-0000-000000000006', 'finance:write'),
('00000000-0000-0000-0000-000000000007', 'academic:read'),
('00000000-0000-0000-0000-000000000008', 'academic:write');

-- Seed Roles
INSERT INTO roles (id, name) VALUES
('10000000-0000-0000-0000-000000000001', 'student'),
('10000000-0000-0000-0000-000000000002', 'teacher'),
('10000000-0000-0000-0000-000000000003', 'finance'),
('10000000-0000-0000-0000-000000000004', 'academic');

-- Assign Permissions to Roles
-- Student
INSERT INTO roles_permissions (id_role, id_permission) VALUES
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001'), -- user:read
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003'); -- course:read

-- Teacher
INSERT INTO roles_permissions (id_role, id_permission) VALUES
('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001'), -- user:read
('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003'), -- course:read
('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004'); -- course:write

-- Finance
INSERT INTO roles_permissions (id_role, id_permission) VALUES
('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005'), -- finance:read
('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000006'); -- finance:write

-- Academic
INSERT INTO roles_permissions (id_role, id_permission) VALUES
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001'), -- user:read
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002'), -- user:write
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000003'), -- course:read
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004'), -- course:write
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000007'), -- academic:read
('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000008'); -- academic:write


-- Seed Users (passwords should be hashed in a real app, e.g., with BCrypt)
-- For simplicity, using plain text. In a real app, the application would handle hashing.
INSERT INTO users (id, username, password) VALUES
('20000000-0000-0000-0000-000000000001', 'student.user', 'password'),
('20000000-0000-0000-0000-000000000002', 'teacher.user', 'password'),
('20000000-0000-0000-0000-000000000003', 'finance.user', 'password'),
('20000000-0000-0000-0000-000000000004', 'academic.user', 'password');

-- Assign Roles to Users
INSERT INTO users_roles (id_user, id_role) VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001'), -- student.user -> student
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002'), -- teacher.user -> teacher
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003'), -- finance.user -> finance
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004'); -- academic.user -> academic
