-- =====================================================
-- CLASS PREPARATION WORKFLOW COMPREHENSIVE TEST SETUP
-- Setup complete test data for end-to-end class preparation workflow
-- Covers all phases from assessment to go-live with realistic data
-- =====================================================

-- STEP 1: STUDENT ASSESSMENTS FOR NEW STUDENTS (PLACEMENT TESTS)
-- Create comprehensive placement test data to meet 80%+ threshold
INSERT INTO student_assessments (
    id, id_student, id_term, student_category, assessment_type,
    assessment_score, determined_level, assessment_date, 
    assessment_notes, assessed_by, is_validated, created_at
) VALUES
-- New Students - Placement Test Results
('12345678-1234-5678-9abc-000000000001', '30000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 85, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '5 days', 'CPW_TEST_Excellent pronunciation and fluency', '20000000-0000-0000-0000-000000000001', true, NOW() - INTERVAL '6 days'),
('12345678-1234-5678-9abc-000000000002', '30000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 75, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), NOW() - INTERVAL '5 days', 'CPW_TEST_Good reading skills, minor improvements needed', '20000000-0000-0000-0000-000000000001', true, NOW() - INTERVAL '6 days'),
('12345678-1234-5678-9abc-000000000003', '30000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 92, (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), NOW() - INTERVAL '5 days', 'CPW_TEST_Outstanding performance, ready for advanced level', '20000000-0000-0000-0000-000000000002', true, NOW() - INTERVAL '6 days'),
('12345678-1234-5678-9abc-000000000004', '30000000-0000-0000-0000-000000000004', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 68, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), NOW() - INTERVAL '5 days', 'CPW_TEST_Basic level with room for improvement', '20000000-0000-0000-0000-000000000002', true, NOW() - INTERVAL '6 days'),
('12345678-1234-5678-9abc-000000000005', '30000000-0000-0000-0000-000000000005', 'D0000000-0000-0000-0000-000000000002', 'NEW', 'PLACEMENT', 88, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), NOW() - INTERVAL '5 days', 'CPW_TEST_Very good progress, suitable for intermediate', '20000000-0000-0000-0000-000000000003', true, NOW() - INTERVAL '6 days')
-- Note: 5/5 new students assessed (100% completion for testing)
ON CONFLICT (id) DO NOTHING;

-- STEP 2: ACADEMIC TERM STATUS UPDATE
-- Set term to PLANNING status to enable teacher availability collection
UPDATE academic_terms 
SET status = 'PLANNING', 
    preparation_deadline = CURRENT_DATE + INTERVAL '30 days'
WHERE id = 'D0000000-0000-0000-0000-000000000002';

-- STEP 3: TEACHER AVAILABILITY DATA
-- Comprehensive teacher availability matrix for all instructors
INSERT INTO teacher_availability (
    id, id_teacher, id_term, day_of_week, id_session,
    is_available, capacity, max_classes_per_week, preferences, submitted_at
) VALUES
-- Teacher 1: ustadz.ahmad (Morning specialist)
('22345678-2234-5678-9abc-000000000001', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_1'), true, 2, 6, 'CPW_TEST_Early morning preference', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000002', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'CPW_TEST_Morning preference', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000003', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'CPW_TEST_Morning preference', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000004', '20000000-0000-0000-0000-000000000001', 'D0000000-0000-0000-0000-000000000002', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_2'), true, 2, 6, 'CPW_TEST_Morning preference', NOW() - INTERVAL '3 days'),

-- Teacher 2: ustadzah.fatimah (Evening specialist)
('22345678-2234-5678-9abc-000000000005', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'CPW_TEST_Evening specialist', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000006', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'CPW_TEST_Evening specialist', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000007', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 1, 5, 'CPW_TEST_Evening specialist', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000008', '20000000-0000-0000-0000-000000000002', 'D0000000-0000-0000-0000-000000000002', 'THURSDAY', (SELECT id FROM sessions WHERE code = 'SESI_6'), true, 1, 5, 'CPW_TEST_Evening specialist', NOW() - INTERVAL '3 days'),

-- Teacher 3: ustadz.ibrahim (Flexible availability)
('22345678-2234-5678-9abc-000000000009', '20000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'MONDAY', (SELECT id FROM sessions WHERE code = 'SESI_3'), true, 2, 6, 'CPW_TEST_Flexible schedule', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000010', '20000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'TUESDAY', (SELECT id FROM sessions WHERE code = 'SESI_3'), true, 2, 6, 'CPW_TEST_Flexible schedule', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000011', '20000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'WEDNESDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 2, 6, 'CPW_TEST_Flexible schedule', NOW() - INTERVAL '3 days'),
('22345678-2234-5678-9abc-000000000012', '20000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000002', 'THURSDAY', (SELECT id FROM sessions WHERE code = 'SESI_5'), true, 2, 6, 'CPW_TEST_Flexible schedule', NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- STEP 4: TEACHER LEVEL ASSIGNMENTS
-- Management-level competency and level assignments
INSERT INTO teacher_level_assignments (
    id, id_teacher, id_level, id_term, 
    competency_level, max_classes_for_level, specialization, 
    assigned_by, assigned_at
) VALUES
-- Teacher 1: Foundation specialist
('32345678-3234-5678-9abc-000000000001', '20000000-0000-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'SENIOR', 4, 'FOUNDATION', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),
('32345678-3234-5678-9abc-000000000002', '20000000-0000-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'SENIOR', 3, 'MIXED', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),

-- Teacher 2: Advanced specialist
('32345678-3234-5678-9abc-000000000003', '20000000-0000-0000-0000-000000000002', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'EXPERT', 3, 'ADVANCED', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),
('32345678-3234-5678-9abc-000000000004', '20000000-0000-0000-0000-000000000002', (SELECT id FROM levels WHERE name = 'Tahsin 3' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'EXPERT', 2, 'ADVANCED', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),

-- Teacher 3: Mixed capability
('32345678-3234-5678-9abc-000000000005', '20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'JUNIOR', 2, 'REMEDIAL', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),
('32345678-3234-5678-9abc-000000000006', '20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), 'D0000000-0000-0000-0000-000000000002', 'JUNIOR', 2, 'MIXED', '60000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

-- STEP 5: CLASS SIZE CONFIGURATION
-- System-wide class size parameters
INSERT INTO class_size_configuration (
    id, config_key, config_value, level_id, 
    updated_by, updated_at
) VALUES
('42345678-4234-5678-9abc-000000000001', 'default.min', 7, NULL, '40000000-0000-0000-0000-000000000001', NOW()),
('42345678-4234-5678-9abc-000000000002', 'default.max', 10, NULL, '40000000-0000-0000-0000-000000000001', NOW()),
('42345678-4234-5678-9abc-000000000003', 'tahsin1.min', 8, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), '40000000-0000-0000-0000-000000000001', NOW()),
('42345678-4234-5678-9abc-000000000004', 'tahsin1.max', 12, (SELECT id FROM levels WHERE name = 'Tahsin 1' LIMIT 1), '40000000-0000-0000-0000-000000000001', NOW()),
('42345678-4234-5678-9abc-000000000005', 'tahsin2.min', 7, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), '40000000-0000-0000-0000-000000000001', NOW()),
('42345678-4234-5678-9abc-000000000006', 'tahsin2.max', 10, (SELECT id FROM levels WHERE name = 'Tahsin 2' LIMIT 1), '40000000-0000-0000-0000-000000000001', NOW())
ON CONFLICT (id) DO NOTHING;

-- STEP 6: GENERATED CLASS PROPOSALS
-- Sample auto-generated class proposals for testing refinement
INSERT INTO generated_class_proposals (
    id, id_term, generation_run, proposal_data, 
    optimization_score, conflict_count, size_violations, manual_overrides,
    generated_by, generated_at, is_approved
) VALUES
('52345678-5234-5678-9abc-000000000001', 'D0000000-0000-0000-0000-000000000002', 1, 
'{"classes": [{"level": "Tahsin 1", "teacher": "ustadz.ahmad", "students": 8, "schedule": "MON-WED 08:00", "room": "A1"}, {"level": "Tahsin 2", "teacher": "ustadzah.fatimah", "students": 7, "schedule": "MON-TUE 16:00", "room": "B1"}], "total_students": 15, "total_classes": 2}',
90.5, 0, '{}', '{}',
'40000000-0000-0000-0000-000000000001', NOW() - INTERVAL '1 day', false),
('52345678-5234-5678-9abc-000000000002', 'D0000000-0000-0000-0000-000000000002', 2, 
'{"classes": [{"level": "Tahsin 1", "teacher": "ustadz.ahmad", "students": 9, "schedule": "MON-WED 08:00", "room": "A1"}, {"level": "Tahsin 2", "teacher": "ustadz.ibrahim", "students": 6, "schedule": "TUE-THU 10:00", "room": "B2"}], "total_students": 15, "total_classes": 2}',
88.0, 1, '{"undersized": ["Tahsin 2 - 6 students"]}', '{"manual_adjustments": ["Moved 1 student from Tahsin 1 to Tahsin 2"]}',
'40000000-0000-0000-0000-000000000001', NOW() - INTERVAL '12 hours', false)
ON CONFLICT (id) DO NOTHING;

-- STEP 7: CLASS SESSIONS PREPARATION
-- Sample class sessions for testing preparation workflow
INSERT INTO class_sessions (
    id, id_class_group, session_date, session_number,
    teaching_materials, preparation_status, id_instructor, 
    created_at, updated_at
) VALUES
('62345678-6234-5678-9abc-000000000001', '70000000-0000-0000-0000-000000000001', '2025-02-03', 1,
'{"textbook": "Tahsin Level 1 - Chapter 1", "audio_files": ["pronunciation_guide.mp3"], "worksheets": ["basic_letters.pdf"]}',
'READY', '20000000-0000-0000-0000-000000000001', NOW(), NOW()),
('62345678-6234-5678-9abc-000000000002', '70000000-0000-0000-0000-000000000002', '2025-02-03', 1,
'{"textbook": "Tahsin Level 1 - Chapter 2", "video_files": ["group_practice.mp4"], "handouts": ["review_sheet.pdf"]}',
'IN_PROGRESS', '20000000-0000-0000-0000-000000000002', NOW(), NOW()),
('62345678-6234-5678-9abc-000000000003', '70000000-0000-0000-0000-000000000003', '2025-02-03', 1,
'{}',
'DRAFT', '20000000-0000-0000-0000-000000000003', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert learning objectives into separate table
INSERT INTO class_session_objectives (class_session_id, learning_objective) VALUES
('62345678-6234-5678-9abc-000000000001', 'Master basic Arabic letters'),
('62345678-6234-5678-9abc-000000000001', 'Practice pronunciation'),
('62345678-6234-5678-9abc-000000000001', 'Introduction to Tajwid rules'),
('62345678-6234-5678-9abc-000000000002', 'Review previous lesson'),
('62345678-6234-5678-9abc-000000000002', 'Advanced pronunciation techniques'),
('62345678-6234-5678-9abc-000000000002', 'Group reading practice'),
('62345678-6234-5678-9abc-000000000003', 'Intermediate Tajwid rules'),
('62345678-6234-5678-9abc-000000000003', 'Practical application'),
('62345678-6234-5678-9abc-000000000003', 'Individual assessment')
ON CONFLICT DO NOTHING;

-- STEP 8: SESSION MATERIALS
-- Teaching materials for class sessions
INSERT INTO session_materials (
    id, id_session, material_type, file_path, 
    material_title, is_shared_with_students, upload_date
) VALUES
('72345678-7234-5678-9abc-000000000001', '62345678-6234-5678-9abc-000000000001', 'AUDIO', '/materials/audio/basic_pronunciation.mp3', 'Basic Arabic Pronunciation Guide', true, NOW()),
('72345678-7234-5678-9abc-000000000002', '62345678-6234-5678-9abc-000000000001', 'WORKSHEET', '/materials/worksheets/arabic_letters.pdf', 'Arabic Letters Practice Sheet', true, NOW()),
('72345678-7234-5678-9abc-000000000003', '62345678-6234-5678-9abc-000000000002', 'VIDEO', '/materials/videos/group_reading.mp4', 'Group Reading Demonstration', false, NOW()),
('72345678-7234-5678-9abc-000000000004', '62345678-6234-5678-9abc-000000000003', 'TEXT', '/materials/texts/tajwid_rules.pdf', 'Intermediate Tajwid Rules', true, NOW())
ON CONFLICT (id) DO NOTHING;

-- STEP 9: CLASS PREPARATION CHECKLISTS
-- Preparation checklists for instructors
INSERT INTO class_preparation_checklist (
    id, id_session, checklist_item, is_completed, 
    completed_at, completed_by
) VALUES
('82345678-8234-5678-9abc-000000000001', '62345678-6234-5678-9abc-000000000001', 'Review student profiles and assessment results', true, NOW() - INTERVAL '1 hour', '20000000-0000-0000-0000-000000000001'),
('82345678-8234-5678-9abc-000000000002', '62345678-6234-5678-9abc-000000000001', 'Prepare teaching materials and resources', true, NOW() - INTERVAL '1 hour', '20000000-0000-0000-0000-000000000001'),
('82345678-8234-5678-9abc-000000000003', '62345678-6234-5678-9abc-000000000001', 'Setup classroom environment and technology', true, NOW() - INTERVAL '30 minutes', '20000000-0000-0000-0000-000000000001'),
('82345678-8234-5678-9abc-000000000004', '62345678-6234-5678-9abc-000000000001', 'Plan differentiated instruction strategies', true, NOW() - INTERVAL '30 minutes', '20000000-0000-0000-0000-000000000001'),
('82345678-8234-5678-9abc-000000000005', '62345678-6234-5678-9abc-000000000002', 'Review student profiles and assessment results', true, NOW() - INTERVAL '2 hours', '20000000-0000-0000-0000-000000000002'),
('82345678-8234-5678-9abc-000000000006', '62345678-6234-5678-9abc-000000000002', 'Prepare teaching materials and resources', false, NULL, NULL),
('82345678-8234-5678-9abc-000000000007', '62345678-6234-5678-9abc-000000000003', 'Review student profiles and assessment results', false, NULL, NULL),
('82345678-8234-5678-9abc-000000000008', '62345678-6234-5678-9abc-000000000003', 'Prepare teaching materials and resources', false, NULL, NULL)
ON CONFLICT (id) DO NOTHING;