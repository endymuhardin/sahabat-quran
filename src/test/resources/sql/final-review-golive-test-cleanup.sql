-- =====================================================
-- FINAL REVIEW GO-LIVE TEST CLEANUP
-- Cleanup test data for Phase 6: Final Schedule Review & System Go-Live
-- =====================================================

-- Delete test class preparation checklist
DELETE FROM class_preparation_checklist WHERE checklist_item LIKE 'FRGOL_TEST%';

-- Delete test class sessions
DELETE FROM class_sessions WHERE id IN (
    SELECT cs.id FROM class_sessions cs
    JOIN class_groups cg ON cs.id_class = cg.id
    WHERE cg.class_name LIKE 'FRGOL_TEST_%'
);

-- Delete test class groups
DELETE FROM class_groups WHERE class_name LIKE 'FRGOL_TEST_%';

-- Note: We don't delete levels or academic terms as they might be used by other tests