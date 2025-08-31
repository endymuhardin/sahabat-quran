-- =====================================================
-- CLASS PREPARATION SYSTEM SCHEMA
-- Version: V005
-- Description: Complete schema for class preparation, teacher availability, and automated class generation
-- =====================================================

-- =====================================================
-- 1. ACADEMIC TERM MANAGEMENT
-- =====================================================
CREATE TABLE academic_terms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    term_name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PLANNING', -- PLANNING, ACTIVE, COMPLETED
    preparation_deadline DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. TEACHER AVAILABILITY SYSTEM
-- =====================================================
CREATE TABLE teacher_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_teacher UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE CASCADE,
    day_of_week INTEGER NOT NULL, -- 1=Monday, 7=Sunday
    session_time VARCHAR(20) NOT NULL, -- 'PAGI_AWAL', 'PAGI', 'SIANG', 'SORE', 'MALAM'
    is_available BOOLEAN NOT NULL DEFAULT false,
    max_classes_per_week INTEGER DEFAULT 6,
    preferences TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_teacher, id_term, day_of_week, session_time)
);

-- =====================================================
-- 3. TEACHER LEVEL ASSIGNMENTS
-- =====================================================
CREATE TABLE teacher_level_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_teacher UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_level UUID NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    id_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE CASCADE,
    competency_level VARCHAR(20) NOT NULL, -- 'JUNIOR', 'SENIOR', 'EXPERT'
    max_classes_for_level INTEGER,
    specialization VARCHAR(50), -- 'FOUNDATION', 'REMEDIAL', 'ADVANCED', 'MIXED'
    assigned_by UUID REFERENCES users(id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    UNIQUE(id_teacher, id_level, id_term)
);

-- =====================================================
-- 4. ENHANCED CLASS TABLE (Add new columns)
-- =====================================================
-- Add new columns to existing classes table
ALTER TABLE classes ADD COLUMN IF NOT EXISTS id_term UUID REFERENCES academic_terms(id);
ALTER TABLE classes ADD COLUMN IF NOT EXISTS min_students INTEGER DEFAULT 7;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS max_students INTEGER DEFAULT 10;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS size_override_reason TEXT;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS is_undersized_approved BOOLEAN DEFAULT false;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS student_category_mix VARCHAR(20) DEFAULT 'MIXED'; -- 'NEW_ONLY', 'EXISTING_ONLY', 'MIXED'
ALTER TABLE classes ADD COLUMN IF NOT EXISTS class_type VARCHAR(30) DEFAULT 'STANDARD'; -- 'FOUNDATION', 'STANDARD', 'REMEDIAL', 'ADVANCED'

-- =====================================================
-- 5. SYSTEM CONFIGURATION
-- =====================================================
CREATE TABLE class_size_configuration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(50) UNIQUE NOT NULL, -- 'default.min', 'default.max', 'tahsin1.min', etc.
    config_value INTEGER NOT NULL,
    level_id UUID REFERENCES levels(id), -- NULL for system defaults
    description TEXT,
    updated_by UUID REFERENCES users(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6. CLASS GENERATION SYSTEM
-- =====================================================
CREATE TABLE generated_class_proposals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE CASCADE,
    generation_run INTEGER NOT NULL,
    proposal_data JSONB NOT NULL, -- Complete class schedule proposal
    optimization_score DECIMAL(5,2),
    conflict_count INTEGER DEFAULT 0,
    size_violations JSONB, -- Classes violating size constraints
    manual_overrides JSONB, -- Manual changes made
    generation_parameters JSONB, -- Parameters used for generation
    generated_by UUID REFERENCES users(id),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT false,
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP
);

-- =====================================================
-- 7. CLASS SESSION PREPARATION
-- =====================================================
CREATE TABLE class_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_class UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    session_date DATE NOT NULL,
    session_number INTEGER, -- Sequential session number within term
    learning_objectives TEXT[],
    teaching_materials JSONB,
    preparation_status VARCHAR(20) DEFAULT 'DRAFT', -- 'DRAFT', 'IN_PROGRESS', 'READY', 'COMPLETED'
    id_instructor UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_class, session_date)
);

-- =====================================================
-- 7B. CLASS SESSION OBJECTIVES (ElementCollection table)
-- =====================================================
CREATE TABLE class_session_objectives (
    class_session_id UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    learning_objective TEXT NOT NULL
);

-- =====================================================
-- 8. SESSION MATERIALS MANAGEMENT
-- =====================================================
CREATE TABLE session_materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    material_type VARCHAR(50) NOT NULL, -- 'AUDIO', 'TEXT', 'VIDEO', 'WORKSHEET', 'PRESENTATION'
    file_path VARCHAR(500),
    material_title VARCHAR(200) NOT NULL,
    material_description TEXT,
    file_size BIGINT, -- in bytes
    mime_type VARCHAR(100),
    is_shared_with_students BOOLEAN DEFAULT false,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by UUID REFERENCES users(id)
);

