-- =====================================================
-- SCHEMA CREATION FOR YAYASAN SAHABAT QURAN APPLICATION
-- =====================================================

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_credentials table (one-to-one with users)
CREATE TABLE user_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_user UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create role_permissions junction table
CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_role UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    id_permission UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_role, id_permission)
);

-- Create user_roles junction table (many-to-many between users and roles)
CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_user UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_role UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_assigned_by UUID REFERENCES users(id),
    UNIQUE(id_user, id_role)
);

-- Create levels table
CREATE TABLE levels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    order_number INTEGER NOT NULL,
    competency_level VARCHAR(20) NOT NULL DEFAULT 'FOUNDATION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_competency_level CHECK (competency_level IN ('FOUNDATION', 'BASIC', 'INTERMEDIATE', 'ADVANCED'))
);

-- Academic Terms Table (moved here to avoid circular dependency)
CREATE TABLE academic_terms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    term_name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING' CHECK (status IN ('PLANNING', 'ACTIVE', 'COMPLETED')),
    preparation_deadline DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_academic_terms_date_range CHECK (end_date > start_date)
);

-- Create class_groups table (was classes)
CREATE TABLE class_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    id_level UUID NOT NULL REFERENCES levels(id),
    id_instructor UUID REFERENCES users(id),
    id_term UUID REFERENCES academic_terms(id),
    capacity INTEGER NOT NULL,
    min_students INTEGER DEFAULT 7,
    max_students INTEGER DEFAULT 10,
    id_time_slot UUID,
    location VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    size_override_reason TEXT,
    is_undersized_approved BOOLEAN DEFAULT false,
    student_category_mix VARCHAR(20) DEFAULT 'MIXED' CHECK (student_category_mix IN ('NEW_ONLY', 'EXISTING_ONLY', 'MIXED')),
    class_type VARCHAR(30) DEFAULT 'STANDARD' CHECK (class_type IN ('FOUNDATION', 'STANDARD', 'REMEDIAL', 'ADVANCED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create enrollments table
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_student UUID NOT NULL REFERENCES users(id),
    id_class_group UUID NOT NULL REFERENCES class_groups(id),
    enrollment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_student, id_class_group)
);

-- Create attendance table (student attendance)
CREATE TABLE attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_enrollment UUID NOT NULL REFERENCES enrollments(id),
    attendance_date DATE NOT NULL,
    is_present BOOLEAN DEFAULT false,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_created_by UUID REFERENCES users(id),
    UNIQUE(id_enrollment, attendance_date)
);

-- Class Sessions Table (moved here to resolve dependency)
CREATE TABLE class_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_class_group UUID NOT NULL REFERENCES class_groups(id),
    session_date DATE NOT NULL,
    session_number INTEGER,
    teaching_materials JSONB,
    preparation_status VARCHAR(20) DEFAULT 'DRAFT' CHECK (preparation_status IN ('DRAFT', 'IN_PROGRESS', 'READY', 'COMPLETED')),
    id_instructor UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(id_class_group, session_date)
);

-- Create teacher attendance table
CREATE TABLE teacher_attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_class_session UUID NOT NULL REFERENCES class_sessions(id),
    id_scheduled_instructor UUID NOT NULL REFERENCES users(id),
    id_actual_instructor UUID REFERENCES users(id), -- NULL if absent, different if substitute
    arrival_time TIMESTAMP,
    departure_time TIMESTAMP,
    is_present BOOLEAN DEFAULT false,
    absence_reason VARCHAR(200),
    substitute_arranged BOOLEAN DEFAULT false,
    substitute_notes TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_class_session, id_scheduled_instructor)
);

-- Create assessments table
CREATE TABLE assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_enrollment UUID NOT NULL REFERENCES enrollments(id),
    assessment_type VARCHAR(50) NOT NULL,
    assessment_date DATE NOT NULL,
    score DECIMAL(5,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_created_by UUID REFERENCES users(id)
);

-- Create billing table
CREATE TABLE billing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_student UUID NOT NULL REFERENCES users(id),
    billing_period VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_billing UUID NOT NULL REFERENCES billing(id),
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    reference_number VARCHAR(100),
    id_verified_by UUID REFERENCES users(id),
    verification_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create events table
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_time TIME,
    location VARCHAR(200),
    capacity INTEGER,
    fee DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_created_by UUID REFERENCES users(id)
);

-- Create event_registrations table
CREATE TABLE event_registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_event UUID NOT NULL REFERENCES events(id),
    id_participant UUID NOT NULL REFERENCES users(id),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    UNIQUE(id_event, id_participant)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_credentials_user ON user_credentials(id_user);
CREATE INDEX idx_role_permissions_role ON role_permissions(id_role);
CREATE INDEX idx_role_permissions_permission ON role_permissions(id_permission);
CREATE INDEX idx_user_roles_user ON user_roles(id_user);
CREATE INDEX idx_user_roles_role ON user_roles(id_role);
CREATE INDEX idx_classes_level ON class_groups(id_level);
CREATE INDEX idx_class_groups_time_slot ON class_groups(id_time_slot);
CREATE INDEX idx_enrollments_student ON enrollments(id_student);
CREATE INDEX idx_enrollments_class ON enrollments(id_class_group);
CREATE INDEX idx_attendance_enrollment ON attendance(id_enrollment);
CREATE INDEX idx_teacher_attendance_session ON teacher_attendance(id_class_session);
CREATE INDEX idx_teacher_attendance_instructor ON teacher_attendance(id_scheduled_instructor);
CREATE INDEX idx_teacher_attendance_date ON teacher_attendance(arrival_time);
CREATE INDEX idx_billing_student ON billing(id_student);
CREATE INDEX idx_payments_billing ON payments(id_billing);
CREATE INDEX idx_event_registrations_event ON event_registrations(id_event);
CREATE INDEX idx_levels_competency_level ON levels(competency_level);

