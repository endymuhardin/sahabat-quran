-- Cleanup for Teacher Availability Change Request Test
-- This script removes test data after TA-HP-003 scenario

-- Remove test change requests
DELETE FROM teacher_availability_change_request 
WHERE id_teacher = 'D0000000-0000-0000-0000-000000000003' 
AND id_term = 'D0000000-0000-0000-0000-000000000002';

-- Remove test teacher availability
DELETE FROM teacher_availability 
WHERE id_teacher = 'D0000000-0000-0000-0000-000000000003' 
AND id_term = 'D0000000-0000-0000-0000-000000000002';

-- Remove test user roles
DELETE FROM user_roles 
WHERE id_user IN ('D0000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000004');

-- Remove test users (optional - they might be needed for other tests)
-- DELETE FROM users WHERE id IN ('D0000000-0000-0000-0000-000000000003', 'D0000000-0000-0000-0000-000000000004');

-- Remove test academic term (optional - it might be needed for other tests)
-- DELETE FROM academic_terms WHERE id = 'D0000000-0000-0000-0000-000000000002';

-- Remove test sessions (optional - they might be needed for other tests)
-- DELETE FROM sessions WHERE id IN ('D0000000-0000-0000-0000-000000000010', 'D0000000-0000-0000-0000-000000000011', 'D0000000-0000-0000-0000-000000000012', 'D0000000-0000-0000-0000-000000000013');