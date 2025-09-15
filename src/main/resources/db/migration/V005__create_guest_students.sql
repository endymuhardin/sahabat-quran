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

-- Indexes for better performance
CREATE INDEX idx_guest_students_session ON guest_students(id_session);
CREATE INDEX idx_guest_students_type ON guest_students(guest_type);
CREATE INDEX idx_guest_students_created_at ON guest_students(created_at);

-- Comments
COMMENT ON TABLE guest_students IS 'Guest students added to sessions for attendance discrepancy handling';
COMMENT ON COLUMN guest_students.guest_type IS 'Type of guest student (trial, makeup, visitor, etc.)';
COMMENT ON COLUMN guest_students.reason IS 'Reason for adding this guest student';
COMMENT ON COLUMN guest_students.is_present IS 'Whether the guest student is marked as present';