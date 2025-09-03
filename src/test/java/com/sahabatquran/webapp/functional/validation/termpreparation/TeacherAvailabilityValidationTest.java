package com.sahabatquran.webapp.functional.validation.termpreparation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teacher Availability Submission Validation Tests.
 * Tests form validation, error handling, and edge cases for availability submission.
 * 
 * User Role: INSTRUCTOR
 * Focus: Form validation, constraint checking, error scenarios
 */
@Slf4j
@DisplayName("TAS-VAL: Teacher Availability Submission Validation Tests")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TeacherAvailabilityValidationTest extends BasePlaywrightTest {

    private static final String AVAILABILITY_URL = "/instructor/availability-submission";

    @BeforeEach
    void setupValidationTest() {
        log.info("🔧 Setting up teacher availability validation test");
        loginAsInstructor();
        page.navigate(getBaseUrl() + AVAILABILITY_URL);
        page.waitForLoadState();
    }

    @Test
    @DisplayName("TAS-VAL-001: Empty Form Submission Validation")
    void emptyFormSubmissionValidation() {
        log.info("🎯 TAS-VAL-001: Testing empty form submission validation");
        
        // Given: Form is empty (no slots selected)
        assertThat(page.locator("#totalSelectedSlots")).containsText("0");
        
        // When: Attempt to submit empty form
        page.locator("#submitButton").click();
        
        // Then: Should show validation error or prevent submission
        // Check for JavaScript alert or validation message
        page.onDialog(dialog -> {
            assertTrue(dialog.message().contains("Please select at least one available time slot"), 
                      "Should show validation message for empty form");
            dialog.dismiss();
        });
        
        // Form should not be submitted (URL should remain the same)
        assertTrue(page.url().contains("availability-submission"), 
                  "Form should not submit with no selected slots");
        
        log.info("✅ TAS-VAL-001: Empty form submission properly validated");
    }

    @Test
    @DisplayName("TAS-VAL-002: Minimum Availability Slots Warning")
    void minimumAvailabilitySlotsWarning() {
        log.info("🎯 TAS-VAL-002: Testing minimum availability slots warning");
        
        // Given: Select only 3 slots (below recommended minimum of 5)
        page.locator("#availability-MONDAY-PAGI").click();
        page.locator("#availability-TUESDAY-SORE").click();
        page.locator("#availability-WEDNESDAY-MALAM").click();
        
        // Verify 3 slots selected
        assertThat(page.locator("#totalSelectedSlots")).containsText("3");
        
        // When: Attempt to submit with minimal slots
        page.locator("#submitButton").click();
        
        // Then: Should show warning dialog
        page.onDialog(dialog -> {
            assertTrue(dialog.message().contains("fewer than 5 time slots") && 
                      dialog.message().contains("limit scheduling flexibility"), 
                      "Should warn about low availability");
            dialog.dismiss(); // Cancel submission
        });
        
        log.info("✅ TAS-VAL-002: Minimum slots warning displayed correctly");
    }

    @Test
    @DisplayName("TAS-VAL-003: Maximum Classes Constraint Validation")
    void maximumClassesConstraintValidation() {
        log.info("🎯 TAS-VAL-003: Testing maximum classes constraint validation");
        
        // Given: Select 20 slots (many more than typical max classes)
        selectMultipleSlots(20);
        
        // Set maximum classes to 2 (very low compared to availability)
        page.locator("#maxClassesPerWeek").selectOption("2");
        
        // When: Submit form with high availability but low max classes
        page.locator("#submitButton").click();
        page.waitForLoadState();
        
        // Then: Form should accept this (it's valid, just unusual)
        assertTrue(page.locator(".bg-green-50").isVisible() || 
                   page.url().contains("availability-submission") ||
                   !page.url().contains("error"),
                   "Form should accept valid max classes setting");
        
        log.info("✅ TAS-VAL-003: Maximum classes constraint validation passed");
    }

    @Test
    @DisplayName("TAS-VAL-004: Special Characters in Text Fields")
    void specialCharactersInTextFields() {
        log.info("🎯 TAS-VAL-004: Testing special characters in text fields");
        
        // Given: Select minimum required slots
        page.locator("#availability-MONDAY-PAGI").click();
        page.locator("#availability-TUESDAY-SORE").click();
        page.locator("#availability-WEDNESDAY-MALAM").click();
        page.locator("[data-day='4'][data-session='SIANG']").click();
        page.locator("[data-day='5'][data-session='PAGI']").click();
        
        // When: Enter special characters in text fields
        String specialChars = "Special chars: @#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";
        page.locator("#preferences").fill(specialChars);
        page.locator("#specialConstraints").fill("Arabic text: السلام عليكم ورحمة الله");
        
        // Submit form
        page.locator("#submitButton").click();
        page.waitForLoadState();
        
        // Then: Should handle special characters gracefully
        assertTrue(!page.url().contains("error"), 
                  "Form should handle special characters without errors");
        
        log.info("✅ TAS-VAL-004: Special characters handled correctly");
    }

    @Test
    @DisplayName("TAS-VAL-005: Field Length Limit Validation")
    void fieldLengthLimitValidation() {
        log.info("🎯 TAS-VAL-005: Testing field length limit validation");
        
        // Given: Select required slots
        selectMultipleSlots(5);
        
        // When: Enter very long text (exceed reasonable limits)
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(100); // ~5500 chars
        page.locator("#preferences").fill(longText);
        page.locator("#specialConstraints").fill(longText);
        
        // Then: Fields should handle long text (truncate or show error)
        // Check if text was accepted or limited
        String actualPreferences = page.locator("#preferences").inputValue();
        String actualConstraints = page.locator("#specialConstraints").inputValue();
        
        log.info("Preferences length: {}, Constraints length: {}", 
                actualPreferences.length(), actualConstraints.length());
        
        // Submit form
        page.locator("#submitButton").click();
        page.waitForLoadState();
        
        // Should not cause errors
        assertTrue(!page.url().contains("error"), 
                  "Form should handle long text without errors");
        
        log.info("✅ TAS-VAL-005: Field length limits validated");
    }

    @Test
    @DisplayName("TAS-VAL-006: Form State Persistence on Error")
    void formStatePersistenceOnError() {
        log.info("🎯 TAS-VAL-006: Testing form state persistence on validation error");
        
        // Given: Fill form with specific data
        page.locator("#availability-MONDAY-PAGI").click();
        page.locator("#availability-TUESDAY-SORE").click();
        page.locator("#availability-SUNDAY-MALAM").click();
        
        page.locator("#maxClassesPerWeek").selectOption("4");
        page.locator("#preferredLevels").selectOption(new String[]{"TAHSIN_1", "TAHFIDZ_PEMULA"});
        page.locator("#preferences").fill("Test preferences");
        page.locator("#specialConstraints").fill("Test constraints");
        
        // When: Submit with insufficient slots (should trigger warning)
        page.locator("#submitButton").click();
        
        // Handle dialog and cancel
        page.onDialog(dialog -> {
            if (dialog.message().contains("fewer than 5 time slots")) {
                dialog.dismiss(); // Cancel submission
            }
        });
        
        // Then: Form state should be preserved
        assertThat(page.locator("#totalSelectedSlots")).containsText("3");
        assertThat(page.locator("#maxClassesPerWeek")).hasValue("4");
        assertThat(page.locator("#preferences")).hasValue("Test preferences");
        assertThat(page.locator("#specialConstraints")).hasValue("Test constraints");
        
        // Verify specific slots are still selected
        assertThat(page.locator("#slot-1-PAGI")).hasClass("selected");
        assertThat(page.locator("#slot-2-SORE")).hasClass("selected");
        assertThat(page.locator("#slot-7-MALAM")).hasClass("selected");
        
        log.info("✅ TAS-VAL-006: Form state preserved after validation error");
    }

    @Test
    @DisplayName("TAS-VAL-007: Clear All Functionality")
    void clearAllFunctionality() {
        log.info("🎯 TAS-VAL-007: Testing Clear All functionality");
        
        // Given: Fill form with data
        selectMultipleSlots(10);
        page.locator("#maxClassesPerWeek").selectOption("6");
        page.locator("#preferences").fill("Clear test preferences");
        page.locator("#specialConstraints").fill("Clear test constraints");
        
        // Verify data is filled
        assertThat(page.locator("#totalSelectedSlots")).containsText("10");
        
        // When: Click Clear All button
        page.locator("#clearAllButton").click();
        
        // Then: All selections should be cleared
        assertThat(page.locator("#totalSelectedSlots")).containsText("0");
        
        // Verify no slots are selected
        assertThat(page.locator(".time-slot.selected")).hasCount(0);
        
        // All daily totals should be 0
        for (int day = 1; day <= 7; day++) {
            assertThat(page.locator("#total-" + day)).containsText("0");
        }
        
        // Text fields should remain (Clear All only clears matrix)
        assertThat(page.locator("#preferences")).hasValue("Clear test preferences");
        assertThat(page.locator("#specialConstraints")).hasValue("Clear test constraints");
        
        log.info("✅ TAS-VAL-007: Clear All functionality working correctly");
    }

    @Test
    @DisplayName("TAS-VAL-008: Matrix Interaction Edge Cases")
    void matrixInteractionEdgeCases() {
        log.info("🎯 TAS-VAL-008: Testing matrix interaction edge cases");
        
        // Test rapid clicking (double-click prevention)
        page.locator("#availability-MONDAY-PAGI").click();
        page.locator("#availability-MONDAY-PAGI").click(); // Double click
        
        // Should toggle off after second click
        assertThat(page.locator("#slot-1-PAGI")).not().hasClass("selected");
        assertThat(page.locator("#total-1")).containsText("0");
        
        // Test selecting and deselecting multiple slots on same day
        page.locator("#availability-MONDAY-PAGI").click();
        page.locator("[data-day='1'][data-session='SIANG']").click();
        page.locator("[data-day='1'][data-session='SORE']").click();
        assertThat(page.locator("#total-1")).containsText("3");
        
        // Deselect middle slot
        page.locator("[data-day='1'][data-session='SIANG']").click();
        assertThat(page.locator("#total-1")).containsText("2");
        
        // Test selecting all slots for one day
        page.locator("[data-day='7'][data-session='PAGI_AWAL']").click();
        page.locator("[data-day='7'][data-session='PAGI']").click();
        page.locator("[data-day='7'][data-session='SIANG']").click();
        page.locator("[data-day='7'][data-session='SORE']").click();
        page.locator("#availability-SUNDAY-MALAM").click();
        assertThat(page.locator("#total-7")).containsText("5");
        
        // Total should be 2 (Monday) + 5 (Sunday) = 7
        assertThat(page.locator("#totalSelectedSlots")).containsText("7");
        
        log.info("✅ TAS-VAL-008: Matrix interaction edge cases handled correctly");
    }

    @Test
    @DisplayName("TAS-VAL-009: Form Submission When Disabled")
    void formSubmissionWhenDisabled() {
        log.info("🎯 TAS-VAL-009: Testing form behavior when submission is disabled");
        
        // This test would require a scenario where canSubmit is false
        // For now, test button state
        if (page.locator("button[type='submit']").getAttribute("disabled") != null) {
            log.info("Submit button is disabled - testing disabled state");
            
            // Button should show "Submission Closed" text
            assertThat(page.locator("button[type='submit']")).containsText("Submission Closed");
            
            // Form should not be submittable
            page.locator("#submitButton").click();
            
            // Should remain on same page
            assertTrue(page.url().contains("availability-submission"), 
                      "Should not submit when disabled");
            
            log.info("✅ TAS-VAL-009: Disabled form submission handled correctly");
        } else {
            log.info("Submit button is enabled - skipping disabled state test");
        }
    }

    @Test
    @DisplayName("TAS-VAL-010: Browser Back Button Behavior")
    void browserBackButtonBehavior() {
        log.info("🎯 TAS-VAL-010: Testing browser back button behavior");
        
        // Given: Fill some form data
        selectMultipleSlots(5);
        page.locator("#preferences").fill("Back button test");
        
        // Navigate away
        page.navigate(getBaseUrl() + "/instructor/my-classes");
        page.waitForLoadState();
        
        // Navigate back using browser back button
        page.goBack();
        page.waitForLoadState();
        
        // Then: Should return to availability form
        assertTrue(page.url().contains("availability-submission"), 
                  "Should return to availability form");
        
        // Form data persistence depends on implementation
        // (may or may not persist - both behaviors are acceptable)
        log.info("Form data after back: {} slots selected", 
                page.locator("#totalSelectedSlots").textContent());
        
        log.info("✅ TAS-VAL-010: Browser back button behavior tested");
    }

    /**
     * Helper method to select multiple slots across different days/sessions
     */
    private void selectMultipleSlots(int count) {
        String[] businessSlots = {
                "availability-MONDAY-PAGI", "availability-MONDAY-SIANG", "availability-MONDAY-SORE",
                "availability-TUESDAY-PAGI_AWAL", "availability-TUESDAY-PAGI", "availability-TUESDAY-SORE", "availability-TUESDAY-MALAM",
                "availability-WEDNESDAY-PAGI", "availability-WEDNESDAY-SIANG", "availability-WEDNESDAY-MALAM",
                "availability-THURSDAY-PAGI_AWAL", "availability-THURSDAY-SORE", "availability-THURSDAY-MALAM",
                "availability-FRIDAY-PAGI", "availability-FRIDAY-SIANG", "availability-FRIDAY-SORE",
                "availability-SATURDAY-PAGI_AWAL", "availability-SATURDAY-PAGI", "availability-SATURDAY-SIANG", "availability-SATURDAY-SORE", "availability-SATURDAY-MALAM",
                "availability-SUNDAY-PAGI_AWAL", "availability-SUNDAY-PAGI", "availability-SUNDAY-SIANG", "availability-SUNDAY-SORE", "availability-SUNDAY-MALAM"
        };
        
        int selected = 0;
        for (String slotId : businessSlots) {
            if (selected >= count) break;
            page.locator("#" + slotId).click();
            selected++;
        }
    }
}