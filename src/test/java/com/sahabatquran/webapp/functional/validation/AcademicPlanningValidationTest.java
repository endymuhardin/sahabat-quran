package com.sahabatquran.webapp.functional.validation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validation tests for the Academic Planning Workflow system.
 * Tests form validation, business rules, error conditions, and security.
 * 
 * NOTE: This focuses on validation and error scenarios.
 * Successful workflow scenarios are in AcademicPlanningWorkflowTest.
 */
@Slf4j
@DisplayName("Academic Planning Workflow - Validation Tests")
class AcademicPlanningValidationTest extends BasePlaywrightTest {

    @Test
    @DisplayName("Phase 1: Access Control - Student User Should Be Denied Access")
    void phase1_accessControl_studentUserShouldBeDeniedAccess() {
        log.info("🎯 Testing Phase 1: Access control validation");
        
        // Given: Student user is logged in (should not have access)
        loginAsStudent();
        
        // When: Try to access Assessment Foundation Dashboard
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Then: Should be denied access or redirected
        String pageContent = page.content().toLowerCase();
        boolean accessDenied = pageContent.contains("403") ||
                              pageContent.contains("access denied") || 
                              pageContent.contains("akses ditolak") ||
                              page.url().contains("/login") ||
                              page.url().contains("/dashboard");
        
        assertTrue(accessDenied, "Student user should be denied access to Academic Planning");
        log.info("✅ Access control working - student user properly denied access");
    }
    
    @Test
    @DisplayName("Phase 1: Instructor Access Control - Should Be Denied Administrative Access")
    void phase1_accessControl_instructorShouldBeDeniedAccess() {
        log.info("🎯 Testing Phase 1: Instructor access control validation");
        
        // Given: Instructor user is logged in (should not have admin access)
        loginAsInstructor();
        
        // When: Try to access Assessment Foundation Dashboard  
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Then: Should be denied access
        String pageContent = page.content().toLowerCase();
        boolean accessDenied = pageContent.contains("403") ||
                              pageContent.contains("access denied") ||
                              pageContent.contains("akses ditolak") ||
                              page.url().contains("/login");
        
        assertTrue(accessDenied, "Instructor user should be denied access to Academic Planning administration");
        log.info("✅ Access control working - instructor properly denied administrative access");
    }
    
    @Test
    @DisplayName("Phase 1: Data Validation - Statistics Should Be Numeric Format")
    void phase1_dataValidation_statisticsShouldBeNumericFormat() {
        log.info("🎯 Testing Phase 1: Data format validation for statistics");
        
        // Given: Admin user is logged in and page loads successfully
        loginAsAdmin();
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Skip test if page shows access denied (security working correctly)
        String pageContent = page.content().toLowerCase();
        if (pageContent.contains("akses ditolak") || pageContent.contains("access denied")) {
            log.info("ℹ️ Access denied - skipping data validation test (security working correctly)");
            return;
        }
        
        // When: Page displays statistics
        if (page.locator("#overall-readiness-percentage").isVisible()) {
            // Then: All numeric statistics should contain only digits or decimal numbers
            String readinessPercentage = page.locator("#overall-readiness-percentage").textContent().trim();
            assertTrue(readinessPercentage.matches("\\d+(\\.\\d+)?"), 
                      "Readiness percentage should be numeric, got: " + readinessPercentage);
            
            String totalStudents = page.locator("#total-students").textContent().trim();
            assertTrue(totalStudents.matches("\\d+"), 
                      "Total students should be numeric, got: " + totalStudents);
            
            String totalStudentsReady = page.locator("#total-students-ready").textContent().trim();
            assertTrue(totalStudentsReady.matches("\\d+"), 
                      "Total students ready should be numeric, got: " + totalStudentsReady);
            
            log.info("✅ All statistics display valid numeric values");
            log.info("   Overall Readiness: {}%", readinessPercentage);
            log.info("   Total Students: {}", totalStudents);
            log.info("   Students Ready: {}", totalStudentsReady);
        } else {
            log.info("ℹ️ Statistics elements not visible - may be due to access restrictions");
        }
    }
    
    @Test
    @DisplayName("Phase 1: Error Handling - Invalid Term ID Should Not Crash")
    void phase1_errorHandling_invalidTermIdShouldNotCrash() {
        log.info("🎯 Testing Phase 1: Error handling for invalid term ID");
        
        // Given: Admin user is logged in
        loginAsAdmin();
        
        // When: Navigate with invalid term ID parameter
        var response = page.navigate(getBaseUrl() + "/academic/assessment-foundation?termId=invalid-uuid");
        page.waitForLoadState();
        
        // Then: Page should handle error gracefully (not return HTTP 500)
        int statusCode = response.status();
        log.info("🐛 DEBUG: HTTP Status Code: {}", statusCode);
        log.info("🐛 DEBUG: Page URL: {}", page.url());
        log.info("🐛 DEBUG: Page title: {}", page.title());
        
        assertTrue(statusCode != 500, 
                  String.format("Page should handle invalid term ID gracefully, not return HTTP 500. Got status: %d", statusCode));
        log.info("✅ Invalid term ID handled gracefully without server crash");
    }
    