-- =====================================================
-- STUDENT REGISTRATION SYSTEM SCHEMA
-- =====================================================

-- PROGRAMS REFERENCE TABLE
CREATE TABLE programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    id_level UUID NOT NULL REFERENCES levels(id),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- SESSIONS REFERENCE TABLE
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- TIME SLOT TABLE (Day of week x Session)
CREATE TABLE time_slot (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    day_of_week VARCHAR(15) NOT NULL CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    id_session UUID NOT NULL REFERENCES sessions(id),
    UNIQUE(day_of_week, id_session)
);

-- Indexes related to time_slot
CREATE INDEX idx_time_slot_day_session ON time_slot (day_of_week, id_session);

-- Add FK and NOT NULL constraint now that time_slot exists
ALTER TABLE class_groups
    ALTER COLUMN id_time_slot SET NOT NULL,
    ADD CONSTRAINT fk_class_groups_time_slot FOREIGN KEY (id_time_slot) REFERENCES time_slot(id);

-- PLACEMENT TEST VERSES TABLE
CREATE TABLE placement_test_verses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    surah_number INTEGER NOT NULL CHECK (surah_number >= 1 AND surah_number <= 114),
    surah_name VARCHAR(50) NOT NULL,
    ayah_start INTEGER NOT NULL CHECK (ayah_start >= 1),
    ayah_end INTEGER NOT NULL CHECK (ayah_end >= ayah_start),
    arabic_text TEXT NOT NULL,
    difficulty_level INTEGER NOT NULL CHECK (difficulty_level >= 1 AND difficulty_level <= 5),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- STUDENT REGISTRATIONS TABLE
CREATE TABLE student_registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Personal Information
    full_name VARCHAR(150) NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    date_of_birth DATE NOT NULL,
    place_of_birth VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    emergency_contact_name VARCHAR(150) NOT NULL,
    emergency_contact_phone VARCHAR(20) NOT NULL,
    emergency_contact_relation VARCHAR(50) NOT NULL,
    
    -- Educational Information
    education_level VARCHAR(50) NOT NULL,
    school_name VARCHAR(150),
    quran_reading_experience TEXT,
    previous_tahsin_experience BOOLEAN DEFAULT false,
    previous_tahsin_details TEXT,
    
    -- Program Selection
    id_program UUID NOT NULL REFERENCES programs(id),
    registration_reason TEXT,
    learning_goals TEXT,
    
    -- Schedule Preferences (JSON array of session preferences with priority)
    schedule_preferences JSONB NOT NULL,
    
    -- Placement Test Information
    id_placement_verse UUID REFERENCES placement_test_verses(id),
    recording_drive_link VARCHAR(500),
    placement_test_status VARCHAR(20) DEFAULT 'PENDING' CHECK (placement_test_status IN ('PENDING', 'SUBMITTED', 'EVALUATED')),
    placement_result INTEGER CHECK (placement_result >= 1 AND placement_result <= 5),
    placement_notes TEXT,
    placement_evaluated_by UUID REFERENCES users(id),
    placement_evaluated_at TIMESTAMP,
    
    -- Registration Status and Metadata
    registration_status VARCHAR(20) DEFAULT 'DRAFT' CHECK (registration_status IN ('DRAFT', 'SUBMITTED', 'ASSIGNED', 'UNDER_REVIEW', 'REVIEWED', 'COMPLETED', 'APPROVED', 'REJECTED')),
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by UUID REFERENCES users(id),
    review_notes TEXT,
    id_assigned_teacher UUID REFERENCES users(id),
    assigned_at TIMESTAMP,
    id_assigned_by UUID REFERENCES users(id),
    teacher_evaluation_notes TEXT,
    teacher_evaluated_at TIMESTAMP,
    teacher_review_status VARCHAR(20) DEFAULT 'PENDING' CHECK (teacher_review_status IN ('PENDING', 'IN_REVIEW', 'COMPLETED')),
    teacher_remarks TEXT,
    id_recommended_level UUID REFERENCES levels(id),
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT chk_age CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '5 years'),
    CONSTRAINT chk_phone_format CHECK (phone_number ~ '^[0-9+\-\s()]+$'),
    CONSTRAINT chk_email_format CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- STUDENT REGISTRATION SESSION PREFERENCES TABLE
CREATE TABLE student_session_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_registration UUID NOT NULL REFERENCES student_registrations(id) ON DELETE CASCADE,
    id_time_slot UUID NOT NULL REFERENCES time_slot(id),
    preference_priority INTEGER NOT NULL CHECK (preference_priority >= 1 AND preference_priority <= 3),
    created_at TIMESTAMP DEFAULT NOW(),

    -- Constraints
    UNIQUE (id_registration, preference_priority),
    UNIQUE (id_registration, id_time_slot)
);

-- AUDIT LOG TABLE FOR REGISTRATION CHANGES
CREATE TABLE student_registration_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_registration UUID NOT NULL REFERENCES student_registrations(id),
    changed_by UUID REFERENCES users(id),
    change_type VARCHAR(20) NOT NULL CHECK (change_type IN ('CREATE', 'UPDATE', 'STATUS_CHANGE', 'PLACEMENT_EVAL')),
    old_values JSONB,
    new_values JSONB,
    change_reason TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Additional indexes for student registration system