-- =====================================================
-- 9. CLASS PREPARATION CHECKLIST
-- =====================================================
CREATE TABLE class_preparation_checklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    checklist_item VARCHAR(200) NOT NULL,
    item_category VARCHAR(50), -- 'PLANNING', 'MATERIALS', 'ASSESSMENT', 'SETUP'
    is_completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    completed_by UUID REFERENCES users(id),
    notes TEXT,
    display_order INTEGER DEFAULT 0
);

-- =====================================================
-- 10. STUDENT ASSESSMENT TRACKING (Enhanced)
-- =====================================================
CREATE TABLE student_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_student UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE CASCADE,
    student_category VARCHAR(20) NOT NULL, -- 'NEW', 'EXISTING'
    assessment_type VARCHAR(30) NOT NULL, -- 'PLACEMENT_TEST', 'TERM_EXAM'
    assessment_score DECIMAL(5,2),
    assessment_grade VARCHAR(5), -- 'A', 'B', 'C', 'D' for exams
    determined_level UUID REFERENCES levels(id),
    previous_class UUID REFERENCES classes(id), -- for existing students
    assessment_date DATE,
    assessment_notes TEXT,
    assessed_by UUID REFERENCES users(id),
    is_validated BOOLEAN DEFAULT false,
    validated_by UUID REFERENCES users(id),
    validated_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 11. CLASS GENERATION AUDIT LOG
-- =====================================================
CREATE TABLE class_generation_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE CASCADE,
    id_proposal UUID REFERENCES generated_class_proposals(id),
    action_type VARCHAR(50) NOT NULL, -- 'GENERATION', 'MANUAL_EDIT', 'APPROVAL', 'REJECTION'
    action_description TEXT,
    old_data JSONB, -- Previous state
    new_data JSONB, -- New state
    performed_by UUID REFERENCES users(id),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT
);

-- =====================================================
-- 12. INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_teacher_availability_term ON teacher_availability(id_term);
CREATE INDEX IF NOT EXISTS idx_teacher_availability_teacher ON teacher_availability(id_teacher);
CREATE INDEX IF NOT EXISTS idx_teacher_availability_day_session ON teacher_availability(day_of_week, session_time);

CREATE INDEX IF NOT EXISTS idx_teacher_level_assignments_term ON teacher_level_assignments(id_term);
CREATE INDEX IF NOT EXISTS idx_teacher_level_assignments_teacher ON teacher_level_assignments(id_teacher);
CREATE INDEX IF NOT EXISTS idx_teacher_level_assignments_level ON teacher_level_assignments(id_level);

CREATE INDEX IF NOT EXISTS idx_classes_term ON classes(id_term);
-- idx_classes_level already exists from V001, skip it
CREATE INDEX IF NOT EXISTS idx_classes_instructor ON classes(id_instructor);
CREATE INDEX IF NOT EXISTS idx_classes_active ON classes(is_active);

CREATE INDEX IF NOT EXISTS idx_class_sessions_class ON class_sessions(id_class);
CREATE INDEX IF NOT EXISTS idx_class_sessions_date ON class_sessions(session_date);
CREATE INDEX IF NOT EXISTS idx_class_sessions_instructor ON class_sessions(id_instructor);
CREATE INDEX IF NOT EXISTS idx_class_sessions_status ON class_sessions(preparation_status);

CREATE INDEX IF NOT EXISTS idx_session_materials_session ON session_materials(id_session);
CREATE INDEX IF NOT EXISTS idx_session_materials_type ON session_materials(material_type);
CREATE INDEX IF NOT EXISTS idx_session_materials_shared ON session_materials(is_shared_with_students);

CREATE INDEX IF NOT EXISTS idx_preparation_checklist_session ON class_preparation_checklist(id_session);
CREATE INDEX IF NOT EXISTS idx_preparation_checklist_completed ON class_preparation_checklist(is_completed);

CREATE INDEX IF NOT EXISTS idx_student_assessments_student ON student_assessments(id_student);
CREATE INDEX IF NOT EXISTS idx_student_assessments_term ON student_assessments(id_term);
CREATE INDEX IF NOT EXISTS idx_student_assessments_type ON student_assessments(assessment_type);
CREATE INDEX IF NOT EXISTS idx_student_assessments_level ON student_assessments(determined_level);

CREATE INDEX IF NOT EXISTS idx_generation_proposals_term ON generated_class_proposals(id_term);
CREATE INDEX IF NOT EXISTS idx_generation_proposals_approved ON generated_class_proposals(is_approved);

