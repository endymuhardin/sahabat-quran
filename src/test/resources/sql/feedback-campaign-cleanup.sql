-- Feedback Campaign Test Data Cleanup
-- Removes all test data created for feedback testing

-- Delete test feedback data in correct order to respect foreign key constraints
DELETE FROM feedback_answers 
WHERE id_response IN (
    SELECT id FROM feedback_responses 
    WHERE id_campaign IN (
        SELECT id FROM feedback_campaigns 
        WHERE campaign_name LIKE 'TEST_%'
    )
);

DELETE FROM feedback_responses 
WHERE id_campaign IN (
    SELECT id FROM feedback_campaigns 
    WHERE campaign_name LIKE 'TEST_%'
);

DELETE FROM feedback_questions 
WHERE id_campaign IN (
    SELECT id FROM feedback_campaigns 
    WHERE campaign_name LIKE 'TEST_%'
);

DELETE FROM feedback_campaigns 
WHERE campaign_name LIKE 'TEST_%';

-- Delete test enrollments
DELETE FROM enrollments 
WHERE id = 'i9999999-9999-9999-9999-999999999999'::uuid;

-- Delete test class groups
DELETE FROM class_groups 
WHERE id = 'h8888888-8888-8888-8888-888888888888'::uuid
   OR group_name LIKE 'TEST_%';

-- Delete test user permissions (but keep the users as they might be used elsewhere)
DELETE FROM user_permissions 
WHERE user_id IN (
    SELECT id FROM users 
    WHERE username IN ('siswa.ali', 'ustadz.ahmad')
    AND username LIKE 'TEST_%'
);

-- Delete test users only if they were created specifically for testing
DELETE FROM users 
WHERE username LIKE 'TEST_%';

-- Note: We don't delete siswa.ali or ustadz.ahmad as they might be used in other tests
-- Also keeping the test academic term as it might be referenced elsewhere

-- Clean up any orphaned anonymous tokens
DELETE FROM feedback_responses 
WHERE anonymous_token LIKE 'TEST_%';

-- Reset any counters or sequences if needed
-- This is database-specific and might not be necessary

-- Vacuum to reclaim space (PostgreSQL specific, optional)
-- VACUUM ANALYZE feedback_campaigns;
-- VACUUM ANALYZE feedback_questions;
-- VACUUM ANALYZE feedback_responses;
-- VACUUM ANALYZE feedback_answers;