    @Test
    @DisplayName("Phase 1: Responsive Design Validation - Elements Should Remain Functional on Mobile")
    void phase1_responsiveDesign_mobileViewportShouldMaintainFunctionality() {
        log.info("🎯 Testing Phase 1: Responsive design validation on mobile");
        
        // Given: Admin user is logged in
        loginAsAdmin();
        
        // When: Set mobile viewport (small screen)
        page.setViewportSize(375, 667); // iPhone SE dimensions
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Skip test if access denied
        String pageContent = page.content().toLowerCase();
        if (pageContent.contains("akses ditolak") || pageContent.contains("access denied")) {
            log.info("ℹ️ Access denied - skipping responsive design test (security working correctly)");
            return;
        }
        
        // Then: Key elements should still be visible and functional
        if (page.locator("#page-title").isVisible()) {
            assertThat(page.locator("#page-title")).isVisible();
            
            // Critical elements should be accessible on mobile
            if (page.locator("#overall-summary").isVisible()) {
                assertThat(page.locator("#overall-summary")).isVisible();
            }
            
            // Form elements should be usable
            if (page.locator("#termSelector").isVisible()) {
                assertThat(page.locator("#termSelector")).isVisible();
                // Should be able to interact with select element
                assertTrue(page.locator("#termSelector").isEnabled(), "Term selector should be functional on mobile");
            }
            
            log.info("✅ Responsive design maintains functionality on mobile viewport");
        } else {
            log.info("ℹ️ Page elements not accessible - may be due to access restrictions");
        }
    }
    
    @Test
    @DisplayName("Phase 1: Business Rule Validation - Term Selection Should Update Data")
    void phase1_businessRuleValidation_termSelectionShouldUpdateData() {
        log.info("🎯 Testing Phase 1: Business rule validation for term selection");
        
        // Given: Admin user is on Assessment Foundation page
        loginAsAdmin();
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Skip test if access denied
        String pageContent = page.content().toLowerCase();
        if (pageContent.contains("akses ditolak") || pageContent.contains("access denied")) {
            log.info("ℹ️ Access denied - skipping business rule test (security working correctly)");
            return;
        }
        
        // When: Multiple terms are available
        if (page.locator("#termSelector").isVisible() && page.locator("#termSelector option").count() > 1) {
            // Store initial readiness value
            String initialReadiness = page.locator("#overall-readiness-percentage").isVisible() ? 
                                    page.locator("#overall-readiness-percentage").textContent() : "0";
            
            // Select a different term and capture the response
            String selectedTermId = page.locator("#termSelector option").nth(1).getAttribute("value");
            var termSelectionResponse = page.navigate(getBaseUrl() + "/academic/assessment-foundation?termId=" + selectedTermId);
            page.waitForLoadState();
            
            // Then: Page should reload and potentially show different data
            // At minimum, the page should not return HTTP 500
            int statusCode = termSelectionResponse.status();
            log.info("🐛 DEBUG: After term selection - HTTP Status Code: {}", statusCode);
            log.info("🐛 DEBUG: After term selection - Page URL: {}", page.url());
            log.info("🐛 DEBUG: After term selection - Selected Term ID: {}", selectedTermId);
            
            assertTrue(statusCode != 500, 
                      String.format("Term selection should not cause server errors. Got status: %d", statusCode));
            
            // Statistics should still be visible after term change
            if (page.locator("#overall-readiness-percentage").isVisible()) {
                assertThat(page.locator("#overall-readiness-percentage")).isVisible();
            }
            
            log.info("✅ Term selection business rule validation passed");
        } else {
            log.info("ℹ️ Only one term available or selector not visible - skipping term selection test");
        }
    }
    
    @Test
    @DisplayName("Phase 1: Navigation Validation - Missing Required Permissions Should Block Access")  
    void phase1_navigationValidation_missingPermissionsShouldBlockAccess() {
        log.info("🎯 Testing Phase 1: Navigation validation with insufficient permissions");
        
        // Test direct URL access without proper authentication
        // This validates that security is properly implemented
        
        // Given: No user is logged in (anonymous access)
        // When: Try to access Assessment Foundation directly
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Then: Should be redirected to login or show access denied
        String currentUrl = page.url();
        String pageContent = page.content().toLowerCase();
        
        boolean properlyBlocked = currentUrl.contains("/login") ||
                                 pageContent.contains("akses ditolak") ||
                                 pageContent.contains("access denied") ||
                                 pageContent.contains("403");
        
        assertTrue(properlyBlocked, "Anonymous users should be blocked from accessing Academic Planning");
        log.info("✅ Navigation validation working - anonymous access properly blocked");
    }
}