CREATE INDEX idx_programs_level ON programs (id_level);
CREATE INDEX idx_programs_code ON programs (code);
CREATE INDEX idx_sessions_code ON sessions (code);
CREATE INDEX idx_sessions_time ON sessions (start_time, end_time);
CREATE INDEX idx_placement_verses_surah ON placement_test_verses (surah_number);
CREATE INDEX idx_placement_verses_difficulty ON placement_test_verses (difficulty_level);
CREATE INDEX idx_placement_verses_active ON placement_test_verses (is_active);
CREATE INDEX idx_student_reg_status ON student_registrations (registration_status);
CREATE INDEX idx_student_reg_program ON student_registrations (id_program);
CREATE INDEX idx_student_reg_placement_status ON student_registrations (placement_test_status);
CREATE INDEX idx_student_reg_email ON student_registrations (email);
CREATE INDEX idx_student_reg_phone ON student_registrations (phone_number);
CREATE INDEX idx_student_reg_created ON student_registrations (created_at);
CREATE INDEX idx_session_pref_registration ON student_session_preferences (id_registration);
CREATE INDEX idx_session_pref_time_slot ON student_session_preferences (id_time_slot);
CREATE INDEX idx_session_pref_priority ON student_session_preferences (preference_priority);
CREATE INDEX idx_reg_audit_registration ON student_registration_audit (id_registration);
CREATE INDEX idx_reg_audit_changed_by ON student_registration_audit (changed_by);
CREATE INDEX idx_reg_audit_type ON student_registration_audit (change_type);
CREATE INDEX idx_reg_audit_created ON student_registration_audit (created_at);

-- =====================================================
-- CLASS PREPARATION SCHEMA
-- =====================================================

-- Generated Class Proposals Table
CREATE TABLE generated_class_proposals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    generation_run INTEGER NOT NULL,
    proposal_data JSONB NOT NULL,
    optimization_score NUMERIC(5,2),
    conflict_count INTEGER DEFAULT 0,
    size_violations JSONB,
    manual_overrides JSONB,
    generation_parameters JSONB,
    generated_by UUID REFERENCES users(id),
    generated_at TIMESTAMP DEFAULT NOW(),
    is_approved BOOLEAN DEFAULT false,
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP
);

-- Class Generation Log Table
CREATE TABLE class_generation_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    id_proposal UUID REFERENCES generated_class_proposals(id),
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('GENERATION', 'MANUAL_EDIT', 'APPROVAL', 'REJECTION', 'PUBLICATION')),
    action_description TEXT,
    old_data JSONB,
    new_data JSONB,
    performed_by UUID REFERENCES users(id),
    performed_at TIMESTAMP DEFAULT NOW(),
    ip_address INET,
    user_agent TEXT
);

-- Class Sessions Table already created above to resolve dependency

-- Class Session Objectives Table (ElementCollection)
CREATE TABLE class_session_objectives (
    class_session_id UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    learning_objective TEXT NOT NULL
);

-- Class Preparation Checklist Table
CREATE TABLE class_preparation_checklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id),
    checklist_item VARCHAR(200) NOT NULL,
    item_category VARCHAR(50) CHECK (item_category IN ('PLANNING', 'MATERIALS', 'ASSESSMENT', 'SETUP')),
    is_completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    completed_by UUID REFERENCES users(id),
    notes TEXT,
    display_order INTEGER DEFAULT 0
);

-- Session Materials Table
CREATE TABLE session_materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id),
    material_type VARCHAR(50) NOT NULL CHECK (material_type IN ('AUDIO', 'TEXT', 'VIDEO', 'WORKSHEET', 'PRESENTATION', 'OTHER')),
    file_path VARCHAR(500),
    material_title VARCHAR(200) NOT NULL,
    material_description TEXT,
    file_size BIGINT,
    mime_type VARCHAR(100),
    is_shared_with_students BOOLEAN DEFAULT false,
    upload_date TIMESTAMP DEFAULT NOW(),
    uploaded_by UUID REFERENCES users(id)
);

-- Class Size Configuration Table
CREATE TABLE class_size_configuration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(50) UNIQUE NOT NULL,
    config_value INTEGER NOT NULL,
    level_id UUID REFERENCES levels(id),
    description TEXT,
    updated_by UUID REFERENCES users(id),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Student Assessment Table
CREATE TABLE student_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_student UUID NOT NULL REFERENCES users(id),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    student_category VARCHAR(20) NOT NULL CHECK (student_category IN ('NEW', 'EXISTING')),
    assessment_type VARCHAR(30) NOT NULL CHECK (assessment_type IN ('PLACEMENT', 'MIDTERM', 'FINAL')),
    assessment_score NUMERIC(5,2) CHECK (assessment_score >= 0 AND assessment_score <= 100),
    assessment_grade VARCHAR(5),
    determined_level UUID REFERENCES levels(id),
    previous_class_group UUID REFERENCES class_groups(id),
    assessment_date DATE,
    assessment_notes TEXT,
    assessed_by UUID REFERENCES users(id),
    is_validated BOOLEAN DEFAULT false,
    validated_by UUID REFERENCES users(id),
    validated_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Teacher Availability Table
CREATE TABLE teacher_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    id_teacher UUID NOT NULL REFERENCES users(id),
    id_time_slot UUID NOT NULL REFERENCES time_slot(id),
    is_available BOOLEAN DEFAULT true,
    capacity INTEGER DEFAULT 1,
    max_classes_per_week INTEGER DEFAULT 6,
    preferences TEXT,
    submitted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_teacher_schedule UNIQUE (id_term, id_teacher, id_time_slot)
);

-- Teacher Level Assignments Table
CREATE TABLE teacher_level_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    id_teacher UUID NOT NULL REFERENCES users(id),
    id_level UUID NOT NULL REFERENCES levels(id),
    competency_level VARCHAR(20) NOT NULL CHECK (competency_level IN ('JUNIOR', 'SENIOR', 'EXPERT')),
    max_classes_for_level INTEGER,
    specialization VARCHAR(50) CHECK (specialization IN ('FOUNDATION', 'REMEDIAL', 'ADVANCED', 'MIXED')),
    assigned_by UUID REFERENCES users(id),
    assigned_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_teacher_level UNIQUE (id_term, id_teacher, id_level)
);


