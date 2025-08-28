-- =====================================================
-- STUDENT REGISTRATION SYSTEM SCHEMA
-- Version: V003__student_registration_schema.sql
-- Description: Tables for student registration flow
-- =====================================================

-- =====================================================
-- 1. PROGRAMS REFERENCE TABLE
-- =====================================================
CREATE TABLE programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    level_order INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Index for better performance
CREATE INDEX idx_programs_level_order ON programs (level_order);
CREATE INDEX idx_programs_code ON programs (code);

-- =====================================================
-- 2. SESSIONS REFERENCE TABLE
-- =====================================================
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

-- Index for better performance
CREATE INDEX idx_sessions_code ON sessions (code);
CREATE INDEX idx_sessions_time ON sessions (start_time, end_time);

-- =====================================================
-- 3. PLACEMENT TEST VERSES TABLE
-- =====================================================
CREATE TABLE placement_test_verses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    surah_number INTEGER NOT NULL CHECK (surah_number >= 1 AND surah_number <= 114),
    surah_name VARCHAR(50) NOT NULL,
    ayah_start INTEGER NOT NULL CHECK (ayah_start >= 1),
    ayah_end INTEGER NOT NULL CHECK (ayah_end >= ayah_start),
    arabic_text TEXT NOT NULL,
    transliteration TEXT,
    difficulty_level INTEGER NOT NULL CHECK (difficulty_level >= 1 AND difficulty_level <= 5),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Index for better performance
CREATE INDEX idx_placement_verses_surah ON placement_test_verses (surah_number);
CREATE INDEX idx_placement_verses_difficulty ON placement_test_verses (difficulty_level);
CREATE INDEX idx_placement_verses_active ON placement_test_verses (is_active);

-- =====================================================
-- 4. STUDENT REGISTRATIONS TABLE
-- =====================================================
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
    registration_status VARCHAR(20) DEFAULT 'DRAFT' CHECK (registration_status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')),
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by UUID REFERENCES users(id),
    review_notes TEXT,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT chk_age CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '5 years'),
    CONSTRAINT chk_phone_format CHECK (phone_number ~ '^[0-9+\-\s()]+$'),
    CONSTRAINT chk_email_format CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Indexes for better performance
CREATE INDEX idx_student_reg_status ON student_registrations (registration_status);
CREATE INDEX idx_student_reg_program ON student_registrations (id_program);
CREATE INDEX idx_student_reg_placement_status ON student_registrations (placement_test_status);
CREATE INDEX idx_student_reg_email ON student_registrations (email);
CREATE INDEX idx_student_reg_phone ON student_registrations (phone_number);
CREATE INDEX idx_student_reg_created ON student_registrations (created_at);

-- =====================================================
-- 5. STUDENT REGISTRATION SESSION PREFERENCES TABLE
-- =====================================================
CREATE TABLE student_session_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_registration UUID NOT NULL REFERENCES student_registrations(id) ON DELETE CASCADE,
    id_session UUID NOT NULL REFERENCES sessions(id),
    preference_priority INTEGER NOT NULL CHECK (preference_priority >= 1 AND preference_priority <= 3),
    preferred_days JSONB NOT NULL, -- Array of day names: ["MONDAY", "TUESDAY", "WEDNESDAY", etc.]
    created_at TIMESTAMP DEFAULT NOW(),
    
    -- Constraints
    UNIQUE (id_registration, preference_priority),
    UNIQUE (id_registration, id_session)
);

-- Index for better performance
CREATE INDEX idx_session_pref_registration ON student_session_preferences (id_registration);
CREATE INDEX idx_session_pref_session ON student_session_preferences (id_session);
CREATE INDEX idx_session_pref_priority ON student_session_preferences (preference_priority);

-- =====================================================
-- 6. AUDIT LOG TABLE FOR REGISTRATION CHANGES
-- =====================================================
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

-- Index for better performance
CREATE INDEX idx_reg_audit_registration ON student_registration_audit (id_registration);
CREATE INDEX idx_reg_audit_changed_by ON student_registration_audit (changed_by);
CREATE INDEX idx_reg_audit_type ON student_registration_audit (change_type);
CREATE INDEX idx_reg_audit_created ON student_registration_audit (created_at);

-- =====================================================
-- 7. TRIGGER FOR UPDATED_AT TIMESTAMP
-- =====================================================
-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for updated_at
CREATE TRIGGER update_programs_updated_at
    BEFORE UPDATE ON programs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_sessions_updated_at
    BEFORE UPDATE ON sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_placement_verses_updated_at
    BEFORE UPDATE ON placement_test_verses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_student_registrations_updated_at
    BEFORE UPDATE ON student_registrations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();