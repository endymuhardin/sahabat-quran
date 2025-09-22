package com.sahabatquran.webapp.functional.validation.operation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.InstructorSessionPage;
import com.sahabatquran.webapp.functional.page.LoginPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Instructor Validation Tests for Alternate Path scenarios.
 * Tests error handling, late check-ins, and edge cases.
 * 
 * User Role: INSTRUCTOR/TEACHER
 * Focus: Validation and error scenarios during session management.
 */
@Slf4j
@DisplayName("AKH-AP: Instructor Validation Alternate Path Scenarios")
class InstructorValidationTest extends BasePlaywrightTest {

    @BeforeEach
    void resetBrowserState() {
        // Clear any browser state that might interfere between tests
        page.evaluate("() => {" +
            "localStorage.clear();" +
            "sessionStorage.clear();" +
        "}");

        // Navigate to a clean page to reset any JavaScript state
        page.navigate(getBaseUrl() + "/logout");
        page.waitForTimeout(500); // Brief pause for logout to process
    }

    @Test
    @DisplayName("AKH-AP-001: Instructor - Late Check-in Handling")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleLateCheckinValidation() {
        log.info("🚀 Starting AKH-AP-001: Instructor Late Check-in Handling...");
        
        // Test data sesuai dokumentasi
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        final String SESSION_TIME = "08:00-10:00";
        final String LATE_CHECK_IN_TIME = "08:45"; // 45 minutes late
        final int LATE_THRESHOLD_MINUTES = 15;
        
        LoginPage loginPage = new LoginPage(page);
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        // Bagian 1: Attempt Late Check-in
        log.info("📝 Bagian 1: Attempt Late Check-in");
        
        loginAsInstructor();
        
        // Navigate to My Classes
        sessionPage.navigateToMyClasses();
        
        // Verify late session indicators
        assertTrue(sessionPage.isLateSessionWarningVisible(), "Late session warning should be visible");
        assertTrue(sessionPage.isLateBadgeVisible(), "LATE badge should be visible on session card");
        assertTrue(sessionPage.isOverdueColorCoding(), "Red/orange color coding should indicate overdue session");
        
        // Attempt check-in despite being late
        sessionPage.clickCheckIn();
        assertTrue(sessionPage.isLateCheckinModalVisible(), "Late check-in modal should appear");
        assertTrue(sessionPage.isLateWarningMessageVisible(), "Late warning message should be displayed");
        assertTrue(sessionPage.isReasonRequiredForLateCheckin(), "Reason field should be required for late check-in");
        
        // Bagian 2: Handle Late Check-in Validation
        log.info("📝 Bagian 2: Handle Late Check-in Validation");
        
        // Try to proceed without providing reason
        sessionPage.attemptCheckInWithoutReason();
        assertTrue(sessionPage.isValidationErrorVisible(), "Validation error should be shown for missing reason");
        assertTrue(sessionPage.isReasonFieldHighlighted(), "Reason field should be highlighted as required");
        
        // Provide valid reason and location
        String lateReason = "Terjebak macet di jalan karena hujan deras";
        sessionPage.enterLateCheckinReason(lateReason);
        sessionPage.enterCheckInLocation("Ruang Kelas A1");
        sessionPage.confirmLateCheckIn();

        // Check for server-side exceptions after check-in operation
        assertNoServerErrors();
        
        // Verify late check-in success with warnings
        assertTrue(sessionPage.isLateCheckinSuccessVisible(), "Late check-in success message should be visible");
        assertTrue(sessionPage.isLateSessionStatusVisible(), "Session should show LATE status");
        assertTrue(sessionPage.isRemainingTimeWarningVisible(), "Warning about reduced session time should be visible");
        assertTrue(sessionPage.isAdministrativeNoteRecorded(), "Administrative note about lateness should be recorded");
        
        // Bagian 3: Verify Impact on Session
        log.info("📝 Bagian 3: Verify Impact on Session");
        
        // Check session duration adjustment
        assertTrue(sessionPage.isSessionDurationAdjusted(), "Session duration should be adjusted for late start");
        assertTrue(sessionPage.isAutoNotificationSent(), "Auto-notification should be sent to admin about late check-in");
        assertTrue(sessionPage.isStudentWaitingTimeRecorded(), "Student waiting time should be recorded");

        // Final server error check
        assertNoServerErrors();
        
        log.info("✅ AKH-AP-001: Late Check-in Handling completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-AP-003: Instructor - Session Equipment Failure")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleSessionEquipmentFailure() {
        log.info("🚀 Starting AKH-AP-003: Session Equipment Failure...");
        
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        loginAsInstructor();
        sessionPage.navigateToMyClasses();
        sessionPage.clickCheckIn();
        sessionPage.confirmCheckIn();
        sessionPage.clickStartSession();
        
        // Bagian 1: Report Equipment Issue
        log.info("📝 Bagian 1: Report Equipment Issue");
        
        // Access emergency options
        assertTrue(sessionPage.isEmergencyOptionsMenuVisible(), "Emergency options menu should be available");
        sessionPage.clickEmergencyOptions();
        assertTrue(sessionPage.isEquipmentIssueOptionVisible(), "Equipment issue option should be visible");
        
        // Report equipment failure
        sessionPage.selectEquipmentIssue();
        assertTrue(sessionPage.isEquipmentIssueFormVisible(), "Equipment issue reporting form should be visible");
        
        String issueDescription = "Proyektor tidak menyala, sudah dicoba restart beberapa kali";
        sessionPage.enterEquipmentIssueDescription(issueDescription);
        sessionPage.selectEquipmentType("PROJECTOR");
        sessionPage.markAsUrgent(true);
        sessionPage.submitEquipmentIssue();
        
        // Verify issue reporting
        assertTrue(sessionPage.isEquipmentIssueReported(), "Equipment issue should be successfully reported");
        assertTrue(sessionPage.isMaintenanceNotificationSent(), "Maintenance team should be notified");
        assertTrue(sessionPage.isIssueTrackingNumberGenerated(), "Issue tracking number should be generated");
        
        // Bagian 2: Handle Session Continuation Options
        log.info("📝 Bagian 2: Handle Session Continuation Options");
        
        // Check available options
        assertTrue(sessionPage.isContinueWithoutEquipmentOptionVisible(), "Continue without equipment option should be available");
        assertTrue(sessionPage.isRescheduleSessionOptionVisible(), "Reschedule session option should be available");
        assertTrue(sessionPage.isRequestAlternativeRoomOptionVisible(), "Request alternative room option should be available");
        
        // Choose to continue without equipment
        sessionPage.selectContinueWithoutEquipment();
        assertTrue(sessionPage.isAlternativeMethodsGuideVisible(), "Alternative teaching methods guide should be shown");
        assertTrue(sessionPage.isSessionNotesUpdatedWithIssue(), "Session notes should reflect equipment issue");

        // Check for server-side exceptions after equipment issue handling
        assertNoServerErrors();
        
        log.info("✅ AKH-AP-003: Equipment Failure Handling completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-AP-004: Instructor - Student Attendance Discrepancy")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleStudentAttendanceDiscrepancy() {
        log.info("🚀 Starting AKH-AP-004: Student Attendance Discrepancy...");
        
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        loginAsInstructor();
        sessionPage.navigateToMyClasses();
        sessionPage.clickCheckIn();
        sessionPage.confirmCheckIn();
        sessionPage.clickStartSession();
        
        // Bagian 1: Detect Attendance Discrepancy
        log.info("📝 Bagian 1: Detect Attendance Discrepancy");
        
        // More students present than registered
        int registeredStudents = 8;
        int actualStudents = 10; // 2 extra students
        
        sessionPage.markStudentAttendance(actualStudents, 0); // All present, but extra students
        
        // System should detect discrepancy
        assertTrue(sessionPage.isAttendanceDiscrepancyWarningVisible(), "Attendance discrepancy warning should be visible");
        assertTrue(sessionPage.isExtraStudentsDetected(), "Extra students should be detected");
        assertTrue(sessionPage.isAttendanceValidationTriggered(), "Attendance validation should be triggered");
        
        // Bagian 2: Handle Extra Students
        log.info("📝 Bagian 2: Handle Extra Students");
        
        // Options for handling extra students
        assertTrue(sessionPage.isAddGuestStudentOptionVisible(), "Add guest student option should be available");
        assertTrue(sessionPage.isContactAdminOptionVisible(), "Contact admin option should be available");
        assertTrue(sessionPage.isRejectExtraStudentsOptionVisible(), "Reject extra students option should be available");
        
        // Add guest students
        sessionPage.selectAddGuestStudents();
        
        String guestStudent1 = "Ahmad Faruq (Tamu)";
        String guestStudent2 = "Siti Aisyah (Tamu)";
        
        sessionPage.addGuestStudent(guestStudent1, "Trial class - calon siswa baru");
        sessionPage.addGuestStudent(guestStudent2, "Make-up class dari kelas lain");
        
        assertTrue(sessionPage.isGuestStudentsRecorded(), "Guest students should be properly recorded");
        assertTrue(sessionPage.isAdminNotifiedOfGuestStudents(), "Admin should be notified of guest students");

        // Check for server-side exceptions after attendance handling
        assertNoServerErrors();
        
        log.info("✅ AKH-AP-004: Attendance Discrepancy Handling completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-AP-005: Instructor - Emergency Session Termination")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleEmergencySessionTermination() {
        log.info("🚀 Starting AKH-AP-005: Emergency Session Termination...");
        
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        loginAsInstructor();
        sessionPage.navigateToMyClasses();
        sessionPage.clickCheckIn();
        sessionPage.confirmCheckIn();
        sessionPage.clickStartSession();
        
        // Bagian 1: Initiate Emergency Termination
        log.info("📝 Bagian 1: Initiate Emergency Termination");
        
        // Access emergency termination
        sessionPage.clickEmergencyOptions();
        assertTrue(sessionPage.isEmergencyTerminationOptionVisible(), "Emergency termination option should be visible");
        
        sessionPage.selectEmergencyTermination();
        assertTrue(sessionPage.isEmergencyTerminationConfirmationVisible(), "Emergency termination confirmation should be shown");
        assertTrue(sessionPage.isEmergencyReasonRequired(), "Emergency reason should be required");
        
        // Provide emergency reason
        String emergencyReason = "Kebakaran di gedung - evakuasi darurat diperlukan";
        sessionPage.enterEmergencyReason(emergencyReason);
        sessionPage.selectEmergencyType("FIRE_EVACUATION");
        sessionPage.confirmEmergencyTermination();
        
        // Bagian 2: Verify Emergency Procedures
        log.info("📝 Bagian 2: Verify Emergency Procedures");
        
        // Check emergency notifications
        assertTrue(sessionPage.isEmergencyNotificationSent(), "Emergency notification should be sent to all stakeholders");
        assertTrue(sessionPage.isSessionTerminatedImmediately(), "Session should be terminated immediately");
        assertTrue(sessionPage.isEmergencyLogCreated(), "Emergency log should be created");
        assertTrue(sessionPage.isParentNotificationTriggered(), "Parent notification should be triggered");
        
        // Verify session data preservation
        assertTrue(sessionPage.isSessionDataPreserved(), "Session data should be preserved despite emergency termination");
        assertTrue(sessionPage.isAttendanceDataSaved(), "Attendance data should be saved");
        assertTrue(sessionPage.isEmergencyReportGenerated(), "Emergency report should be generated");

        // Check for server-side exceptions after emergency termination
        assertNoServerErrors();
        
        log.info("✅ AKH-AP-005: Emergency Session Termination completed successfully!");
    }
}