-- Indexes for class preparation tables
CREATE INDEX idx_academic_terms_status ON academic_terms (status);
CREATE INDEX idx_academic_terms_dates ON academic_terms (start_date, end_date);
CREATE INDEX idx_student_assessments_student ON student_assessments (id_student);
CREATE INDEX idx_student_assessments_term ON student_assessments (id_term);
CREATE INDEX idx_student_assessments_category ON student_assessments (student_category);
CREATE INDEX idx_student_assessments_validated ON student_assessments (is_validated);
CREATE INDEX idx_teacher_availability_term ON teacher_availability (id_term);
CREATE INDEX idx_teacher_availability_teacher ON teacher_availability (id_teacher);
CREATE INDEX idx_teacher_availability_time_slot ON teacher_availability (id_time_slot);
CREATE INDEX idx_teacher_level_assignments_term ON teacher_level_assignments (id_term);
CREATE INDEX idx_teacher_level_assignments_teacher ON teacher_level_assignments (id_teacher);
CREATE INDEX idx_teacher_level_assignments_level ON teacher_level_assignments (id_level);
CREATE INDEX idx_class_sessions_class_group ON class_sessions (id_class_group);
CREATE INDEX idx_class_sessions_date ON class_sessions (session_date);
CREATE INDEX idx_class_sessions_instructor ON class_sessions (id_instructor);
CREATE INDEX idx_class_session_objectives_session ON class_session_objectives (class_session_id);
CREATE INDEX idx_class_preparation_session ON class_preparation_checklist (id_session);
CREATE INDEX idx_class_preparation_completed ON class_preparation_checklist (is_completed);
CREATE INDEX idx_session_materials_session ON session_materials (id_session);
CREATE INDEX idx_session_materials_type ON session_materials (material_type);
CREATE INDEX idx_class_size_config_key ON class_size_configuration (config_key);
CREATE INDEX idx_class_size_config_level ON class_size_configuration (level_id);

-- =====================================================
-- DAILY OPERATIONS SCHEMA FOR SEMESTER ACTIVITIES
-- =====================================================

-- Session Execution and Check-in Enhancement
ALTER TABLE class_sessions 
ADD COLUMN session_status VARCHAR(30) DEFAULT 'SCHEDULED' 
    CHECK (session_status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'RESCHEDULED')),
ADD COLUMN actual_start_time TIMESTAMP,
ADD COLUMN actual_end_time TIMESTAMP,
ADD COLUMN session_notes TEXT,
ADD COLUMN learning_objectives JSONB,
ADD COLUMN objectives_achieved JSONB,
ADD COLUMN attendance_summary JSONB;

-- Teacher Check-in/Check-out Enhancement
ALTER TABLE teacher_attendance
ADD COLUMN check_in_time TIMESTAMP,
ADD COLUMN check_out_time TIMESTAMP,
ADD COLUMN check_in_location VARCHAR(200),
ADD COLUMN session_completed BOOLEAN DEFAULT false,
ADD COLUMN session_notes TEXT,
ADD COLUMN substitute_reason VARCHAR(50) 
    CHECK (substitute_reason IN ('ILLNESS', 'EMERGENCY', 'PLANNED_LEAVE', 'OTHER'));

-- Session Reschedule Requests
CREATE TABLE session_reschedule_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_class_session UUID NOT NULL REFERENCES class_sessions(id),
    id_requested_by UUID NOT NULL REFERENCES users(id),
    original_date DATE NOT NULL,
    original_time VARCHAR(20),
    proposed_date DATE NOT NULL,
    proposed_time VARCHAR(20),
    reason VARCHAR(50) NOT NULL CHECK (reason IN ('TEACHER_ILLNESS', 'EMERGENCY', 'HOLIDAY', 'FACILITY_ISSUE', 'OTHER')),
    reason_details TEXT,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    auto_approved BOOLEAN DEFAULT false,
    id_approved_by UUID REFERENCES users(id),
    approval_date TIMESTAMP,
    approval_notes TEXT,
    students_notified BOOLEAN DEFAULT false,
    notification_sent_at TIMESTAMP,
    id_new_session UUID REFERENCES class_sessions(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Substitute Teacher Pool
CREATE TABLE substitute_teachers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_teacher UUID NOT NULL REFERENCES users(id),
    is_available BOOLEAN DEFAULT true,
    emergency_available BOOLEAN DEFAULT false,
    hourly_rate DECIMAL(10,2),
    rating DECIMAL(3,2),
    total_substitutions INTEGER DEFAULT 0,
    last_substitution_date DATE,
    contact_preference VARCHAR(20) CHECK (contact_preference IN ('SMS', 'WHATSAPP', 'PHONE', 'EMAIL')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_teacher)
);

-- Substitute Assignments
CREATE TABLE substitute_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_class_session UUID NOT NULL REFERENCES class_sessions(id),
    id_original_teacher UUID NOT NULL REFERENCES users(id),
    id_substitute_teacher UUID NOT NULL REFERENCES users(id),
    assignment_type VARCHAR(20) CHECK (assignment_type IN ('EMERGENCY', 'PLANNED', 'TEMPORARY')),
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_assigned_by UUID NOT NULL REFERENCES users(id),
    reason TEXT,
    materials_shared BOOLEAN DEFAULT false,
    materials_shared_at TIMESTAMP,
    substitute_accepted BOOLEAN,
    acceptance_time TIMESTAMP,
    compensation_amount DECIMAL(10,2),
    special_instructions TEXT,
    performance_rating DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback Campaigns
