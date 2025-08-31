-- =====================================================
-- CLASS PREPARATION SYSTEM SEED DATA
-- Version: V006
-- Description: Initial seed data for class preparation system including configuration, permissions, and test data
-- =====================================================

-- =====================================================
-- 1. ADD NEW PERMISSIONS FOR CLASS PREPARATION SYSTEM
-- =====================================================
INSERT INTO permissions (code, name, module, description) VALUES
-- Class Management Module (Missing Implementation)
('CLASS_SESSION_CREATE', 'Create Class Sessions', 'CLASS_MANAGEMENT', 'Create new class sessions and schedules'),
('CLASS_SESSION_EDIT', 'Edit Class Sessions', 'CLASS_MANAGEMENT', 'Edit existing class sessions'),
('CLASS_SESSION_DELETE', 'Delete Class Sessions', 'CLASS_MANAGEMENT', 'Delete class sessions'),
('CLASS_MATERIAL_UPLOAD', 'Upload Class Materials', 'CLASS_MANAGEMENT', 'Upload teaching materials for classes'),
('CLASS_MATERIAL_SHARE', 'Share Class Materials', 'CLASS_MANAGEMENT', 'Share materials with students'),
('CLASS_ROSTER_VIEW', 'View Class Roster', 'CLASS_MANAGEMENT', 'View student rosters for classes'),
('CLASS_SCHEDULE_VIEW', 'View Class Schedule', 'CLASS_MANAGEMENT', 'View class schedules and timetables'),
('CLASS_ASSIGNMENT_MANAGE', 'Manage Class Assignments', 'CLASS_MANAGEMENT', 'Assign students and teachers to classes'),

-- Class Preparation Module (New)
('LESSON_PLAN_CREATE', 'Create Lesson Plans', 'CLASS_PREPARATION', 'Create lesson plans for class sessions'),
('LESSON_PLAN_EDIT', 'Edit Lesson Plans', 'CLASS_PREPARATION', 'Edit existing lesson plans'),
('LESSON_PLAN_VIEW', 'View Lesson Plans', 'CLASS_PREPARATION', 'View lesson plans and preparation materials'),
('TEACHING_MATERIAL_MANAGE', 'Manage Teaching Materials', 'CLASS_PREPARATION', 'Manage teaching materials library'),
('STUDENT_PROGRESS_REVIEW', 'Review Student Progress', 'CLASS_PREPARATION', 'Review individual student progress'),
('SESSION_PREPARATION_MARK_READY', 'Mark Session as Ready', 'CLASS_PREPARATION', 'Mark class session as ready to teach'),
('PREPARATION_ANALYTICS_VIEW', 'View Preparation Analytics', 'CLASS_PREPARATION', 'View preparation completion analytics'),

-- Academic Planning Module (New)
('ACADEMIC_TERM_MANAGE', 'Manage Academic Terms', 'ACADEMIC_PLANNING', 'Manage academic terms and semesters'),
('TEACHER_AVAILABILITY_VIEW', 'View Teacher Availability', 'ACADEMIC_PLANNING', 'View teacher availability submissions'),
('TEACHER_AVAILABILITY_SUBMIT', 'Submit Teacher Availability', 'ACADEMIC_PLANNING', 'Submit availability for teaching assignments'),
('TEACHER_LEVEL_ASSIGN', 'Assign Teachers to Levels', 'ACADEMIC_PLANNING', 'Assign teachers to appropriate teaching levels'),
('CLASS_GENERATION_RUN', 'Run Class Generation', 'ACADEMIC_PLANNING', 'Execute automated class generation algorithms'),
('CLASS_GENERATION_REVIEW', 'Review Generated Classes', 'ACADEMIC_PLANNING', 'Review and refine generated class schedules'),
('SCHEDULE_APPROVE', 'Approve Final Schedule', 'ACADEMIC_PLANNING', 'Approve final class schedule for publication'),
('SYSTEM_GOLIVE_MANAGE', 'Manage System Go-Live', 'ACADEMIC_PLANNING', 'Manage system go-live and readiness confirmation'),

