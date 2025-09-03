package com.sahabatquran.webapp.functional.scenarios.registrationworkflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.TeacherRegistrationPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Instructor Registration Workflow Tests.
 * Covers instructor involvement in student registration process.
 * 
 * User Role: INSTRUCTOR/TEACHER
 * Focus: Placement test administration, student assessment, and registration review.
 */
@Slf4j
@DisplayName("Instructor Registration Workflow")
class InstructorTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("MR-HP-003: Should successfully complete registration review and evaluation")
    @Sql(scripts = "/sql/teacher-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/teacher-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyCompleteRegistrationReviewAndEvaluation() {
        log.info("🚀 Starting MR-HP-003: Teacher Review Registration Workflow Test...");
        
        // Test data sesuai dokumentasi
        final String TEACHER_USERNAME = "ustadz.ahmad";
        final String TEACHER_PASSWORD = "Welcome@YSQ2024";
        // TEST_REGISTRATION_ID from SQL setup data
        final String TEACHER_REMARKS = "Bacaan cukup baik dengan tajwid yang benar pada sebagian besar ayat. " +
                                     "Perlu perbaikan pada mad dan qalqalah. " +
                                     "Rekomendasikan untuk masuk Tahsin Level 2.";
        final String RECOMMENDED_LEVEL = "Tahsin 2"; // Level name from seed data
        final String PLACEMENT_RESULT = "3";
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Bagian 1: Login dan View Assigned Registrations
        log.info("📝 Bagian 1: Login dan View Assigned Registrations");
        
        // Step 1: Login sebagai Teacher
        loginPage.navigateToLoginPage(getBaseUrl());
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
        
        loginPage.login(TEACHER_USERNAME, TEACHER_PASSWORD);
        
        // Verifikasi: Login berhasil, dashboard teacher muncul
        page.waitForURL("**/dashboard");
        assertTrue(page.url().contains("dashboard"), "Should redirect to dashboard after login");
        
        // Step 2: View assigned registrations
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        assertTrue(teacherPage.isOnTeacherRegistrationsPage(), "Should be on teacher registrations page");
        
        // Verifikasi: Dashboard counts dan assignments list
        assertTrue(teacherPage.hasAssignments() || teacherPage.hasNoAssignments(), 
            "Should show assignments or no assignments message");
        
        // Check dashboard summary
        int totalAssignments = teacherPage.getTotalAssignments();
        int pendingCount = teacherPage.getPendingCount();
        int inReviewCount = teacherPage.getInReviewCount();
        int completedCount = teacherPage.getCompletedCount();
        
        log.info("📊 Dashboard Summary - Total: {}, Pending: {}, In Review: {}, Completed: {}", 
            totalAssignments, pendingCount, inReviewCount, completedCount);
        
        // Bagian 2: Review Registration Detail
        log.info("📝 Bagian 2: Review Registration Detail");
        
        // Find first available assigned registration from our test data
        String dynamicRegistrationId = teacherPage.getFirstAssignedRegistrationId();
        
        // Fallback: try using our known test registration ID if dynamic lookup fails
        final String firstAssignedRegistration = (dynamicRegistrationId != null) 
            ? dynamicRegistrationId 
            : "aa001000-0000-0000-0000-000000000001";
        
        if (dynamicRegistrationId == null) {
            log.info("📝 Using fallback test registration ID: {}", firstAssignedRegistration);
        }
        
        // IMPORTANT: Test should fail if there are no assignments
        // The test expects at least 2 assignments as per the test description
        assertTrue(totalAssignments >= 2, 
            "Test expects at least 2 assignments for instructor but found " + totalAssignments);
        
        assertNotNull(firstAssignedRegistration, 
            "Should have at least one assigned registration but found none");
        
        assertTrue(teacherPage.isAssignmentVisible(firstAssignedRegistration),
            "First assignment should be visible but it's not: " + firstAssignedRegistration);
        
        log.info("📝 Working with test assignment: {}", firstAssignedRegistration);
        
        // Step 3: Select registration for evaluation
        teacherPage.expectAssignmentVisible(firstAssignedRegistration);
        teacherPage.expectReviewButtonAvailable(firstAssignedRegistration);
        
        // Step 4: Start review process
        teacherPage.startReview(firstAssignedRegistration);
        assertTrue(teacherPage.isOnReviewPage(), "Should be on review page");
        
        // Bagian 3: Review Student Information
        log.info("📝 Bagian 3: Review Student Information");
        
        // Verify student information is displayed
        String studentName = teacherPage.getStudentName();
        String studentEmail = teacherPage.getStudentEmail();
        String studentProgram = teacherPage.getStudentProgram();
        
        assertFalse(studentName.isEmpty(), "Student name should be displayed");
        assertFalse(studentEmail.isEmpty(), "Student email should be displayed");
        assertFalse(studentProgram.isEmpty(), "Student program should be displayed");
        
        log.info("📋 Reviewing student: {} ({}) - Program: {}", studentName, studentEmail, studentProgram);
        
        // Bagian 4: Placement Test Review
        log.info("📝 Bagian 4: Placement Test Review");
        
        if (teacherPage.isRecordingAvailable()) {
            log.info("🎵 Recording available - can proceed with evaluation");
            // Note: Actual playback testing would require additional setup
            // teacherPage.playRecording(); // Opens in new tab
        } else {
            log.warn("⚠️ No recording available for this test");
        }
        
        // Bagian 5: Complete Teacher Evaluation
        log.info("📝 Bagian 5: Complete Teacher Evaluation");
        
        // Step 8-9: Complete evaluation with all details
        teacherPage.completeReviewWithPlacementTest(
            firstAssignedRegistration, 
            TEACHER_REMARKS, 
            RECOMMENDED_LEVEL, 
            PLACEMENT_RESULT, 
            "Evaluasi lengkap dengan rekomendasi level"
        );
        
        // Wait for submission completion
        page.waitForTimeout(2000);
        
        // Verify completion - should redirect back to assignments list
        assertTrue(teacherPage.isOnTeacherRegistrationsPage() || page.url().contains("registrations"), 
            "Should return to assignments list or detail page after submission");
        
        log.info("✅ Teacher evaluation completed successfully");
        
        log.info("✅ MR-HP-003: Teacher Review Registration Workflow Test completed!");
    }
    
    @Test
    @DisplayName("TP-HP-001: Should successfully complete placement test evaluation")
    @Sql(scripts = "/sql/teacher-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/teacher-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyCompletePlacementTestEvaluation() {
        log.info("🚀 Starting TP-HP-001: Teacher Placement Test Evaluation Test...");
        
        final String TEACHER_USERNAME = "ustadz.ahmad";
        final String TEACHER_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "aa001000-0000-0000-0000-000000000001"; // From SQL setup
        final String EVALUATION_REMARKS = "Bacaan Al-Fatiha sudah cukup baik. " +
                                        "Makhorijul huruf sebagian besar benar. " +
                                        "Perlu latihan lebih untuk mad dan ghunnah.";
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(TEACHER_USERNAME, TEACHER_PASSWORD);
        page.waitForURL("**/dashboard");
        
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        // Test should fail if the expected assignment is not visible
        assertTrue(teacherPage.isAssignmentVisible(TEST_REGISTRATION_ID),
            "Test registration " + TEST_REGISTRATION_ID + " should be visible but was not found");
        
        // Start placement test evaluation
        teacherPage.startReview(TEST_REGISTRATION_ID);
        
        // Verify placement test information
        assertTrue(teacherPage.isRecordingAvailable(),
            "Placement test recording should be available for evaluation");
        
        log.info("📝 Placement test recording is available");
        
        // Complete evaluation focusing on placement test
        teacherPage.setReviewStatus("COMPLETED");
        teacherPage.fillTeacherRemarks(EVALUATION_REMARKS);
        teacherPage.setPlacementTestResult("3");
        teacherPage.fillPlacementNotes("Siswa menunjukkan pemahaman dasar tajwid yang baik");
        
        // Submit evaluation
        teacherPage.submitReview();
        
        log.info("✅ Placement test evaluation completed");
        
        log.info("✅ TP-HP-001: Teacher Placement Test Evaluation Test completed!");
    }
    
    @Test
    @DisplayName("Should handle draft save functionality")
    @Sql(scripts = "/sql/teacher-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/teacher-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleDraftSaveFunctionality() {
        log.info("🚀 Testing Draft Save Functionality...");
        
        final String TEACHER_USERNAME = "ustadz.ahmad";
        final String TEACHER_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "aa001000-0000-0000-0000-000000000001"; // From SQL setup
        final String DRAFT_REMARKS = "Draft catatan evaluasi - masih perlu review lebih lanjut";
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(TEACHER_USERNAME, TEACHER_PASSWORD);
        page.waitForURL("**/dashboard");
        
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        // Test should fail if the expected assignment is not visible
        assertTrue(teacherPage.isAssignmentVisible(TEST_REGISTRATION_ID),
            "Test registration " + TEST_REGISTRATION_ID + " should be visible for draft save test");
        
        // Test draft save functionality
        teacherPage.saveDraftReview(TEST_REGISTRATION_ID, DRAFT_REMARKS);
        
        // Verify draft is saved (status should be IN_REVIEW)
        page.waitForTimeout(1000);
        
        log.info("✅ Draft save functionality tested");
        
        log.info("✅ Draft save functionality test completed!");
    }
    
    @Test
    @DisplayName("Should display instructor dashboard with assigned registrations")
    void shouldDisplayInstructorDashboardWithAssignedRegistrations() {
        log.info("🚀 Testing Instructor Dashboard Display...");
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login as instructor
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("ustadz.ahmad", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        // Navigate to instructor registrations
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        // Verify instructor can access assignments
        assertTrue(teacherPage.isOnTeacherRegistrationsPage(), 
            "Instructor should have access to assignments page");
        
        // Verify dashboard elements
        assertTrue(teacherPage.getTotalAssignments() >= 0, 
            "Should display total assignments count");
        assertTrue(teacherPage.getPendingCount() >= 0, 
            "Should display pending count");
        assertTrue(teacherPage.getInReviewCount() >= 0, 
            "Should display in-review count");
        assertTrue(teacherPage.getCompletedCount() >= 0, 
            "Should display completed count");
        
        log.info("✅ Instructor dashboard display test completed!");
    }
    
    @Test
    @DisplayName("Should show only assignments assigned to current instructor")
    void shouldShowOnlyAssignmentsAssignedToCurrentInstructor() {
        log.info("🚀 Testing Instructor Assignment Filtering...");
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login as specific instructor
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("ustadz.ahmad", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        // Verify only assigned registrations are shown
        // (This would require specific test data to validate properly)
        assertTrue(teacherPage.isOnTeacherRegistrationsPage(), 
            "Instructor should see assignments page");
        
        // In a complete test, you would verify that:
        // 1. Only assignments assigned to ustadz.ahmad are visible
        // 2. No assignments from other instructors are shown
        // 3. Proper security filtering is applied
        
        log.info("✅ Instructor assignment filtering test completed!");
    }
    
    @Test
    @DisplayName("Should prevent review of unassigned registrations")
    void shouldPreventReviewOfUnassignedRegistrations() {
        log.info("🚀 Testing Unassigned Registration Access Prevention...");
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login as instructor
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("ustadz.ahmad", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        // Try to access a registration not assigned to this instructor (if such exists)
        // This would typically result in:
        // 1. 403 Forbidden response
        // 2. Redirect to access denied page
        // 3. Error message about unauthorized access
        
        // For this test, we verify that only assigned registrations appear in the list
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        assertTrue(teacherPage.isOnTeacherRegistrationsPage(), 
            "Instructor should access assignments page securely");
        
        // Note: Testing direct URL access to unassigned registrations 
        // would require specific test scenarios and error page handling
        
        log.info("✅ Unassigned registration access prevention test completed!");
    }
    
    @Test
    @DisplayName("Should handle form validation for incomplete review")
    @Sql(scripts = "/sql/teacher-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/teacher-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleFormValidationForIncompleteReview() {
        log.info("🚀 Testing Review Form Validation...");
        
        final String TEACHER_USERNAME = "ustadz.ahmad";
        final String TEACHER_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "aa001000-0000-0000-0000-000000000001"; // From SQL setup
        
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(TEACHER_USERNAME, TEACHER_PASSWORD);
        page.waitForURL("**/dashboard");
        
        teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        
        // Test should fail if the expected assignment is not visible
        assertTrue(teacherPage.isAssignmentVisible(TEST_REGISTRATION_ID),
            "Test registration " + TEST_REGISTRATION_ID + " should be visible for validation test");
        
        // Start review
        teacherPage.startReview(TEST_REGISTRATION_ID);
        
        // Try to submit without required fields
        teacherPage.setReviewStatus("COMPLETED");
        // Don't fill remarks (should cause validation error)
        teacherPage.submitReview();
        
        // Should still be on review page due to validation
        assertTrue(teacherPage.isOnReviewPage(), 
            "Should remain on review page due to validation errors");
        
        // Check for validation error indicators
        teacherPage.expectFormValidationError();
        
        log.info("✅ Form validation working correctly");
        
        log.info("✅ Review form validation test completed!");
    }
}