-- Term Preparation Documentation Test Setup
-- Sets up test data for demonstrating the complete term preparation workflow

-- Clear existing test data (including previous runs)
DELETE FROM class_sessions WHERE id_class_group IN (
    SELECT cg.id FROM class_groups cg 
    JOIN academic_terms at ON cg.id_term = at.id 
    WHERE at.term_name LIKE 'TEST_%'
);
DELETE FROM enrollments WHERE id_class_group IN (
    SELECT cg.id FROM class_groups cg 
    JOIN academic_terms at ON cg.id_term = at.id 
    WHERE at.term_name LIKE 'TEST_%'
);
DELETE FROM class_groups WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Clear teacher availability for our test term AND the instructor we'll use in the test
-- This ensures the instructor (ustadz.ahmad) has no existing availability for our test term
DELETE FROM teacher_availability WHERE id_term = '11111111-1111-1111-1111-111111111111';
DELETE FROM teacher_availability WHERE id_teacher = (SELECT id FROM users WHERE username = 'ustadz.ahmad') 
    AND id_term = '11111111-1111-1111-1111-111111111111';

DELETE FROM teacher_level_assignments WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');
DELETE FROM student_assessments WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');
DELETE FROM academic_terms WHERE term_name LIKE 'TEST_%';

-- Update existing seed terms to have future preparation deadlines for testing
UPDATE academic_terms 
SET preparation_deadline = CURRENT_DATE + INTERVAL '6 months'
WHERE id IN ('d0000000-0000-0000-0000-000000000002', 'd0000000-0000-0000-0000-000000000003');

-- Create test academic term - this should appear in instructor availability dropdown
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline, created_at, updated_at)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'TEST_Semester 1 2024/2025', '2024-07-01', '2024-12-31', 'PLANNING', CURRENT_DATE + INTERVAL '1 year', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    term_name = EXCLUDED.term_name,
    start_date = EXCLUDED.start_date, 
    end_date = EXCLUDED.end_date,
    status = EXCLUDED.status,
    preparation_deadline = EXCLUDED.preparation_deadline,
    updated_at = NOW();

-- Verify test term was inserted  
SELECT CASE 
    WHEN EXISTS (SELECT 1 FROM academic_terms WHERE id = '11111111-1111-1111-1111-111111111111' AND status = 'PLANNING') 
    THEN 'SUCCESS: Test term inserted with PLANNING status'
    ELSE 'FAILED: Test term not found or not PLANNING'
END AS verification_result;

-- Create test users for student assessments
INSERT INTO users (id, username, email, full_name, phone_number, is_active) VALUES
('22111111-1111-1111-1111-111111111111', 'test.student.ahmad', 'ahmad@test.com', 'TEST Ahmad', '08123456001', true),
('22111111-1111-1111-1111-111111111112', 'test.student.fatimah', 'fatimah@test.com', 'TEST Fatimah', '08123456002', true),
('22111111-1111-1111-1111-111111111113', 'test.student.ibrahim', 'ibrahim@test.com', 'TEST Ibrahim', '08123456003', true),
('22111111-1111-1111-1111-111111111114', 'test.student.khadijah', 'khadijah@test.com', 'TEST Khadijah', '08123456004', true),
('22111111-1111-1111-1111-111111111115', 'test.student.omar', 'omar@test.com', 'TEST Omar', '08123456005', true)
ON CONFLICT (username) DO NOTHING;