-- Assessment Management (Enhanced)
('STUDENT_ASSESSMENT_VIEW', 'View Student Assessments', 'ASSESSMENT', 'View placement test and exam results'),
('STUDENT_ASSESSMENT_PROCESS', 'Process Student Assessments', 'ASSESSMENT', 'Process and validate assessment results'),
('PLACEMENT_TEST_SCHEDULE', 'Schedule Placement Tests', 'ASSESSMENT', 'Schedule placement tests for new students'),
('ASSESSMENT_ANALYTICS', 'Assessment Analytics', 'ASSESSMENT', 'View assessment analytics and trends');

-- =====================================================
-- 2. ASSIGN NEW PERMISSIONS TO EXISTING ROLES
-- =====================================================

-- ADMIN gets all new permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions
WHERE code IN (
    'CLASS_SESSION_CREATE', 'CLASS_SESSION_EDIT', 'CLASS_SESSION_DELETE',
    'CLASS_MATERIAL_UPLOAD', 'CLASS_MATERIAL_SHARE', 'CLASS_ROSTER_VIEW',
    'CLASS_SCHEDULE_VIEW', 'CLASS_ASSIGNMENT_MANAGE',
    'LESSON_PLAN_CREATE', 'LESSON_PLAN_EDIT', 'LESSON_PLAN_VIEW',
    'TEACHING_MATERIAL_MANAGE', 'STUDENT_PROGRESS_REVIEW',
    'SESSION_PREPARATION_MARK_READY', 'PREPARATION_ANALYTICS_VIEW',
    'ACADEMIC_TERM_MANAGE', 'TEACHER_AVAILABILITY_VIEW', 'TEACHER_AVAILABILITY_SUBMIT',
    'TEACHER_LEVEL_ASSIGN', 'CLASS_GENERATION_RUN', 'CLASS_GENERATION_REVIEW',
    'SCHEDULE_APPROVE', 'SYSTEM_GOLIVE_MANAGE',
    'STUDENT_ASSESSMENT_VIEW', 'STUDENT_ASSESSMENT_PROCESS',
    'PLACEMENT_TEST_SCHEDULE', 'ASSESSMENT_ANALYTICS'
);

-- INSTRUCTOR permissions (teaching and preparation focused)
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN (
    'CLASS_SESSION_VIEW', 'CLASS_MATERIAL_UPLOAD', 'CLASS_MATERIAL_SHARE',
    'CLASS_ROSTER_VIEW', 'CLASS_SCHEDULE_VIEW',
    'LESSON_PLAN_CREATE', 'LESSON_PLAN_EDIT', 'LESSON_PLAN_VIEW',
    'TEACHING_MATERIAL_MANAGE', 'STUDENT_PROGRESS_REVIEW',
    'SESSION_PREPARATION_MARK_READY',
    'TEACHER_AVAILABILITY_SUBMIT',
    'STUDENT_ASSESSMENT_VIEW'
);

-- ADMIN_STAFF permissions (academic planning and coordination)
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    'CLASS_SESSION_CREATE', 'CLASS_SESSION_EDIT', 'CLASS_ROSTER_VIEW',
    'CLASS_SCHEDULE_VIEW', 'CLASS_ASSIGNMENT_MANAGE',
    'LESSON_PLAN_VIEW', 'PREPARATION_ANALYTICS_VIEW',
    'ACADEMIC_TERM_MANAGE', 'TEACHER_AVAILABILITY_VIEW',
    'CLASS_GENERATION_RUN', 'CLASS_GENERATION_REVIEW',
    'SCHEDULE_APPROVE', 'SYSTEM_GOLIVE_MANAGE',
    'STUDENT_ASSESSMENT_VIEW', 'STUDENT_ASSESSMENT_PROCESS',
    'PLACEMENT_TEST_SCHEDULE', 'ASSESSMENT_ANALYTICS'
);