CREATE TABLE feedback_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_name VARCHAR(200) NOT NULL,
    campaign_type VARCHAR(50) NOT NULL CHECK (campaign_type IN ('TEACHER_EVALUATION', 'FACILITY_ASSESSMENT', 'OVERALL_EXPERIENCE', 'CURRICULUM_FEEDBACK', 'CUSTOM')),
    target_audience VARCHAR(50) CHECK (target_audience IN ('STUDENTS', 'PARENTS', 'BOTH')),
    id_term UUID REFERENCES academic_terms(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_anonymous BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    template_id UUID,
    min_responses_required INTEGER,
    current_responses INTEGER DEFAULT 0,
    response_rate DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_created_by UUID REFERENCES users(id)
);

-- Feedback Questions Template
CREATE TABLE feedback_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_campaign UUID NOT NULL REFERENCES feedback_campaigns(id) ON DELETE CASCADE,
    question_number INTEGER NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL CHECK (question_type IN ('RATING', 'YES_NO', 'MULTIPLE_CHOICE', 'TEXT', 'SCALE')),
    question_category VARCHAR(50),
    is_required BOOLEAN DEFAULT true,
    options JSONB, -- For multiple choice questions
    min_value INTEGER, -- For scale questions
    max_value INTEGER, -- For scale questions
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_campaign, question_number)
);

-- Feedback Responses (Anonymous)
CREATE TABLE feedback_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_campaign UUID NOT NULL REFERENCES feedback_campaigns(id),
    anonymous_token VARCHAR(100) NOT NULL, -- Generated token instead of user ID for anonymity
    id_target_teacher UUID REFERENCES users(id), -- Teacher being evaluated (if applicable)
    id_class_group UUID REFERENCES class_groups(id), -- Class context (if applicable)
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_complete BOOLEAN DEFAULT false,
    device_info VARCHAR(200),
    UNIQUE(id_campaign, anonymous_token)
);

-- Feedback Answers
CREATE TABLE feedback_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_response UUID NOT NULL REFERENCES feedback_responses(id) ON DELETE CASCADE,
    id_question UUID NOT NULL REFERENCES feedback_questions(id),
    rating_value INTEGER, -- For rating questions
    boolean_value BOOLEAN, -- For yes/no questions
    text_value TEXT, -- For text questions
    selected_option VARCHAR(200), -- For multiple choice
    scale_value INTEGER, -- For scale questions
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_response, id_question)
);

-- Weekly Progress Records
CREATE TABLE weekly_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_enrollment UUID NOT NULL REFERENCES enrollments(id),
    id_teacher UUID NOT NULL REFERENCES users(id),
    week_number INTEGER NOT NULL,
    week_start_date DATE NOT NULL,
    week_end_date DATE NOT NULL,
    recitation_score DECIMAL(5,2),
    recitation_grade VARCHAR(5),
    memorization_progress TEXT,
    memorization_score DECIMAL(5,2),
    tajweed_score DECIMAL(5,2),
    tajweed_grade VARCHAR(5),
    participation_grade VARCHAR(5),
    attendance_count INTEGER,
    sessions_attended INTEGER,
    sessions_total INTEGER,
    teacher_notes TEXT,
    areas_of_improvement TEXT,
    needs_support BOOLEAN DEFAULT false,
    support_reason TEXT,
    parent_communication_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_enrollment, week_number)
);

-- Session Monitoring Dashboard Data
CREATE TABLE session_monitoring (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    monitoring_date DATE NOT NULL DEFAULT CURRENT_DATE,
    total_sessions_scheduled INTEGER,
    sessions_in_progress INTEGER,
    sessions_completed INTEGER,
    sessions_cancelled INTEGER,
    teacher_check_ins INTEGER,
    teacher_absences INTEGER,
    average_student_attendance DECIMAL(5,2),
    alerts JSONB, -- Array of system alerts
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(monitoring_date)
);

-- System Alerts for Monitoring
CREATE TABLE system_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_type VARCHAR(50) NOT NULL CHECK (alert_type IN ('LATE_CHECK_IN', 'NO_CHECK_IN', 'LOW_ATTENDANCE', 'SESSION_NOT_STARTED', 'SUBSTITUTE_NEEDED', 'TECHNICAL_ISSUE')),
    severity VARCHAR(20) CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    id_class_session UUID REFERENCES class_sessions(id),
    id_teacher UUID REFERENCES users(id),
    alert_message TEXT NOT NULL,
    is_resolved BOOLEAN DEFAULT false,
    resolved_by UUID REFERENCES users(id),
    resolved_at TIMESTAMP,
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Parent Notifications Log
CREATE TABLE parent_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_student UUID NOT NULL REFERENCES users(id),
    id_parent UUID REFERENCES users(id), -- NULL if sent to phone/email directly
    notification_type VARCHAR(50) CHECK (notification_type IN ('TEACHER_CHANGE', 'SESSION_RESCHEDULE', 'PROGRESS_UPDATE', 'ATTENDANCE_ALERT', 'GENERAL')),
    subject VARCHAR(200),
    message TEXT NOT NULL,
    delivery_method VARCHAR(20) CHECK (delivery_method IN ('SMS', 'EMAIL', 'WHATSAPP', 'IN_APP')),
    recipient_contact VARCHAR(100), -- Phone or email
    is_sent BOOLEAN DEFAULT false,
    sent_at TIMESTAMP,
    is_read BOOLEAN DEFAULT false,
    read_at TIMESTAMP,
    id_related_session UUID REFERENCES class_sessions(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback Analytics Summary (Materialized View alternative as table)
CREATE TABLE feedback_analytics_summary (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_campaign UUID REFERENCES feedback_campaigns(id),
    id_teacher UUID REFERENCES users(id),
    analysis_period VARCHAR(20), -- 'WEEKLY', 'MONTHLY', 'TERM'
    period_start DATE,
    period_end DATE,
    total_responses INTEGER,
    response_rate DECIMAL(5,2),
    average_rating DECIMAL(3,2),
    teaching_quality_avg DECIMAL(3,2),
    communication_avg DECIMAL(3,2),
    punctuality_avg DECIMAL(3,2),
    fairness_avg DECIMAL(3,2),
    top_strengths JSONB,
    areas_for_improvement JSONB,
    action_items JSONB,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_campaign, id_teacher, period_start, period_end)
);

