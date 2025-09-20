-- Test data setup for Semester Closure with incomplete data
-- Creates academic term with missing/incomplete student data for testing error scenarios

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

-- Use existing students from seed data (no need to create new ones)
-- We'll use the existing students: siswa.ali, siswa.sarah, siswa.umar, siswa.aisyah, siswa.bilal, siswa.fatimah

-- Use existing levels from seed data (Tahsin 1, Tahsin 2, Tahsin 3, etc. already exist)

-- Create test class groups using existing levels, instructors, and time slots
INSERT INTO class_groups (id, name, id_level, id_term, id_instructor, id_time_slot, capacity, is_active, created_at)
SELECT
    gen_random_uuid(),
    'Incomplete Class ' || chr(64 + generate_series),
    (ARRAY[
        (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1),
        (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1),
        (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1)
    ])[((generate_series - 1) % 3) + 1],
    '550e8400-e29b-41d4-a716-446655440001',
    (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1), -- Use existing instructor from seed data
    (SELECT ts.id FROM time_slot ts JOIN sessions s ON s.id = ts.id_session LIMIT 1 OFFSET (generate_series - 1) % 3), -- Use any available time slot
    10,
    true,
    CURRENT_TIMESTAMP
FROM generate_series(1, 3); -- Only 3 classes

-- Create enrollments with existing students (some missing for incomplete data scenario)
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
    FROM users WHERE username IN ('siswa.ali', 'siswa.sarah', 'siswa.umar') -- Only 3 of 6 students enrolled (incomplete data)
) s
CROSS JOIN (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) as cn
    FROM class_groups WHERE name LIKE 'Incomplete Class%'
) c
WHERE s.rn = c.cn; -- One student per class

-- Create INCOMPLETE student assessments (missing grades for some students)
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
    CASE WHEN RANDOM() > 0.3 THEN 80 + (RANDOM() * 20)::numeric(5,2) ELSE NULL END, -- 30% missing scores
    CASE
        WHEN RANDOM() > 0.3 THEN
            CASE
                WHEN RANDOM() > 0.8 THEN 'A'
                WHEN RANDOM() > 0.6 THEN 'B'
                WHEN RANDOM() > 0.3 THEN 'C'
                ELSE 'D'
            END
        ELSE NULL -- 30% missing grades
    END,
    CASE WHEN RANDOM() > 0.2 THEN '2024-12-15'::date ELSE NULL END, -- 20% missing assessment dates
    'Incomplete assessment for error testing',
    (SELECT id FROM users WHERE username = 'academic.admin1' LIMIT 1),
    CASE WHEN RANDOM() > 0.4 THEN true ELSE false END, -- 40% not validated
    CURRENT_TIMESTAMP
FROM users u
WHERE u.username IN ('siswa.ali', 'siswa.sarah') -- Only 2 of 3 enrolled students have assessments (incomplete data)
AND RANDOM() > 0.2; -- Some assessments missing scores/grades

-- Create fewer class sessions (incomplete session data)
INSERT INTO class_sessions (
    id, id_class_group, session_date, id_instructor,
    session_status, created_at
)
SELECT
    gen_random_uuid(),
    cg.id,
    generate_series('2024-08-01'::date, '2024-11-30'::date, '2 weeks'::interval)::date, -- Every 2 weeks instead of weekly
    cg.id_instructor, -- Use same instructor as class group
    CASE WHEN RANDOM() > 0.3 THEN 'COMPLETED' ELSE 'SCHEDULED' END, -- 30% not completed
    CURRENT_TIMESTAMP
FROM class_groups cg
WHERE cg.name LIKE 'Incomplete Class%';

-- Skip attendance records for this incomplete data test scenario
-- (Attendance entity structure is different - requires enrollment relationship)
-- The purpose is to test incomplete data, not full data population

-- Ensure required users exist
UPDATE users SET is_active = true, updated_at = CURRENT_TIMESTAMP
WHERE username IN ('academic.admin1', 'management.director');

ANALYZE users;
ANALYZE enrollments;
ANALYZE student_assessments;
ANALYZE class_sessions;