-- Create test student assessments using proper schema
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type, 
    assessment_score, determined_level, assessment_date, assessment_notes, 
    assessed_by, is_validated
) VALUES 
('21111111-1111-1111-1111-111111111111', '22111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'NEW', 'PLACEMENT', 85, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '5 days', 'TEST_Good performance', (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1), true),
('21111111-1111-1111-1111-111111111112', '22111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'NEW', 'PLACEMENT', 92, (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), NOW() - INTERVAL '5 days', 'TEST_Excellent performance', (SELECT id FROM users WHERE username = 'ustadzah.fatimah' LIMIT 1), true),
('21111111-1111-1111-1111-111111111113', '22111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', 'NEW', 'PLACEMENT', 65, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), NOW() - INTERVAL '5 days', 'TEST_Basic level', (SELECT id FROM users WHERE username = 'ustadz.ahmad' LIMIT 1), true),
('21111111-1111-1111-1111-111111111114', '22111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', 'NEW', 'PLACEMENT', 88, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '4 days', 'TEST_Very good', (SELECT id FROM users WHERE username = 'ustadzah.fatimah' LIMIT 1), true),
('21111111-1111-1111-1111-111111111115', '22111111-1111-1111-1111-111111111115', '11111111-1111-1111-1111-111111111111', 'NEW', 'PLACEMENT', 95, (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), NOW() - INTERVAL '4 days', 'TEST_Outstanding', (SELECT id FROM users WHERE username = 'ustadz.ibrahim' LIMIT 1), true)
ON CONFLICT (id) DO NOTHING;

-- NOTE: No pre-existing teacher availability data inserted
-- This functional test demonstrates the first-time availability submission workflow
-- Starting with clean state allows testing the complete submission flow

-- Create sample teacher level assignments
INSERT INTO teacher_level_assignments (id, id_teacher, id_level, id_term, competency_level, max_classes_for_level, specialization, assigned_by)
SELECT
    gen_random_uuid(),
    u.id,
    l.id,
    '11111111-1111-1111-1111-111111111111',
    CASE 
        WHEN u.username = 'ustadz.ahmad' THEN 'SENIOR'
        WHEN u.username = 'ustadzah.zahra' THEN 'EXPERT'
        ELSE 'JUNIOR'
    END,
    CASE 
        WHEN l.name = 'Tahsin 1' THEN 3
        WHEN l.name = 'Tahsin 2' THEN 2
        ELSE 1
    END,
    CASE 
        WHEN l.name = 'Tahsin 1' AND u.username = 'ustadz.ahmad' THEN 'FOUNDATION'
        WHEN l.name = 'Tahsin 1' AND u.username = 'ustadzah.zahra' THEN 'REMEDIAL'
        ELSE 'MIXED'
    END,
    (SELECT id FROM users WHERE username = 'management.director' LIMIT 1)
FROM users u
CROSS JOIN levels l
WHERE u.username IN ('ustadz.ahmad', 'ustadzah.zahra')
  AND l.name IN ('Tahsin 1', 'Tahsin 2')
  AND random() > 0.3; -- Assign some but not all combinations

-- Create sample generated class proposals (for refinement phase demo)
INSERT INTO generated_class_proposals (id, id_term, generation_run, proposal_data, optimization_score, conflict_count, generated_by, generated_at, is_approved)
VALUES (
    '31111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    1,
    '{
        "classes": [
            {
                "id": "class_1",
                "level": "Tahsin 1",
                "teacher": "Ustadz Ahmad",
                "schedule": "Senin 08:00-10:00",
                "students": 8,
                "student_mix": {"new": 3, "existing": 5},
                "room": "Ruang A"
            },
            {
                "id": "class_2",
                "level": "Tahsin 1",
                "teacher": "Ustadzah Zahra",
                "schedule": "Rabu 19:00-21:00",
                "students": 9,
                "student_mix": {"new": 4, "existing": 5},
                "room": "Ruang B"
            },
            {
                "id": "class_3",
                "level": "Tahsin 2",
                "teacher": "Ustadz Ahmad",
                "schedule": "Jumat 16:00-18:00",
                "students": 7,
                "student_mix": {"new": 3, "existing": 4},
                "room": "Ruang A"
            }
        ],
        "summary": {
            "total_classes": 3,
            "total_students": 24,
            "avg_class_size": 8.0,
            "teacher_workload": {
                "Ustadz Ahmad": 2,
                "Ustadzah Zahra": 1
            }
        }
    }'::jsonb,
    85.5,
    0,
    (SELECT id FROM users WHERE username = 'academic.admin1' LIMIT 1),
    NOW() - INTERVAL '2 hours',
    false
);

-- Add class size configuration (skip if already exists)
INSERT INTO class_size_configuration (id, config_key, config_value, level_id, updated_by, updated_at)
VALUES
    (gen_random_uuid(), 'default.min', 7, NULL, (SELECT id FROM users WHERE username = 'sysadmin' LIMIT 1), NOW()),
    (gen_random_uuid(), 'default.max', 10, NULL, (SELECT id FROM users WHERE username = 'sysadmin' LIMIT 1), NOW()),
    (gen_random_uuid(), 'tahsin1.min', 8, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), (SELECT id FROM users WHERE username = 'sysadmin' LIMIT 1), NOW()),
    (gen_random_uuid(), 'tahsin1.max', 12, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), (SELECT id FROM users WHERE username = 'sysadmin' LIMIT 1), NOW())
ON CONFLICT (config_key) DO NOTHING;

-- Log summary
SELECT 'Term Preparation Test Data Setup Complete' AS status,
       (SELECT COUNT(*) FROM academic_terms WHERE term_name LIKE 'TEST_%') AS test_terms,
       (SELECT COUNT(*) FROM student_assessments WHERE id_term = '11111111-1111-1111-1111-111111111111') AS test_assessments,
       (SELECT COUNT(*) FROM teacher_availability WHERE id_term = '11111111-1111-1111-1111-111111111111') AS availability_records,
       (SELECT COUNT(*) FROM teacher_level_assignments WHERE id_term = '11111111-1111-1111-1111-111111111111') AS level_assignments;