-- Indexes for performance
CREATE INDEX idx_session_reschedule_status ON session_reschedule_requests(status);
CREATE INDEX idx_session_reschedule_date ON session_reschedule_requests(proposed_date);
CREATE INDEX idx_session_reschedule_new_session ON session_reschedule_requests(id_new_session);
CREATE INDEX idx_substitute_available ON substitute_teachers(is_available, emergency_available);
CREATE INDEX idx_substitute_assignments_session ON substitute_assignments(id_class_session);
CREATE INDEX idx_feedback_campaign_active ON feedback_campaigns(is_active, start_date, end_date);
CREATE INDEX idx_feedback_responses_campaign ON feedback_responses(id_campaign);
CREATE INDEX idx_weekly_progress_enrollment ON weekly_progress(id_enrollment, week_number);
CREATE INDEX idx_system_alerts_unresolved ON system_alerts(is_resolved, severity);
CREATE INDEX idx_parent_notifications_student ON parent_notifications(id_student, sent_at);
CREATE INDEX idx_class_sessions_status ON class_sessions(session_status, session_date);
CREATE INDEX idx_teacher_attendance_checkin ON teacher_attendance(id_class_session, check_in_time);

-- Add comments for documentation
COMMENT ON TABLE session_reschedule_requests IS 'Tracks all session reschedule requests with approval workflow';
COMMENT ON TABLE substitute_teachers IS 'Pool of available substitute teachers with their qualifications';
COMMENT ON TABLE feedback_campaigns IS 'Anonymous feedback campaigns for quality assurance';
COMMENT ON TABLE weekly_progress IS 'Weekly student progress tracking by teachers';
COMMENT ON TABLE session_monitoring IS 'Real-time dashboard data for academic administrators';
COMMENT ON TABLE system_alerts IS 'System-generated alerts for monitoring and intervention';
COMMENT ON TABLE parent_notifications IS 'Log of all parent communications and notifications';

-- =====================================================
-- EXAM SYSTEM TABLES
-- =====================================================

-- Create exams table
CREATE TABLE exams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    exam_type VARCHAR(20) NOT NULL CHECK (exam_type IN ('PLACEMENT', 'MIDTERM', 'FINAL', 'QUIZ', 'PRACTICE')),
    id_class_group UUID NOT NULL REFERENCES class_groups(id) ON DELETE RESTRICT,
    id_academic_term UUID NOT NULL REFERENCES academic_terms(id) ON DELETE RESTRICT,
    scheduled_start TIMESTAMP NOT NULL,
    scheduled_end TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    max_attempts INTEGER NOT NULL DEFAULT 1 CHECK (max_attempts > 0),
    passing_score DECIMAL(5,2) NOT NULL DEFAULT 60.0 CHECK (passing_score >= 0 AND passing_score <= 100),
    total_points DECIMAL(8,2) NOT NULL DEFAULT 100.0 CHECK (total_points > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    instructions TEXT,
    allow_review BOOLEAN NOT NULL DEFAULT TRUE,
    randomize_questions BOOLEAN NOT NULL DEFAULT FALSE,
    show_results_immediately BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_exam_schedule CHECK (scheduled_end > scheduled_start),
    CONSTRAINT chk_exam_duration CHECK (EXTRACT(EPOCH FROM (scheduled_end - scheduled_start))/60 >= duration_minutes)
);

-- Create exam_questions table
CREATE TABLE exam_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_exam UUID NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    question_number INTEGER NOT NULL CHECK (question_number > 0),
    question_type VARCHAR(20) NOT NULL CHECK (question_type IN ('MULTIPLE_CHOICE', 'MULTIPLE_SELECT', 'TRUE_FALSE', 'SHORT_ANSWER', 'ESSAY', 'FILL_BLANK', 'MATCHING', 'RECITATION')),
    question_text TEXT NOT NULL,
    points DECIMAL(8,2) NOT NULL DEFAULT 1.0 CHECK (points > 0),
    required BOOLEAN NOT NULL DEFAULT TRUE,
    question_data JSONB, -- Stores question-specific data (options, correct answers, etc.)
    explanation TEXT,
    time_limit_seconds INTEGER CHECK (time_limit_seconds > 0),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_exam_questions_number UNIQUE (id_exam, question_number)
);

