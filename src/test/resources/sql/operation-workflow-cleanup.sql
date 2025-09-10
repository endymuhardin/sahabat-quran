-- Operation Workflow Test Cleanup SQL
-- This script cleans up test data after Academic Admin operation workflow tests

-- Delete test data in reverse order of dependencies
DELETE FROM feedback_answers WHERE id_response IN (SELECT id FROM feedback_responses WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid));
DELETE FROM feedback_responses WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);
DELETE FROM feedback_questions WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);
DELETE FROM feedback_campaigns WHERE id IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);

-- Delete system alerts first (before class_sessions due to foreign key)
DELETE FROM system_alerts WHERE id IN ('850e8400-e29b-41d4-a716-446655440001'::uuid, '850e8400-e29b-41d4-a716-446655440002'::uuid, '850e8400-e29b-41d4-a716-446655440003'::uuid, '850e8400-e29b-41d4-a716-446655440004'::uuid);

DELETE FROM attendance WHERE id_enrollment IN (SELECT e.id FROM enrollments e INNER JOIN class_groups cg ON e.id_class_group = cg.id INNER JOIN class_sessions cs ON cs.id_class_group = cg.id WHERE cs.session_date = CURRENT_DATE AND cs.id_instructor = '20000000-0000-0000-0000-000000000001'::uuid) AND attendance_date = CURRENT_DATE;
DELETE FROM teacher_attendance WHERE id_class_session IN (SELECT id FROM class_sessions WHERE session_date = CURRENT_DATE AND id_instructor = '20000000-0000-0000-0000-000000000001'::uuid);
DELETE FROM class_sessions WHERE session_date = CURRENT_DATE AND id_instructor = '20000000-0000-0000-0000-000000000001'::uuid;

DELETE FROM substitute_assignments WHERE id_substitute_teacher IN ('20000000-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000003'::uuid);
DELETE FROM substitute_teachers WHERE id IN ('550e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid);

-- Clean up any orphaned notification records
DELETE FROM parent_notifications WHERE created_at >= CURRENT_DATE;