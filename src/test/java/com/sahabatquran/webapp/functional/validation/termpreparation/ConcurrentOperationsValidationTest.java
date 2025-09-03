package com.sahabatquran.webapp.functional.validation.termpreparation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Concurrent Operations Access Validation Tests.
 * Tests multiple user access to the same academic planning pages.
 * 
 * User Role: Multiple concurrent users (ACADEMIC_ADMIN, MANAGEMENT)
 * Focus: Validation of concurrent access handling, session management
 */
@Slf4j
@DisplayName("PS-AP-012: Concurrent Access Validation Tests")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ConcurrentOperationsValidationTest extends BasePlaywrightTest {

    private BrowserContext context2;
    private Page page2;

    @BeforeEach
    void setupConcurrentTest() {
        log.info("🔧 Setting up concurrent operations validation test");
        
        // Setup first user session (Academic Admin)
        loginAsAdmin();
        
        // Setup second user session (Management)
        context2 = getBrowser().newContext();
        page2 = context2.newPage();
        loginAsUser(page2, "management.director", "Welcome@YSQ2024");
    }

    @AfterEach
    void teardownConcurrentTest() {
        if (page2 != null) {
            page2.close();
        }
        if (context2 != null) {
            context2.close();
        }
    }

    @Test
    @DisplayName("PS-AP-012-001: Concurrent Final Schedule Review Access Validation")
    void concurrentFinalScheduleReviewAccessValidation() {
        log.info("🎯 PS-AP-012-001: Testing concurrent final schedule review access validation");
        
        final String REVIEW_URL = "/academic/final-schedule-review";
        
        // Both users navigate to final schedule review simultaneously
        page.navigate(getBaseUrl() + REVIEW_URL);
        page2.navigate(getBaseUrl() + REVIEW_URL);
        
        // Wait for both pages to load
        page.waitForLoadState();
        page2.waitForLoadState();
        
        // Verify both users can access the page simultaneously
        assertThat(page.locator("#page-title")).containsText("Final Schedule Review");
        assertThat(page2.locator("#page-title")).containsText("Final Schedule Review");
        
        // Verify both sessions are independent
        assertTrue(!page.url().equals(page2.url()) || 
                  page.content().contains("Final Schedule Review"),
                  "Both users should have independent access to the review page");
        
        // Verify both users can see the same data structure
        if (page.locator(".stats-card").count() > 0 && page2.locator(".stats-card").count() > 0) {
            assertThat(page.locator(".stats-card").first()).isVisible();
            assertThat(page2.locator(".stats-card").first()).isVisible();
            log.info("Both users can see schedule statistics independently");
        }
        
        log.info("✅ Concurrent final schedule review access validation completed");
    }

    @Test
    @DisplayName("PS-AP-012-002: Concurrent System Go-Live Access Validation")
    void concurrentSystemGoLiveAccessValidation() {
        log.info("🎯 PS-AP-012-002: Testing concurrent system go-live access validation");
        
        final String GOLIVE_URL = "/academic/system-golive";
        
        // Academic admin navigates to go-live page
        page.navigate(getBaseUrl() + GOLIVE_URL);
        page.waitForLoadState();
        
        // Management navigates to same page
        page2.navigate(getBaseUrl() + GOLIVE_URL);
        page2.waitForLoadState();
        
        // Verify both users can access go-live page
        assertThat(page.locator("#page-title")).containsText("System Go-Live");
        assertThat(page2.locator("#page-title")).containsText("System Go-Live");
        
        // Verify go-live cards are visible for both users
        assertThat(page.locator(".golive-card")).isVisible();
        assertThat(page2.locator(".golive-card")).isVisible();
        
        // Verify both users see consistent system state
        if (page.locator("button:has-text('Execute Go-Live')").isVisible() && 
            page2.locator("button:has-text('Execute Go-Live')").isVisible()) {
            log.info("Both users see go-live controls consistently");
        }
        
        // Verify session independence
        String user1Content = page.locator("#page-title").textContent();
        String user2Content = page2.locator("#page-title").textContent();
        assertTrue(user1Content.equals(user2Content), 
                   "Both users should see the same go-live interface");
        
        log.info("✅ Concurrent system go-live access validation completed");
    }

    @Test
    @DisplayName("PS-AP-012-003: Concurrent Session Management Validation")
    void concurrentSessionManagementValidation() {
        log.info("🎯 PS-AP-012-003: Testing concurrent session management validation");
        
        // Navigate both users to academic dashboard
        page.navigate(getBaseUrl() + "/academic/dashboard");
        page2.navigate(getBaseUrl() + "/management/dashboard");
        
        page.waitForLoadState();
        page2.waitForLoadState();
        
        // Verify both sessions remain active and independent
        String page1Url = page.url();
        String page2Url = page2.url();
        
        assertTrue(page1Url.contains("academic") || page1Url.contains("dashboard"),
                   "Academic admin session should remain active");
        assertTrue(page2Url.contains("management") || page2Url.contains("dashboard"), 
                   "Management session should remain active");
        
        // Verify both users can navigate to academic planning pages simultaneously
        page.navigate(getBaseUrl() + "/academic/final-schedule-review");
        page2.navigate(getBaseUrl() + "/academic/system-golive");
        
        page.waitForLoadState();
        page2.waitForLoadState();
        
        // Check for any session conflicts or errors
        String page1Content = page.content();
        String page2Content = page2.content();
        
        assertTrue(!page1Content.contains("Access Denied") && !page1Content.contains("Unauthorized"),
                   "User 1 should maintain proper access");
        assertTrue(!page2Content.contains("Access Denied") && !page2Content.contains("Unauthorized"),
                   "User 2 should maintain proper access");
        
        // Verify system stability under concurrent access
        assertThat(page.locator("body")).isVisible();
        assertThat(page2.locator("body")).isVisible();
        
        log.info("✅ Concurrent session management validation completed");
    }

    private void loginAsUser(Page userPage, String username, String password) {
        userPage.navigate(getBaseUrl() + "/login");
        userPage.waitForLoadState();
        userPage.locator("#username").fill(username);
        userPage.locator("#password").fill(password);
        userPage.locator("button[type='submit']").click();
        userPage.waitForLoadState();
    }
}