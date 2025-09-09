package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.InstructorSessionPage;
import com.sahabatquran.webapp.functional.page.WeeklyProgressPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Instructor Daily Operations Workflow Tests.
 * Covers instructor daily activities during semester operations.
 * 
 * User Role: INSTRUCTOR/TEACHER
 * Focus: Session check-in/out, attendance marking, progress recording, reschedule requests.
 */
@Slf4j
@DisplayName("AKH-HP: Instructor Daily Operations Happy Path Scenarios")
class InstructorTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("AKH-HP-001: Instructor - Session Check-in dan Pelaksanaan Kelas")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyCompleteSessionExecutionWorkflow() {
        log.info("🚀 Starting AKH-HP-001: Instructor Session Check-in dan Pelaksanaan Kelas...");
        
        // Test data sesuai dokumentasi
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        final String CHECK_IN_LOCATION = "Ruang Kelas A1";
        final String SESSION_NOTES = "Materi Makharijul Huruf - siswa menunjukkan progress baik";
        final int EXPECTED_STUDENTS = 8;
        final int PRESENT_STUDENTS = 6;
        
        LoginPage loginPage = new LoginPage(page);
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        // Bagian 1: Teacher Check-in
        log.info("📝 Bagian 1: Teacher Check-in");
        
        loginAsInstructor();
        
        // Verify instructor dashboard loads
        page.waitForURL("**/dashboard**");
        assertTrue(page.locator("text=My Classes").isVisible(), "My Classes menu should be visible");
        assertTrue(page.locator("text=Ustadz Ahmad").isVisible(), "Welcome message should show instructor name");
        
        // Navigate to My Classes
        sessionPage.navigateToMyClasses();
        assertTrue(sessionPage.isTodaysSessionVisible(), "Today's session should be visible");
        assertTrue(sessionPage.isCheckInButtonEnabled(), "Check-in button should be enabled");
        
        // Teacher Self Check-in
        sessionPage.clickCheckIn();
        assertTrue(sessionPage.isCheckInModalVisible(), "Check-in modal should be visible");
        assertNotNull(sessionPage.getArrivalTime(), "Arrival time should be auto-filled");
        
        sessionPage.enterCheckInLocation(CHECK_IN_LOCATION);
        sessionPage.confirmCheckIn();
        
        // Verify check-in success
        assertTrue(sessionPage.isCheckInSuccessVisible(), "Check-in success message should be visible");
        assertTrue(sessionPage.isSessionStatusInProgress(), "Session status should be IN_PROGRESS");
        assertTrue(sessionPage.isSessionTimerVisible(), "Session timer should be running");
        assertTrue(sessionPage.isStartSessionButtonVisible(), "Start Session button should be visible");
        
        // Bagian 2: Session Execution
        log.info("📝 Bagian 2: Session Execution");
        
        // Start Class Session
        sessionPage.clickStartSession();
        assertTrue(sessionPage.isSessionDashboardVisible(), "Session dashboard should be open");
        assertTrue(sessionPage.isStudentAttendanceInterfaceVisible(), "Student attendance interface should be visible");
        
        // Verify session objectives and materials
        assertTrue(sessionPage.areSessionObjectivesVisible(), "Session objectives should be visible");
        assertTrue(sessionPage.areSessionMaterialsVisible(), "Session materials should be visible");
        
        // Mark Student Attendance (6 present, 2 absent)
        sessionPage.markStudentAttendance(PRESENT_STUDENTS, EXPECTED_STUDENTS - PRESENT_STUDENTS);
        assertTrue(sessionPage.isAttendanceCounterUpdated(PRESENT_STUDENTS, EXPECTED_STUDENTS), 
            "Attendance counter should show correct numbers");
        assertTrue(sessionPage.isAutoSaveActive(), "Auto-save should be active");
        
        // Add Session Notes
        sessionPage.addSessionNotes(SESSION_NOTES);
        assertTrue(sessionPage.isSessionNotesAutoSaved(), "Session notes should be auto-saved");
        assertTrue(sessionPage.isCharacterCounterVisible(), "Character counter should be visible");
        
        // Record Learning Objectives Achievement
        sessionPage.markLearningObjectivesAchieved();
        assertTrue(sessionPage.isProgressIndicatorVisible(), "Progress indicator should be visible");
        assertTrue(sessionPage.areAdditionalNotesFieldVisible(), "Additional notes field should be available");
        
        // Bagian 3: Session Completion
        log.info("📝 Bagian 3: Session Completion");
        
        // End Session dan Check-out
        sessionPage.clickEndSession();
        assertTrue(sessionPage.isEndSessionModalVisible(), "End session modal should be visible");
        assertTrue(sessionPage.isSessionSummaryVisible(), "Session summary should be visible");
        assertTrue(sessionPage.isAttendanceSummaryVisible(PRESENT_STUDENTS, EXPECTED_STUDENTS), 
            "Attendance summary should show correct numbers");
        assertNotNull(sessionPage.getDepartureTime(), "Departure time should be auto-filled");
        