-- Create exam_results table
CREATE TABLE exam_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_exam UUID NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    id_student UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    attempt_number INTEGER NOT NULL DEFAULT 1 CHECK (attempt_number > 0),
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    time_taken_seconds BIGINT CHECK (time_taken_seconds >= 0),
    total_score DECIMAL(8,2) CHECK (total_score >= 0),
    percentage_score DECIMAL(5,2) CHECK (percentage_score >= 0 AND percentage_score <= 100),
    points_earned DECIMAL(8,2) CHECK (points_earned >= 0),
    points_possible DECIMAL(8,2) CHECK (points_possible > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'SUBMITTED', 'GRADED', 'ABANDONED', 'TIME_EXPIRED')),
    grade VARCHAR(5) CHECK (grade IN ('A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'D-', 'F')),
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    auto_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    instructor_feedback TEXT,
    graded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    graded_at TIMESTAMP,
    metadata JSONB, -- Additional metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_exam_results_attempt UNIQUE (id_exam, id_student, attempt_number),
    CONSTRAINT chk_exam_result_completion CHECK (
        (status = 'IN_PROGRESS' AND completed_at IS NULL) OR 
        (status != 'IN_PROGRESS' AND completed_at IS NOT NULL)
    ),
    CONSTRAINT chk_exam_result_grading CHECK (
        (status = 'GRADED' AND graded_by IS NOT NULL AND graded_at IS NOT NULL) OR
        (status != 'GRADED')
    )
);

-- Create exam_answers table
CREATE TABLE exam_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_exam_result UUID NOT NULL REFERENCES exam_results(id) ON DELETE CASCADE,
    id_exam_question UUID NOT NULL REFERENCES exam_questions(id) ON DELETE CASCADE,
    answer_data JSONB, -- Stores the actual answer data (JSON format for flexibility)
    points_earned DECIMAL(8,2) CHECK (points_earned >= 0),
    points_possible DECIMAL(8,2) CHECK (points_possible > 0),
    is_correct BOOLEAN,
    grading_status VARCHAR(20) NOT NULL DEFAULT 'NOT_GRADED' CHECK (grading_status IN ('NOT_GRADED', 'AUTO_GRADED', 'MANUAL_GRADED', 'PENDING_REVIEW', 'FLAGGED')),
    instructor_feedback TEXT,
    graded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    graded_at TIMESTAMP,
    time_spent_seconds BIGINT CHECK (time_spent_seconds >= 0),
    answer_sequence INTEGER, -- For tracking order of answers
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_exam_answers_question UNIQUE (id_exam_result, id_exam_question),
    CONSTRAINT chk_exam_answer_grading CHECK (
        (grading_status IN ('MANUAL_GRADED') AND graded_by IS NOT NULL AND graded_at IS NOT NULL) OR
        (grading_status NOT IN ('MANUAL_GRADED'))
    )
);

-- Create indexes for exam system performance
CREATE INDEX idx_exams_class_group ON exams(id_class_group);
CREATE INDEX idx_exams_academic_term ON exams(id_academic_term);
CREATE INDEX idx_exams_status ON exams(status);
CREATE INDEX idx_exams_scheduled_start ON exams(scheduled_start);
CREATE INDEX idx_exams_created_by ON exams(created_by);

CREATE INDEX idx_exam_questions_exam ON exam_questions(id_exam);
CREATE INDEX idx_exam_questions_type ON exam_questions(question_type);
CREATE INDEX idx_exam_questions_number ON exam_questions(id_exam, question_number);

CREATE INDEX idx_exam_results_exam ON exam_results(id_exam);
CREATE INDEX idx_exam_results_student ON exam_results(id_student);
CREATE INDEX idx_exam_results_status ON exam_results(status);
CREATE INDEX idx_exam_results_completed_at ON exam_results(completed_at);
CREATE INDEX idx_exam_results_exam_student ON exam_results(id_exam, id_student);

CREATE INDEX idx_exam_answers_result ON exam_answers(id_exam_result);
CREATE INDEX idx_exam_answers_question ON exam_answers(id_exam_question);
CREATE INDEX idx_exam_answers_grading_status ON exam_answers(grading_status);

-- Timestamps are handled by Spring Data JPA auditing

-- Add exam system comments
COMMENT ON TABLE exams IS 'Stores exam definitions with scheduling and configuration';
COMMENT ON TABLE exam_questions IS 'Stores individual questions within exams with flexible JSON data structure';
COMMENT ON TABLE exam_results IS 'Stores student exam attempt results and grading information';
COMMENT ON TABLE exam_answers IS 'Stores individual question answers within exam attempts';
COMMENT ON COLUMN exam_questions.question_data IS 'JSON data structure for question-specific information (options, correct answers, matching pairs, etc.)';
COMMENT ON COLUMN exam_results.metadata IS 'Additional metadata for exam results (browser info, IP address, etc.)';

-- Add teacher availability change request table
CREATE TABLE teacher_availability_change_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_teacher UUID NOT NULL REFERENCES users(id),
    id_term UUID NOT NULL REFERENCES academic_terms(id),
    reason TEXT NOT NULL,
    requested_changes JSONB,
    original_max_classes INTEGER,
    new_max_classes INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMP,
    review_comments TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for better performance
CREATE INDEX idx_teacher_availability_change_request_teacher ON teacher_availability_change_request(id_teacher);
CREATE INDEX idx_teacher_availability_change_request_term ON teacher_availability_change_request(id_term);
CREATE INDEX idx_teacher_availability_change_request_status ON teacher_availability_change_request(status);
CREATE INDEX idx_teacher_availability_change_request_submitted_at ON teacher_availability_change_request(submitted_at);

-- Add unique constraint to prevent multiple pending requests per teacher per term
CREATE UNIQUE INDEX idx_teacher_availability_change_request_unique_pending
ON teacher_availability_change_request(id_teacher, id_term)
WHERE status = 'PENDING';

-- Add comments for documentation
COMMENT ON TABLE teacher_availability_change_request IS 'Teacher requests to change their submitted availability schedule';
COMMENT ON COLUMN teacher_availability_change_request.requested_changes IS 'JSON array of availability changes (add/remove/modify slots)';
COMMENT ON COLUMN teacher_availability_change_request.status IS 'Request status: PENDING, APPROVED, REJECTED, CANCELLED';
COMMENT ON COLUMN teacher_availability_change_request.reason IS 'Teacher explanation for the requested changes';
COMMENT ON COLUMN exam_answers.answer_data IS 'JSON data structure storing student answers in flexible format';

