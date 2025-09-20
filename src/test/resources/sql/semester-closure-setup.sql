-- Test data setup for Semester Closure functionality
-- Creates complete academic term with students, classes, and necessary data

-- Ensure we have an active academic term
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, created_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    'Semester 1 2024/2025',
    '2024-08-01',
    '2024-12-20',
    'ACTIVE',
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO UPDATE SET
    status = 'ACTIVE',
    updated_at = CURRENT_TIMESTAMP;

-- Use existing students from seed data (6 students already exist)
-- We'll work with the existing students: siswa.ali, siswa.sarah, siswa.umar, siswa.aisyah, siswa.bilal, siswa.fatimah
-- No need to create new users as they already exist in V002__initial_data_seed.sql

-- Use existing levels from seed data (Tahsin 1, Tahsin 2, Tahsin 3 already exist)

-- Create test class groups (6 classes as per documentation)
-- Use existing instructors, levels, and time slots from seed data
INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, id_time_slot, capacity, is_active, created_at)
VALUES
    (gen_random_uuid(), 'Test Class A', (SELECT id FROM levels WHERE name = 'Tahsin 1'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000001', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'MONDAY' AND s.code = 'SESI_1' LIMIT 1), 10, true, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Test Class B', (SELECT id FROM levels WHERE name = 'Tahsin 2'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'TUESDAY' AND s.code = 'SESI_2' LIMIT 1), 10, true, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Test Class C', (SELECT id FROM levels WHERE name = 'Tahsin 3'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000003', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'WEDNESDAY' AND s.code = 'SESI_3' LIMIT 1), 10, true, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Test Class D', (SELECT id FROM levels WHERE name = 'Tahsin 1'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000001', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'THURSDAY' AND s.code = 'SESI_4' LIMIT 1), 10, true, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Test Class E', (SELECT id FROM levels WHERE name = 'Tahsin 2'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000002', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'FRIDAY' AND s.code = 'SESI_5' LIMIT 1), 10, true, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Test Class F', (SELECT id FROM levels WHERE name = 'Tahsin 3'), '550e8400-e29b-41d4-a716-446655440001', '20000000-0000-0000-0000-000000000003', (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session WHERE ts.day_of_week = 'SATURDAY' AND s.code = 'SESI_6' LIMIT 1), 10, true, CURRENT_TIMESTAMP);

-- Create enrollments for existing students in classes
-- We have 6 students, distribute them across 6 classes (1 student per class for simplicity)
INSERT INTO enrollments (id, id_student, id_class_group, enrollment_date, status, created_at)
SELECT
    gen_random_uuid(),
    s.id,
    c.id,
    '2024-08-15',
    'ACTIVE',
    CURRENT_TIMESTAMP
FROM (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn
    FROM users WHERE username IN ('siswa.ali', 'siswa.sarah', 'siswa.umar', 'siswa.aisyah', 'siswa.bilal', 'siswa.fatimah')
) s
JOIN (
    SELECT id, ROW_NUMBER() OVER (ORDER BY name) as cn
    FROM class_groups WHERE name LIKE 'Test Class%'
) c ON s.rn = c.cn;

-- Create student assessments with grades
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_score, assessment_grade, assessment_date,
    assessment_notes, assessed_by, is_validated, created_at
)
SELECT
    gen_random_uuid(),
    u.id,
    '550e8400-e29b-41d4-a716-446655440001',
    'EXISTING',
    'FINAL',
    80 + (RANDOM() * 20)::numeric(5,2), -- Scores between 80-100
    CASE
        WHEN RANDOM() > 0.8 THEN 'A'
        WHEN RANDOM() > 0.6 THEN 'B'
        WHEN RANDOM() > 0.3 THEN 'C'
        ELSE 'D'
    END,
    '2024-12-15',
    'Final assessment for semester closure test',
    (SELECT id FROM users WHERE username = 'academic.admin1' LIMIT 1),
    true,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.username IN ('siswa.ali', 'siswa.sarah', 'siswa.umar', 'siswa.aisyah', 'siswa.bilal', 'siswa.fatimah');

-- Create class sessions for attendance tracking
INSERT INTO class_sessions (
    id, id_class_group, session_date, session_number,
    preparation_status, id_instructor, created_at
)
SELECT
    gen_random_uuid(),
    cg.id,
    gs.session_date,
    ROW_NUMBER() OVER (PARTITION BY cg.id ORDER BY gs.session_date),
    'COMPLETED',
    cg.id_instructor,
    CURRENT_TIMESTAMP
FROM class_groups cg
CROSS JOIN generate_series('2024-08-01'::date, '2024-12-15'::date, '1 week'::interval) AS gs(session_date)
WHERE cg.name LIKE 'Test Class%';

-- Create attendance records
-- Note: The attendance table uses id_enrollment, not direct id_student/id_class_session
-- We need to create attendance based on enrollments
INSERT INTO attendance (
    id, id_enrollment, attendance_date,
    is_present, notes, created_at
)
SELECT
    gen_random_uuid(),
    e.id,  -- enrollment id, not student id
    cs.session_date,
    CASE WHEN RANDOM() > 0.15 THEN true ELSE false END, -- 85% attendance rate
    CASE WHEN RANDOM() < 0.15 THEN 'Absent' ELSE NULL END,
    CURRENT_TIMESTAMP
FROM enrollments e
JOIN class_sessions cs ON cs.id_class_group = e.id_class_group;

-- Create teacher attendance records
INSERT INTO teacher_attendance (
    id, id_class_session, id_scheduled_instructor, is_present,
    arrival_time, departure_time, created_at
)
SELECT
    gen_random_uuid(),
    cs.id,
    cg.id_instructor,
    CASE WHEN RANDOM() > 0.05 THEN true ELSE false END, -- 95% teacher attendance
    CASE WHEN RANDOM() > 0.05 THEN cs.session_date + '08:45:00'::time ELSE NULL END,
    CASE WHEN RANDOM() > 0.05 THEN cs.session_date + '11:00:00'::time ELSE NULL END,
    CURRENT_TIMESTAMP
FROM class_sessions cs
JOIN class_groups cg ON cg.id = cs.id_class_group;

-- Ensure we have required users
UPDATE users SET is_active = true, updated_at = CURRENT_TIMESTAMP
WHERE username IN ('academic.admin1', 'management.director');

-- Refresh statistics
ANALYZE users;
ANALYZE enrollments;
ANALYZE student_assessments;
ANALYZE attendance;
ANALYZE class_sessions;