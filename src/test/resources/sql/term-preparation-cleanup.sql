-- Term Preparation Documentation Test Cleanup
-- Removes all test data created for the term preparation workflow demonstration

-- Delete in reverse order of dependencies

-- Delete class-related data
DELETE FROM class_sessions WHERE id_class_group IN (
    SELECT cg.id FROM class_groups cg 
    JOIN academic_terms at ON cg.id_term = at.id 
    WHERE at.term_name LIKE 'TEST_%'
);
DELETE FROM enrollments WHERE id_class_group IN (
    SELECT cg.id FROM class_groups cg 
    JOIN academic_terms at ON cg.id_term = at.id 
    WHERE at.term_name LIKE 'TEST_%'
);
DELETE FROM class_preparation_checklist WHERE id_session IN (
    SELECT cs.id FROM class_sessions cs
    JOIN class_groups cg ON cs.id_class_group = cg.id
    JOIN academic_terms at ON cg.id_term = at.id
    WHERE at.term_name LIKE 'TEST_%'
);
DELETE FROM session_materials WHERE id_session IN (
    SELECT cs.id FROM class_sessions cs
    JOIN class_groups cg ON cs.id_class_group = cg.id
    JOIN academic_terms at ON cg.id_term = at.id
    WHERE at.term_name LIKE 'TEST_%'
);

-- Delete class groups
DELETE FROM class_groups WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Delete teacher availability
DELETE FROM teacher_availability WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Delete teacher level assignments
DELETE FROM teacher_level_assignments WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Delete generated class proposals
DELETE FROM generated_class_proposals WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Delete student assessments
DELETE FROM student_assessments WHERE id_term IN (SELECT id FROM academic_terms WHERE term_name LIKE 'TEST_%');

-- Delete academic terms
DELETE FROM academic_terms WHERE term_name LIKE 'TEST_%';

-- Delete test class size configurations (if any were created with TEST_ prefix)
DELETE FROM class_size_configuration WHERE config_key LIKE 'TEST_%';

-- Log cleanup summary
SELECT 'Term Preparation Test Data Cleanup Complete' AS status,
       NOW() AS cleanup_time;