-- MANAGEMENT permissions (oversight and analytics)
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN (
    'CLASS_ROSTER_VIEW', 'CLASS_SCHEDULE_VIEW',
    'PREPARATION_ANALYTICS_VIEW',
    'TEACHER_AVAILABILITY_VIEW', 'TEACHER_LEVEL_ASSIGN',
    'CLASS_GENERATION_REVIEW', 'SCHEDULE_APPROVE',
    'STUDENT_ASSESSMENT_VIEW', 'ASSESSMENT_ANALYTICS'
);

-- =====================================================
-- 3. DEFAULT CLASS SIZE CONFIGURATION
-- =====================================================
INSERT INTO class_size_configuration (config_key, config_value, level_id, description) VALUES
-- System defaults
('default.min', 7, NULL, 'Default minimum students per class'),
('default.max', 10, NULL, 'Default maximum students per class'),

-- Level-specific configurations
('tahsin1.min', 8, (SELECT id FROM levels WHERE name = 'Tahsin 1'), 'Minimum students for Tahsin 1 classes'),
('tahsin1.max', 12, (SELECT id FROM levels WHERE name = 'Tahsin 1'), 'Maximum students for Tahsin 1 classes'),
('tahsin2.min', 7, (SELECT id FROM levels WHERE name = 'Tahsin 2'), 'Minimum students for Tahsin 2 classes'),
('tahsin2.max', 10, (SELECT id FROM levels WHERE name = 'Tahsin 2'), 'Maximum students for Tahsin 2 classes'),
('tahsin3.min', 6, (SELECT id FROM levels WHERE name = 'Tahsin 3'), 'Minimum students for Tahsin 3 classes'),
('tahsin3.max', 8, (SELECT id FROM levels WHERE name = 'Tahsin 3'), 'Maximum students for Tahsin 3 classes'),
('tahfidz_pemula.min', 5, (SELECT id FROM levels WHERE name = 'Tahfidz Pemula'), 'Minimum students for Tahfidz Pemula classes'),
('tahfidz_pemula.max', 8, (SELECT id FROM levels WHERE name = 'Tahfidz Pemula'), 'Maximum students for Tahfidz Pemula classes'),
('tahfidz_lanjutan.min', 4, (SELECT id FROM levels WHERE name = 'Tahfidz Lanjutan'), 'Minimum students for Tahfidz Lanjutan classes'),
('tahfidz_lanjutan.max', 6, (SELECT id FROM levels WHERE name = 'Tahfidz Lanjutan'), 'Maximum students for Tahfidz Lanjutan classes');

-- =====================================================
-- 4. SAMPLE ACADEMIC TERM
-- =====================================================
INSERT INTO academic_terms (id, term_name, start_date, end_date, status, preparation_deadline) VALUES
('80000000-0000-0000-0000-000000000001', 'Semester 1 2024', '2024-03-01', '2024-08-31', 'PLANNING', '2024-02-15'),
('80000000-0000-0000-0000-000000000002', 'Semester 2 2024', '2024-09-01', '2024-12-31', 'PLANNING', '2024-08-15');

