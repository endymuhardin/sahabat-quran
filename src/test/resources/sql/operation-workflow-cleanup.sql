-- Operation Workflow Test Cleanup SQL
-- This script cleans up test data after Academic Admin operation workflow tests

-- Delete test data in reverse order of dependencies
DELETE FROM feedback_answers WHERE id_response IN (SELECT id FROM feedback_responses WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid));
DELETE FROM feedback_responses WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);
DELETE FROM feedback_questions WHERE id_campaign IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);
DELETE FROM feedback_campaigns WHERE id IN ('650e8400-e29b-41d4-a716-446655440001'::uuid, '650e8400-e29b-41d4-a716-446655440002'::uuid);

-- Delete system alerts first (before class_sessions due to foreign key)
DELETE FROM system_alerts WHERE id IN ('850e8400-e29b-41d4-a716-446655440001'::uuid, '850e8400-e29b-41d4-a716-446655440002'::uuid, '850e8400-e29b-41d4-a716-446655440003'::uuid, '850e8400-e29b-41d4-a716-446655440004'::uuid);

-- Clean up attendance and teacher attendance first (referencing class_sessions)
DELETE FROM attendance WHERE id_enrollment IN (SELECT e.id FROM enrollments e INNER JOIN class_groups cg ON e.id_class_group = cg.id INNER JOIN class_sessions cs ON cs.id_class_group = cg.id WHERE (cs.session_date = CURRENT_DATE OR cs.session_date = CURRENT_DATE - INTERVAL '1 day') AND cs.id_instructor = '20000000-0000-0000-0000-000000000001'::uuid) AND (attendance_date = CURRENT_DATE OR attendance_date = CURRENT_DATE - INTERVAL '1 day');
DELETE FROM teacher_attendance WHERE id_class_session IN (SELECT id FROM class_sessions WHERE (session_date = CURRENT_DATE OR session_date = CURRENT_DATE - INTERVAL '1 day') AND id_instructor = '20000000-0000-0000-0000-000000000001'::uuid);

-- Delete all class sessions for the test instructor on current date and yesterday (including the specific test session)
DELETE FROM class_sessions WHERE (session_date = CURRENT_DATE OR session_date = CURRENT_DATE - INTERVAL '1 day') AND id_instructor = '20000000-0000-0000-0000-000000000001'::uuid;

DELETE FROM substitute_assignments WHERE id_substitute_teacher IN ('20000000-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000003'::uuid);
DELETE FROM substitute_teachers WHERE id IN ('550e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid);

-- Clean up any orphaned notification records
DELETE FROM parent_notifications WHERE created_at >= CURRENT_DATE;

-- Clean up student report test data
DELETE FROM student_assessments
WHERE id_student IN (
    SELECT id FROM users
    WHERE username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'invalid.email.test', 'ahmad.zaki.test', 'fatimah.zahra.test', 'siti.khadijah.test', 'testing.student1', 'testing.student2', 'testing.student3')
);

DELETE FROM enrollments
WHERE id_student IN (
    SELECT id FROM users
    WHERE username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'invalid.email.test', 'ahmad.zaki.test', 'fatimah.zahra.test', 'siti.khadijah.test', 'testing.student1', 'testing.student2', 'testing.student3')
);

DELETE FROM class_groups WHERE id = '12345678-1234-1234-1234-123456789012'::uuid;

DELETE FROM user_roles
WHERE id_user IN (
    SELECT id FROM users
    WHERE username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'invalid.email.test', 'ahmad.zaki.test', 'fatimah.zahra.test', 'siti.khadijah.test', 'testing.student1', 'testing.student2', 'testing.student3')
);

DELETE FROM users
WHERE username IN ('ahmad.fauzan.test', 'maria.santos.test', 'ali.rahman.test', 'invalid.email.test', 'ahmad.zaki.test', 'fatimah.zahra.test', 'siti.khadijah.test', 'testing.student1', 'testing.student2', 'testing.student3');