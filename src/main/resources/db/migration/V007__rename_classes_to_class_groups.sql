-- Rename classes table to class_groups to avoid keyword conflicts
-- This migration renames the table and updates all foreign key references

-- Step 1: Drop existing foreign key constraints that reference classes table
ALTER TABLE enrollments DROP CONSTRAINT IF EXISTS enrollments_id_class_fkey;
ALTER TABLE class_sessions DROP CONSTRAINT IF EXISTS class_sessions_id_class_fkey;
ALTER TABLE student_assessments DROP CONSTRAINT IF EXISTS student_assessments_previous_class_fkey;

-- Step 2: Drop existing indexes on classes table
DROP INDEX IF EXISTS idx_classes_level;
DROP INDEX IF EXISTS idx_classes_term;
DROP INDEX IF EXISTS idx_classes_instructor;
DROP INDEX IF EXISTS idx_classes_active;

-- Step 3: Drop existing triggers
DROP TRIGGER IF EXISTS update_classes_updated_at ON classes;

-- Step 4: Drop existing check constraints
ALTER TABLE classes DROP CONSTRAINT IF EXISTS chk_class_size_range;
ALTER TABLE classes DROP CONSTRAINT IF EXISTS chk_student_category_mix;
ALTER TABLE classes DROP CONSTRAINT IF EXISTS chk_class_type;

-- Step 5: Rename the table
ALTER TABLE classes RENAME TO class_groups;

-- Step 6: Rename the id_class column in related tables to id_class_group
ALTER TABLE enrollments RENAME COLUMN id_class TO id_class_group;
ALTER TABLE class_sessions RENAME COLUMN id_class TO id_class_group;
ALTER TABLE student_assessments RENAME COLUMN previous_class TO previous_class_group;

-- Step 7: Re-create foreign key constraints with new table name
ALTER TABLE enrollments 
    ADD CONSTRAINT enrollments_id_class_group_fkey 
    FOREIGN KEY (id_class_group) REFERENCES class_groups(id);

ALTER TABLE class_sessions 
    ADD CONSTRAINT class_sessions_id_class_group_fkey 
    FOREIGN KEY (id_class_group) REFERENCES class_groups(id) ON DELETE CASCADE;

ALTER TABLE student_assessments 
    ADD CONSTRAINT student_assessments_previous_class_group_fkey 
    FOREIGN KEY (previous_class_group) REFERENCES class_groups(id);

-- Step 8: Re-create indexes with new naming
CREATE INDEX idx_class_groups_level ON class_groups(id_level);
CREATE INDEX idx_class_groups_term ON class_groups(id_term);
CREATE INDEX idx_class_groups_instructor ON class_groups(id_instructor);
CREATE INDEX idx_class_groups_active ON class_groups(is_active);

-- Step 9: Re-create check constraints
ALTER TABLE class_groups ADD CONSTRAINT chk_class_group_size_range 
    CHECK (min_students <= max_students);

ALTER TABLE class_groups ADD CONSTRAINT chk_student_category_mix 
    CHECK (student_category_mix IN ('NEW_ONLY', 'EXISTING_ONLY', 'MIXED'));

ALTER TABLE class_groups ADD CONSTRAINT chk_class_type 
    CHECK (class_type IN ('FOUNDATION', 'STANDARD', 'REMEDIAL', 'ADVANCED'));

-- Step 10: Re-create the update trigger
CREATE TRIGGER update_class_groups_updated_at BEFORE UPDATE ON class_groups
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

-- Step 11: Update any views that might reference the old table name
-- (No views currently exist that reference classes table)

-- Step 12: Add comments to document the rename
COMMENT ON TABLE class_groups IS 'Table for managing class groups (renamed from classes to avoid keyword conflicts)';
COMMENT ON COLUMN enrollments.id_class_group IS 'Foreign key to class_groups table (renamed from id_class)';
COMMENT ON COLUMN class_sessions.id_class_group IS 'Foreign key to class_groups table (renamed from id_class)';
COMMENT ON COLUMN student_assessments.previous_class_group IS 'Reference to previous class group for existing students';