-- =====================================================
-- 5. SAMPLE TEACHER AVAILABILITY DATA
-- =====================================================
-- Ustadz Ahmad availability (flexible schedule)
INSERT INTO teacher_availability (id_teacher, id_term, day_of_week, session_time, is_available, max_classes_per_week, preferences) VALUES
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 1, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 1, 'SIANG', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 1, 'SORE', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 2, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 2, 'SORE', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 3, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 3, 'SIANG', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 3, 'SORE', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 4, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 4, 'SORE', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 5, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 5, 'SIANG', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 6, 'PAGI', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 6, 'SIANG', true, 6, 'Prefer morning sessions'),
('20000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 6, 'SORE', true, 6, 'Prefer morning sessions');

-- Ustadzah Fatimah availability (morning focused)
INSERT INTO teacher_availability (id_teacher, id_term, day_of_week, session_time, is_available, max_classes_per_week, preferences) VALUES
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 1, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 1, 'SIANG', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 2, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 2, 'SIANG', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 3, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 4, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 4, 'SIANG', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 5, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 6, 'PAGI', true, 4, 'Morning only, family commitments in afternoon'),
('20000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 6, 'SIANG', true, 4, 'Morning only, family commitments in afternoon');

-- Ustadz Ibrahim availability (full schedule)
INSERT INTO teacher_availability (id_teacher, id_term, day_of_week, session_time, is_available, max_classes_per_week, preferences) VALUES
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 1, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 1, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 1, 'SORE', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 2, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 2, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 2, 'SORE', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 3, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 3, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 3, 'SORE', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 4, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 4, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 4, 'SORE', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 5, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 5, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 6, 'PAGI', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 6, 'SIANG', true, 8, 'Available all times, experienced with all levels'),
('20000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 6, 'SORE', true, 8, 'Available all times, experienced with all levels');

-- =====================================================
-- 6. SAMPLE TEACHER LEVEL ASSIGNMENTS
-- =====================================================
INSERT INTO teacher_level_assignments (id_teacher, id_level, id_term, competency_level, max_classes_for_level, specialization, assigned_by, notes) VALUES
-- Ustadz Ahmad - Senior level, all capabilities
('20000000-0000-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahsin 1'), '80000000-0000-0000-0000-000000000001', 'SENIOR', 3, 'MIXED', '60000000-0000-0000-0000-000000000001', 'Excellent with mixed ability classes'),
('20000000-0000-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahsin 2'), '80000000-0000-0000-0000-000000000001', 'SENIOR', 2, 'ADVANCED', '60000000-0000-0000-0000-000000000001', 'Good at accelerated learning programs'),
('20000000-0000-0000-0000-000000000001', (SELECT id FROM levels WHERE name = 'Tahfidz Pemula'), '80000000-0000-0000-0000-000000000001', 'SENIOR', 2, 'MIXED', '60000000-0000-0000-0000-000000000001', 'Strong memorization techniques'),

-- Ustadzah Fatimah - Junior level, foundation specialist
('20000000-0000-0000-0000-000000000002', (SELECT id FROM levels WHERE name = 'Tahsin 1'), '80000000-0000-0000-0000-000000000001', 'JUNIOR', 3, 'FOUNDATION', '60000000-0000-0000-0000-000000000001', 'Excellent with new students and beginners'),
('20000000-0000-0000-0000-000000000002', (SELECT id FROM levels WHERE name = 'Tahsin 2'), '80000000-0000-0000-0000-000000000001', 'JUNIOR', 2, 'MIXED', '60000000-0000-0000-0000-000000000001', 'Good progression teaching'),

-- Ustadz Ibrahim - Expert level, all specializations
('20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 1'), '80000000-0000-0000-0000-000000000001', 'EXPERT', 2, 'REMEDIAL', '60000000-0000-0000-0000-000000000001', 'Specialist in helping struggling students'),
('20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 2'), '80000000-0000-0000-0000-000000000001', 'EXPERT', 3, 'MIXED', '60000000-0000-0000-0000-000000000001', 'Versatile teaching approach'),
('20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahsin 3'), '80000000-0000-0000-0000-000000000001', 'EXPERT', 2, 'ADVANCED', '60000000-0000-0000-0000-000000000001', 'Advanced tajwid and recitation expert'),
('20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahfidz Pemula'), '80000000-0000-0000-0000-000000000001', 'EXPERT', 2, 'ADVANCED', '60000000-0000-0000-0000-000000000001', 'Memorization methodology expert'),
('20000000-0000-0000-0000-000000000003', (SELECT id FROM levels WHERE name = 'Tahfidz Lanjutan'), '80000000-0000-0000-0000-000000000001', 'EXPERT', 1, 'ADVANCED', '60000000-0000-0000-0000-000000000001', 'Advanced tahfidz and ijazah qualified');