        // Final Session Submission
        sessionPage.submitSession();
        assertTrue(sessionPage.isSessionCompletedStatusVisible(), "Session status should be COMPLETED");
        assertTrue(sessionPage.isSubmissionSuccessMessageVisible(), "Success message should be displayed");
        
        // Verify redirect to My Classes dashboard
        page.waitForURL("**/instructor/my-classes**");
        assertTrue(sessionPage.isSessionCompletedInList(), "Session should show as completed in list");
        
        log.info("✅ AKH-HP-001: Session execution workflow completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-HP-004: Instructor - Handle Session Reschedule Request")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyHandleSessionRescheduleRequest() {
        log.info("🚀 Starting AKH-HP-004: Instructor Session Reschedule Request...");
        
        // Test data sesuai dokumentasi
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        final String RESCHEDULE_REASON = "Teacher Illness";
        final String RESCHEDULE_NOTES = "Sakit demam, tidak bisa mengajar besok";
        final String NEW_DATE = "2024-12-25"; // Day after tomorrow
        final String NEW_TIME = "08:00";
        
        LoginPage loginPage = new LoginPage(page);
        InstructorSessionPage sessionPage = new InstructorSessionPage(page);
        
        // Bagian 1: Request Session Reschedule
        log.info("📝 Bagian 1: Request Session Reschedule");
        
        loginAsInstructor();
        
        // Access My Classes
        sessionPage.navigateToMyClasses();
        assertTrue(sessionPage.isTomorrowSessionVisible(), "Tomorrow's session should be visible");
        assertTrue(sessionPage.isRescheduleOptionAvailable(), "Reschedule option should be available");
        
        // Initiate Reschedule Request
        sessionPage.clickReschedule();
        assertTrue(sessionPage.isRescheduleModalVisible(), "Reschedule modal should be open");
        assertTrue(sessionPage.areSessionDetailsCorrect(), "Session details should be correct");
        assertTrue(sessionPage.isReasonDropdownAvailable(), "Reason dropdown should be available");
        assertTrue(sessionPage.isDateTimePickerAvailable(), "Date/time picker should be available");
        
        // Select Reschedule Reason
        sessionPage.selectRescheduleReason(RESCHEDULE_REASON);
        assertTrue(sessionPage.isAutoApprovalIndicatorVisible(), "Auto-approval indicator should be visible");
        assertTrue(sessionPage.isAdditionalNotesFieldAvailable(), "Additional notes field should be available");
        
        // Choose New Date and Time
        sessionPage.setNewDateTime(NEW_DATE, NEW_TIME);
        assertTrue(sessionPage.isAvailabilityChecked(), "Availability should be checked");
        assertTrue(sessionPage.isStudentImpactAssessmentVisible(), "Student impact assessment should be visible");
        
        // Bagian 2: Submit Reschedule Request
        log.info("📝 Bagian 2: Submit Reschedule Request");
        
        // Add Detailed Notes
        sessionPage.addRescheduleNotes(RESCHEDULE_NOTES);
        assertTrue(sessionPage.isCharacterLimitIndicatorVisible(), "Character limit indicator should be visible");
        
        // Review Impact Assessment
        assertTrue(sessionPage.isAffectedStudentsCountVisible(8), "Affected students count should be shown");
        assertTrue(sessionPage.isParentNotificationRequirementVisible(), "Parent notification requirement should be shown");
        assertTrue(sessionPage.isAutoApprovalStatusVisible(), "Auto-approval status should be visible");
        
        // Submit Reschedule Request
        sessionPage.submitRescheduleRequest();
        assertTrue(sessionPage.isRescheduleRequestSubmittedSuccessfully(), "Request should be submitted successfully");
        assertNotNull(sessionPage.getRequestId(), "Request ID should be generated");
        assertTrue(sessionPage.isStatusApproved(), "Status should be APPROVED for illness");
        
        // Bagian 3: Confirm Notifications Sent
        log.info("📝 Bagian 3: Confirm Notifications Sent");
        
        // Check Request Status
        sessionPage.refreshMyClasses();
        assertTrue(sessionPage.isOriginalSessionRescheduled(), "Original session should show as RESCHEDULED");
        assertTrue(sessionPage.isNewSessionCreated(), "New session should be created");
        assertTrue(sessionPage.isRescheduleLogVisible(), "Reschedule log should be visible");
        
        // Verify Student Notifications
        assertTrue(sessionPage.isStudentsNotifiedStatusTrue(), "Students notified status should be true");
        assertNotNull(sessionPage.getNotificationTimestamp(), "Notification timestamp should be recorded");
        assertTrue(sessionPage.isParentNotificationStatusAvailable(), "Parent notification status should be available");
        
        log.info("✅ AKH-HP-004: Session reschedule request completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-HP-006: Teacher - Weekly Progress Recording")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyRecordWeeklyProgress() {
        log.info("🚀 Starting AKH-HP-006: Teacher Weekly Progress Recording...");
        
        // Test data sesuai dokumentasi
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        final int WEEK_NUMBER = 5;
        final String CLASS_SUMMARY = "Class menunjukkan progress baik dalam tajweed. Perlu lebih banyak latihan praktik untuk beberapa siswa.";
        final String PARENT_COMMUNICATION = "Will contact Omar's parents untuk discuss home practice plan";
        
        LoginPage loginPage = new LoginPage(page);
        WeeklyProgressPage progressPage = new WeeklyProgressPage(page);
        
        // Bagian 1: Access Weekly Progress Interface
        log.info("📝 Bagian 1: Access Weekly Progress Interface");
        
        loginAsInstructor();
        
        progressPage.navigateToWeeklyProgress();
        assertTrue(progressPage.isProgressRecordingInterfaceVisible(), "Progress recording interface should open");
        assertTrue(progressPage.isWeekNumberSelected(WEEK_NUMBER), "Week 5 should be selected by default");
        assertTrue(progressPage.isStudentListVisible(8), "Student list should show 8 students");
        assertTrue(progressPage.areProgressCategoriesVisible(), "Progress categories should be visible");
        
        // Review Session Summary
        assertTrue(progressPage.areCompletedSessionsVisible(2), "2 completed sessions should be visible");
        assertTrue(progressPage.areSessionDatesVisible(), "Session dates should be visible");
        assertTrue(progressPage.isLearningObjectivesSummaryVisible(), "Learning objectives summary should be visible");
        
        // Bagian 2: Record Individual Student Progress
        log.info("📝 Bagian 2: Record Individual Student Progress");
        
        // Student 1 - Ali (High Performer)
        progressPage.recordStudentProgress("Ali", 85, "B+", "Completed Surah Al-Fatiha perfectly", 90, "A-", "A");
        assertTrue(progressPage.areScoreInputsWorking(), "Score inputs should accept values correctly");
        assertTrue(progressPage.areGradeDropdownsWorking(), "Grade dropdowns should work");
        assertTrue(progressPage.areTextAreasForNotesVisible(), "Text areas for detailed notes should be visible");
        
        // Student 2 - Fatima (Good Progress)
        progressPage.recordStudentProgress("Fatima", 78, "B", "Working on Surah Al-Fatiha - 80% complete", 75, "B", "B+");
        assertTrue(progressPage.isConsistentInputInterface(), "Input interface should be consistent");
        
        // Student 3 - Omar (Needs Support)
        progressPage.recordStudentProgress("Omar", 65, "C+", "Struggling with pronunciation - needs extra practice", 60, "C", "C");
        assertTrue(progressPage.areLowerScoresAccepted(), "Lower scores should be accepted");
        assertTrue(progressPage.isNotesFieldForConcernsVisible(), "Notes field for concerns should be visible");
        
        // Bagian 3: Add Teacher Observations
        log.info("📝 Bagian 3: Add Teacher Observations");
        
        // Weekly Class Summary
        progressPage.addClassSummary(CLASS_SUMMARY);
        assertTrue(progressPage.isSummaryFieldAcceptingLongerText(), "Summary field should accept longer text");
        assertTrue(progressPage.isAutoSaveFunctionalityWorking(), "Auto-save functionality should work");
        
        // Identify Students Needing Extra Support
        progressPage.flagStudentForSupport("Omar", "Needs extra practice with pronunciation");
        assertTrue(progressPage.isSupportFlagFunctionalityWorking(), "Support flag functionality should work");
        assertTrue(progressPage.isReasonForSupportFieldAvailable(), "Reason for support field should be available");
        
        // Parent Communication Notes
        progressPage.addParentCommunicationNotes(PARENT_COMMUNICATION);
        assertTrue(progressPage.isParentCommunicationTrackingVisible(), "Parent communication tracking should be visible");
        assertTrue(progressPage.areFollowUpReminderOptionsVisible(), "Follow-up reminder options should be visible");
        
        // Bagian 4: Submit Weekly Progress
        log.info("📝 Bagian 4: Submit Weekly Progress");
        
        // Review Progress Summary
        assertTrue(progressPage.areAllStudentsProgressRecorded(8), "All 8 students should have progress recorded");
        assertTrue(progressPage.areSummaryStatisticsCalculated(), "Summary statistics should be calculated correctly");
        assertFalse(progressPage.isMissingDataHighlighted(), "No missing data should be highlighted");
        
        // Submit Progress Report
        progressPage.submitWeeklyProgress();
        assertTrue(progressPage.isProgressSubmittedSuccessfully(), "Progress should be submitted successfully");
        assertTrue(progressPage.isConfirmationMessageShown(), "Confirmation message should be shown");
        assertTrue(progressPage.isDataSavedToDatabase(), "Data should be saved to database");
        assertTrue(progressPage.isProgressReadOnly(), "Progress should become read-only");
        assertTrue(progressPage.isParentsNotificationTriggered(), "Parents notification should be triggered");
        
        log.info("✅ AKH-HP-006: Weekly progress recording completed successfully!");
    }
}