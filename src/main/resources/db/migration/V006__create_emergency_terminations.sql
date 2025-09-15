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

-- Indexes for better performance
CREATE INDEX idx_emergency_terminations_session ON emergency_terminations(id_session);
CREATE INDEX idx_emergency_terminations_type ON emergency_terminations(emergency_type);
CREATE INDEX idx_emergency_terminations_created_at ON emergency_terminations(created_at);
CREATE INDEX idx_emergency_terminations_tracking_number ON emergency_terminations(tracking_number);

-- Comments
COMMENT ON TABLE emergency_terminations IS 'Emergency terminations of class sessions';
COMMENT ON COLUMN emergency_terminations.emergency_type IS 'Type of emergency that caused termination';
COMMENT ON COLUMN emergency_terminations.emergency_reason IS 'Detailed reason for emergency termination';
COMMENT ON COLUMN emergency_terminations.tracking_number IS 'Unique tracking number for the emergency incident';
COMMENT ON COLUMN emergency_terminations.notifications_sent IS 'Whether notifications have been sent to stakeholders';
COMMENT ON COLUMN emergency_terminations.emergency_report_generated IS 'Whether emergency report has been generated';
COMMENT ON COLUMN emergency_terminations.session_data_preserved IS 'Whether session data was successfully preserved';