-- =====================================================
-- 7. SAMPLE STUDENT ASSESSMENTS
-- =====================================================
-- New students with placement test results
INSERT INTO student_assessments (id_student, id_term, student_category, assessment_type, assessment_score, determined_level, assessment_date, assessment_notes, assessed_by) VALUES
-- High performing new students
('30000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 'NEW', 'PLACEMENT_TEST', 85.5, (SELECT id FROM levels WHERE name = 'Tahsin 2'), '2024-02-10', 'Good foundation, ready for intermediate level', '20000000-0000-0000-0000-000000000001'),
('30000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', 'NEW', 'PLACEMENT_TEST', 92.0, (SELECT id FROM levels WHERE name = 'Tahsin 3'), '2024-02-10', 'Excellent tajwid understanding, advanced placement', '20000000-0000-0000-0000-000000000001'),

-- Standard new students
('30000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000001', 'NEW', 'PLACEMENT_TEST', 65.0, (SELECT id FROM levels WHERE name = 'Tahsin 1'), '2024-02-11', 'Basic foundation needs strengthening', '20000000-0000-0000-0000-000000000002'),
('30000000-0000-0000-0000-000000000004', '80000000-0000-0000-0000-000000000001', 'NEW', 'PLACEMENT_TEST', 72.0, (SELECT id FROM levels WHERE name = 'Tahsin 1'), '2024-02-11', 'Good basic knowledge, standard Tahsin 1 placement', '20000000-0000-0000-0000-000000000002'),

-- Existing students with exam results (from previous term)
('30000000-0000-0000-0000-000000000005', '80000000-0000-0000-0000-000000000001', 'EXISTING', 'TERM_EXAM', 88.0, (SELECT id FROM levels WHERE name = 'Tahsin 2'), '2024-01-25', 'Promoted from Tahsin 1 with excellent progress', '20000000-0000-0000-0000-000000000001'),
('30000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', 'EXISTING', 'TERM_EXAM', 76.0, (SELECT id FROM levels WHERE name = 'Tahsin 2'), '2024-01-25', 'Good progress, ready for next level', '20000000-0000-0000-0000-000000000002');

-- =====================================================
-- 8. SAMPLE DEFAULT PREPARATION CHECKLIST TEMPLATES
-- =====================================================
-- These will be used as templates for creating class preparation checklists
CREATE TABLE preparation_checklist_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name VARCHAR(100) NOT NULL,
    level_type VARCHAR(50), -- 'TAHSIN', 'TAHFIDZ', 'ALL'
    class_type VARCHAR(30), -- 'FOUNDATION', 'STANDARD', 'REMEDIAL', 'ADVANCED'
    checklist_items JSONB NOT NULL,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO preparation_checklist_templates (template_name, level_type, class_type, checklist_items, created_by) VALUES
('Standard Tahsin Class Preparation', 'TAHSIN', 'STANDARD', 
'[
  {"item": "Review student assessment results", "category": "PLANNING", "order": 1},
  {"item": "Plan session learning objectives", "category": "PLANNING", "order": 2},
  {"item": "Select Arabic letters/verses for session", "category": "PLANNING", "order": 3},
  {"item": "Prepare tajwid rules explanation", "category": "PLANNING", "order": 4},
  {"item": "Upload audio recitation examples", "category": "MATERIALS", "order": 5},
  {"item": "Prepare practice worksheets", "category": "MATERIALS", "order": 6},
  {"item": "Set up assessment rubric", "category": "ASSESSMENT", "order": 7},
  {"item": "Prepare individual progress tracking", "category": "ASSESSMENT", "order": 8},
  {"item": "Test classroom audio equipment", "category": "SETUP", "order": 9},
  {"item": "Arrange seating for optimal interaction", "category": "SETUP", "order": 10}
]', '40000000-0000-0000-0000-000000000001'),

('New Student Foundation Preparation', 'TAHSIN', 'FOUNDATION', 
'[
  {"item": "Prepare welcome and orientation materials", "category": "PLANNING", "order": 1},
  {"item": "Review placement test results in detail", "category": "PLANNING", "order": 2},
  {"item": "Plan ice-breaking and introduction activities", "category": "PLANNING", "order": 3},
  {"item": "Prepare Arabic alphabet visual aids", "category": "MATERIALS", "order": 4},
  {"item": "Create basic tajwid introduction handouts", "category": "MATERIALS", "order": 5},
  {"item": "Prepare parent communication templates", "category": "MATERIALS", "order": 6},
  {"item": "Set up gentle assessment approach", "category": "ASSESSMENT", "order": 7},
  {"item": "Prepare confidence-building activities", "category": "PLANNING", "order": 8},
  {"item": "Create homework guidance for beginners", "category": "MATERIALS", "order": 9},
  {"item": "Prepare classroom environment for comfort", "category": "SETUP", "order": 10}
]', '40000000-0000-0000-0000-000000000001'),

('Tahfidz Memorization Preparation', 'TAHFIDZ', 'STANDARD', 
'[
  {"item": "Select Quran passages for memorization", "category": "PLANNING", "order": 1},
  {"item": "Prepare memorization technique explanations", "category": "PLANNING", "order": 2},
  {"item": "Review individual memorization progress", "category": "PLANNING", "order": 3},
  {"item": "Upload high-quality recitation recordings", "category": "MATERIALS", "order": 4},
  {"item": "Prepare verse meaning explanations", "category": "MATERIALS", "order": 5},
  {"item": "Create repetition practice schedules", "category": "MATERIALS", "order": 6},
  {"item": "Set up memorization assessment criteria", "category": "ASSESSMENT", "order": 7},
  {"item": "Prepare peer review activities", "category": "ASSESSMENT", "order": 8},
  {"item": "Test recitation playback equipment", "category": "SETUP", "order": 9},
  {"item": "Arrange quiet memorization environment", "category": "SETUP", "order": 10}
]', '40000000-0000-0000-0000-000000000001');

-- =====================================================
-- 9. UPDATE EXISTING CLASSES WITH TERM AND SIZE INFO
-- =====================================================
-- Update existing sample classes with term and size information
UPDATE classes SET 
    id_term = '80000000-0000-0000-0000-000000000001',
    min_students = 8,
    max_students = 12,
    student_category_mix = 'MIXED',
    class_type = 'STANDARD'
WHERE name LIKE 'Tahsin 1%';

UPDATE classes SET 
    id_term = '80000000-0000-0000-0000-000000000001',
    min_students = 7,
    max_students = 10,
    student_category_mix = 'MIXED',
    class_type = 'STANDARD'
WHERE name LIKE 'Tahsin 2%';

UPDATE classes SET 
    id_term = '80000000-0000-0000-0000-000000000001',
    min_students = 6,
    max_students = 8,
    student_category_mix = 'MIXED',
    class_type = 'STANDARD'
WHERE name LIKE 'Tahsin 3%';

-- =====================================================
-- 10. COMMENTS AND DOCUMENTATION
-- =====================================================
COMMENT ON TABLE preparation_checklist_templates IS 'Template checklist items for different class types and levels';

-- Add useful views for reporting
CREATE VIEW v_teacher_availability_summary AS
SELECT 
    ta.id_teacher,
    u.full_name as teacher_name,
    ta.id_term,
    at.term_name,
    COUNT(*) as total_slots,
    COUNT(CASE WHEN ta.is_available THEN 1 END) as available_slots,
    ta.max_classes_per_week,
    ta.preferences
FROM teacher_availability ta
JOIN users u ON ta.id_teacher = u.id
JOIN academic_terms at ON ta.id_term = at.id
GROUP BY ta.id_teacher, u.full_name, ta.id_term, at.term_name, ta.max_classes_per_week, ta.preferences;

CREATE VIEW v_student_assessment_summary AS
SELECT 
    sa.id_student,
    u.full_name as student_name,
    sa.id_term,
    at.term_name,
    sa.student_category,
    sa.assessment_type,
    sa.assessment_score,
    sa.assessment_grade,
    l.name as determined_level_name,
    sa.assessment_date,
    sa.is_validated
FROM student_assessments sa
JOIN users u ON sa.id_student = u.id
JOIN academic_terms at ON sa.id_term = at.id
LEFT JOIN levels l ON sa.determined_level = l.id;

COMMENT ON VIEW v_teacher_availability_summary IS 'Summary view of teacher availability by term';
COMMENT ON VIEW v_student_assessment_summary IS 'Summary view of student assessments and level determinations';