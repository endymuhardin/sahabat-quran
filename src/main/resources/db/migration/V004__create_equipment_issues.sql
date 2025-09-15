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

-- Indexes for better performance
CREATE INDEX idx_equipment_issues_session ON equipment_issues(id_session);
CREATE INDEX idx_equipment_issues_status ON equipment_issues(status);
CREATE INDEX idx_equipment_issues_urgent ON equipment_issues(is_urgent);
CREATE INDEX idx_equipment_issues_created_at ON equipment_issues(created_at);
CREATE INDEX idx_equipment_issues_tracking_number ON equipment_issues(tracking_number);

-- Comments
COMMENT ON TABLE equipment_issues IS 'Equipment issues reported during class sessions';
COMMENT ON COLUMN equipment_issues.equipment_type IS 'Type of equipment that has issues';
COMMENT ON COLUMN equipment_issues.is_urgent IS 'Whether the issue requires immediate attention';
COMMENT ON COLUMN equipment_issues.status IS 'Current status of the issue resolution';
COMMENT ON COLUMN equipment_issues.tracking_number IS 'Unique tracking number for the issue';