-- =====================================================
-- INSTRUCTOR VALIDATION SYSTEM TABLES
-- =====================================================

-- Equipment Issues table for tracking equipment problems during sessions
CREATE TABLE equipment_issues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    id_reported_by UUID NOT NULL REFERENCES users(id),
    equipment_type VARCHAR(50) NOT NULL CHECK (equipment_type IN (
        'PROJECTOR', 'SOUND_SYSTEM', 'MICROPHONE', 'COMPUTER',
        'LIGHTING', 'AIR_CONDITIONING', 'WHITEBOARD', 'FURNITURE', 'OTHER'
    )),
    issue_description TEXT NOT NULL,
    is_urgent BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'REPORTED' CHECK (status IN (
        'REPORTED', 'MAINTENANCE_NOTIFIED', 'IN_PROGRESS', 'RESOLVED', 'CANCELLED'
    )),
    tracking_number VARCHAR(20) UNIQUE,
    maintenance_notified_at TIMESTAMP,
    resolution_notes TEXT,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Guest Students table for tracking additional students in sessions
CREATE TABLE guest_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    guest_name VARCHAR(100) NOT NULL,
    reason TEXT,
    guest_type VARCHAR(30) NOT NULL CHECK (guest_type IN (
        'TRIAL_CLASS', 'MAKEUP_CLASS', 'VISITOR', 'SUBSTITUTE', 'OTHER'
    )),
    id_added_by UUID NOT NULL REFERENCES users(id),
    is_present BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Emergency Terminations table for tracking emergency session terminations
CREATE TABLE emergency_terminations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_session UUID NOT NULL REFERENCES class_sessions(id) ON DELETE CASCADE,
    id_terminated_by UUID NOT NULL REFERENCES users(id),
    emergency_type VARCHAR(50) NOT NULL CHECK (emergency_type IN (
        'FIRE_EVACUATION', 'MEDICAL_EMERGENCY', 'NATURAL_DISASTER',
        'SECURITY_THREAT', 'POWER_OUTAGE', 'BUILDING_EVACUATION',
        'WEATHER_EMERGENCY', 'OTHER'
    )),
    emergency_reason TEXT NOT NULL,
    tracking_number VARCHAR(20) UNIQUE,
    notifications_sent BOOLEAN NOT NULL DEFAULT FALSE,
    stakeholder_notification_at TIMESTAMP,
    parent_notification_at TIMESTAMP,
    emergency_report_generated BOOLEAN NOT NULL DEFAULT FALSE,
    session_data_preserved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =====================================================
-- INDEXES FOR INSTRUCTOR VALIDATION SYSTEM
-- =====================================================

-- Equipment Issues indexes
CREATE INDEX idx_equipment_issues_session ON equipment_issues(id_session);
CREATE INDEX idx_equipment_issues_status ON equipment_issues(status);
CREATE INDEX idx_equipment_issues_urgent ON equipment_issues(is_urgent);
CREATE INDEX idx_equipment_issues_created_at ON equipment_issues(created_at);
CREATE INDEX idx_equipment_issues_tracking_number ON equipment_issues(tracking_number);

-- Guest Students indexes
CREATE INDEX idx_guest_students_session ON guest_students(id_session);
CREATE INDEX idx_guest_students_type ON guest_students(guest_type);
CREATE INDEX idx_guest_students_created_at ON guest_students(created_at);

-- Emergency Terminations indexes
CREATE INDEX idx_emergency_terminations_session ON emergency_terminations(id_session);
CREATE INDEX idx_emergency_terminations_type ON emergency_terminations(emergency_type);
CREATE INDEX idx_emergency_terminations_created_at ON emergency_terminations(created_at);
CREATE INDEX idx_emergency_terminations_tracking_number ON emergency_terminations(tracking_number);

-- =====================================================
-- COMMENTS FOR INSTRUCTOR VALIDATION SYSTEM
-- =====================================================

-- Equipment Issues comments
COMMENT ON TABLE equipment_issues IS 'Equipment issues reported during class sessions';
COMMENT ON COLUMN equipment_issues.equipment_type IS 'Type of equipment that has issues';
COMMENT ON COLUMN equipment_issues.is_urgent IS 'Whether the issue requires immediate attention';
COMMENT ON COLUMN equipment_issues.status IS 'Current status of the issue resolution';
COMMENT ON COLUMN equipment_issues.tracking_number IS 'Unique tracking number for the issue';

-- Guest Students comments
COMMENT ON TABLE guest_students IS 'Guest students added to sessions for attendance discrepancy handling';
COMMENT ON COLUMN guest_students.guest_type IS 'Type of guest student (trial, makeup, visitor, etc.)';
COMMENT ON COLUMN guest_students.reason IS 'Reason for adding this guest student';
COMMENT ON COLUMN guest_students.is_present IS 'Whether the guest student is marked as present';

-- Emergency Terminations comments
COMMENT ON TABLE emergency_terminations IS 'Emergency terminations of class sessions';
COMMENT ON COLUMN emergency_terminations.emergency_type IS 'Type of emergency that caused termination';
COMMENT ON COLUMN emergency_terminations.emergency_reason IS 'Detailed reason for emergency termination';
COMMENT ON COLUMN emergency_terminations.tracking_number IS 'Unique tracking number for the emergency incident';
COMMENT ON COLUMN emergency_terminations.notifications_sent IS 'Whether notifications have been sent to stakeholders';
COMMENT ON COLUMN emergency_terminations.emergency_report_generated IS 'Whether emergency report has been generated';
COMMENT ON COLUMN emergency_terminations.session_data_preserved IS 'Whether session data was successfully preserved';