-- =====================================================
-- 13. CONSTRAINTS AND VALIDATIONS
-- =====================================================
-- Ensure day_of_week is valid (1-7)
ALTER TABLE teacher_availability ADD CONSTRAINT chk_day_of_week 
    CHECK (day_of_week >= 1 AND day_of_week <= 7);

-- Ensure session_time is valid
ALTER TABLE teacher_availability ADD CONSTRAINT chk_session_time 
    CHECK (session_time IN ('PAGI_AWAL', 'PAGI', 'SIANG', 'SORE', 'MALAM'));

-- Ensure competency_level is valid
ALTER TABLE teacher_level_assignments ADD CONSTRAINT chk_competency_level 
    CHECK (competency_level IN ('JUNIOR', 'SENIOR', 'EXPERT'));

-- Ensure specialization is valid
ALTER TABLE teacher_level_assignments ADD CONSTRAINT chk_specialization 
    CHECK (specialization IN ('FOUNDATION', 'REMEDIAL', 'ADVANCED', 'MIXED') OR specialization IS NULL);

-- Ensure min_students <= max_students
ALTER TABLE classes ADD CONSTRAINT chk_class_size_range 
    CHECK (min_students <= max_students);

-- Ensure student_category_mix is valid
ALTER TABLE classes ADD CONSTRAINT chk_student_category_mix 
    CHECK (student_category_mix IN ('NEW_ONLY', 'EXISTING_ONLY', 'MIXED'));

-- Ensure class_type is valid
ALTER TABLE classes ADD CONSTRAINT chk_class_type 
    CHECK (class_type IN ('FOUNDATION', 'STANDARD', 'REMEDIAL', 'ADVANCED'));

-- Ensure material_type is valid
ALTER TABLE session_materials ADD CONSTRAINT chk_material_type 
    CHECK (material_type IN ('AUDIO', 'TEXT', 'VIDEO', 'WORKSHEET', 'PRESENTATION', 'OTHER'));

-- Ensure preparation_status is valid
ALTER TABLE class_sessions ADD CONSTRAINT chk_preparation_status 
    CHECK (preparation_status IN ('DRAFT', 'IN_PROGRESS', 'READY', 'COMPLETED'));

-- Ensure student_category is valid
ALTER TABLE student_assessments ADD CONSTRAINT chk_student_category 
    CHECK (student_category IN ('NEW', 'EXISTING'));

-- Ensure assessment_type is valid
ALTER TABLE student_assessments ADD CONSTRAINT chk_assessment_type 
    CHECK (assessment_type IN ('PLACEMENT_TEST', 'TERM_EXAM'));

-- Ensure assessment_score is valid (0-100)
ALTER TABLE student_assessments ADD CONSTRAINT chk_assessment_score 
    CHECK (assessment_score IS NULL OR (assessment_score >= 0 AND assessment_score <= 100));

-- Ensure term status is valid
ALTER TABLE academic_terms ADD CONSTRAINT chk_term_status 
    CHECK (status IN ('PLANNING', 'ACTIVE', 'COMPLETED'));

-- Ensure start_date < end_date
ALTER TABLE academic_terms ADD CONSTRAINT chk_term_dates 
    CHECK (start_date < end_date);

-- =====================================================
-- 14. COMMENTS FOR DOCUMENTATION
-- =====================================================
COMMENT ON TABLE academic_terms IS 'Academic term/semester management for class preparation cycles';
COMMENT ON TABLE teacher_availability IS 'Teacher availability submissions for class scheduling';
COMMENT ON TABLE teacher_level_assignments IS 'Management assignments of teachers to teaching levels';
COMMENT ON TABLE class_size_configuration IS 'System configuration for class size limits';
COMMENT ON TABLE generated_class_proposals IS 'Automated class generation results and proposals';
COMMENT ON TABLE class_sessions IS 'Individual class session preparation tracking';
COMMENT ON TABLE session_materials IS 'Teaching materials for class sessions';
COMMENT ON TABLE class_preparation_checklist IS 'Preparation checklist items for class sessions';
COMMENT ON TABLE student_assessments IS 'Student assessment results (placement tests and exams)';
COMMENT ON TABLE class_generation_log IS 'Audit log for class generation and modification activities';

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply update triggers to relevant tables
CREATE TRIGGER update_academic_terms_updated_at BEFORE UPDATE ON academic_terms
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_teacher_availability_updated_at BEFORE UPDATE ON teacher_availability
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_classes_updated_at BEFORE UPDATE ON classes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_class_sessions_updated_at BEFORE UPDATE ON class_sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_class_size_configuration_updated_at BEFORE UPDATE ON class_size_configuration
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();