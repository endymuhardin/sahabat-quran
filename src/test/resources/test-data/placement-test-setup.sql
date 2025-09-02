-- =======================================================
-- PLACEMENT TEST VERSE TEST DATA SETUP
-- This script sets up clean test data for PlacementTestVerse tests
-- =======================================================

-- Clean existing test data first (delete referencing tables first to avoid FK constraints)
DELETE FROM student_registrations WHERE id_placement_verse IN (
    SELECT id FROM placement_test_verses WHERE 
        arabic_text LIKE 'Test Arabic Text%' 
        OR surah_name LIKE 'Test%'
        OR id::text LIKE 'b0000000%'
);
DELETE FROM placement_test_verses WHERE 
    arabic_text LIKE 'Test Arabic Text%' 
    OR surah_name LIKE 'Test%'
    OR id::text LIKE 'b0000000%';

-- Insert exactly the test data expected by tests
-- Only 3 verses with difficulty levels 1, 2, 3
INSERT INTO placement_test_verses (
    id, surah_number, surah_name, ayah_start, ayah_end, 
    arabic_text, difficulty_level, is_active,
    created_at, updated_at
) VALUES 
-- Level 1 verse
('b0000000-0000-0000-0000-000000000001', 1, 'Al-Fatihah', 1, 7, 
 'Test Arabic Text for Al-Fatihah', 1, true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Level 2 verse
('b0000000-0000-0000-0000-000000000002', 2, 'Al-Baqarah', 1, 5, 
 'Test Arabic Text for Al-Baqarah', 2, true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Level 3 verse
('b0000000-0000-0000-0000-000000000003', 2, 'Al-Baqarah', 255, 255, 
 'Test Arabic Text for Al-Baqarah', 3, true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);