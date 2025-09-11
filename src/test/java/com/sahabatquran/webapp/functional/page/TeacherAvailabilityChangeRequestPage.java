package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Playwright Page Object for Teacher Availability Change Request functionality.
 * 
 * Handles teacher availability change request submission and management workflows.
 * Scenario: TA-HP-003 - Request Schedule Change After Submission
 */
public class TeacherAvailabilityChangeRequestPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator availabilityMenu;
    private final Locator changeRequestBtn;
    
    // Availability status locators
    private final Locator availabilityStatus;
    private final Locator submissionDate;
    private final Locator currentAvailability;
    
    // Change request form locators
    private final Locator changeRequestForm;
    private final Locator reasonField;
    private final Locator currentAvailabilitySummary;
    
    // Slot modification locators
    private final Locator slotsToRemove;
    private final Locator slotsToAdd;
    private final Locator newMaxClassesField;
    private final Locator impactCalculation;
    
    // Submission locators
    private final Locator submitButton;
    private final Locator successMessage;
    
    // Request status locators
    private final Locator requestTable;
    private final Locator statusPending;
    private final Locator requestDetails;
    private final Locator requestId;
    
    // Admin approval locators
    private final Locator pendingRequest;
    private final Locator approveButton;
    private final Locator rejectButton;
    private final Locator adminComments;
    
    public TeacherAvailabilityChangeRequestPage(Page page) {
        this.page = page;
        
        // Initialize locators with IDs
        this.availabilityMenu = page.locator("#nav-availability");
        this.changeRequestBtn = page.locator("#request-change-btn");
        
        // Availability status
        this.availabilityStatus = page.locator("#availability-status");
        this.submissionDate = page.locator("#submission-date");
        this.currentAvailability = page.locator("#current-availability");
        
        // Change request form
        this.changeRequestForm = page.locator("#change-request-form");
        this.reasonField = page.locator("#reason");
        this.currentAvailabilitySummary = page.locator("#current-availability-summary");
        
        // Slot modifications
        this.slotsToRemove = page.locator("input[name='slotsToRemove']");
        this.slotsToAdd = page.locator("input[name='slotsToAdd']");
        this.newMaxClassesField = page.locator("#newMaxClasses");
        this.impactCalculation = page.locator("#impact-calculation");
        
        // Submission
        this.submitButton = page.locator("#btn-submit-change-request");
        this.successMessage = page.locator("#success-message");
        
        // Request status - use indexed IDs for first row
        this.requestTable = page.locator("#change-requests-table");
        this.statusPending = page.locator("#status-pending-0");
        this.requestDetails = page.locator("#request-details-0");
        this.requestId = page.locator("#request-id-0");
        
        // Admin approval
        this.pendingRequest = page.locator("#pending-request");
        this.approveButton = page.locator("#btn-approve");
        this.rejectButton = page.locator("#btn-reject");
        this.adminComments = page.locator("#admin-comments");
    }
    
    // Navigation methods
    public void navigateToAvailability(String baseUrl) {
        page.navigate(baseUrl + "/instructor/availability");
        page.waitForLoadState();
    }
    
    public void clickRequestChange() {
        changeRequestBtn.click();
        page.waitForLoadState();
    }
    
    // Verification methods
    public boolean isAvailabilitySubmitted() {
        return availabilityStatus.textContent().contains("SUBMITTED");
    }
    
    public boolean isSubmissionDateVisible() {
        return submissionDate.isVisible();
    }
    
    public boolean isCurrentAvailabilityVisible() {
        return currentAvailability.isVisible();
    }
    
    public boolean isChangeRequestFormVisible() {
        return changeRequestForm.isVisible();
    }
    
    public boolean isCurrentAvailabilitySummaryVisible() {
        return currentAvailabilitySummary.isVisible();
    }
    
    // Form interaction methods
    public void fillReason(String reason) {
        reasonField.fill(reason);
    }
    
    public void selectSlotToRemove(String slotValue) {
        page.locator("input[name='slotsToRemove'][value='" + slotValue + "']").check();
    }
    
    public void selectSlotToAdd(String slotValue) {
        page.locator("input[name='slotsToAdd'][value='" + slotValue + "']").check();
    }
    
    public void setMaxClasses(String maxClasses) {
        newMaxClassesField.fill(maxClasses);
    }
    
    public void submitChangeRequest() {
        submitButton.click();
        page.waitForLoadState();
    }
    
    // Status verification methods
    public boolean isSuccessMessageVisible() {
        return successMessage.isVisible();
    }
    
    public boolean isRequestTableVisible() {
        return requestTable.isVisible();
    }
    
    public boolean isPendingStatusVisible() {
        // Wait a bit for the page to render
        page.waitForTimeout(500);
        
        // Check if there's a status container with PENDING status
        // Try multiple approaches to find the PENDING status
        if (page.locator("#status-container-0[data-status='PENDING']").count() > 0) {
            return true;
        }
        if (page.locator("#status-pending-0").count() > 0) {
            return true;
        }
        // Also check for the class-based approach
        if (page.locator(".status-pending").first().count() > 0) {
            return true;
        }
        return false;
    }
    
    public boolean isRequestDetailsVisible() {
        return requestDetails.isVisible();
    }
    
    public boolean isRequestIdVisible() {
        return requestId.isVisible();
    }
    
    // Admin methods
    public void navigateToManageChangeRequests(String baseUrl) {
        page.navigate(baseUrl + "/management/change-requests");
        page.waitForLoadState();
    }
    
    public boolean isPendingRequestVisible() {
        return pendingRequest.isVisible();
    }
    
    public void approveRequest() {
        approveButton.click();
        page.waitForLoadState();
    }
    
    public void rejectRequest() {
        rejectButton.click();
        page.waitForLoadState();
    }
    
    public void addAdminComments(String comments) {
        if (adminComments.isVisible()) {
            adminComments.fill(comments);
        }
    }
    
    // Page title verification
    public boolean hasCorrectPageTitle() {
        return page.locator("h1").textContent().contains("Request Availability Change");
    }
    
    // Check if we're on the change requests list page
    public boolean isOnChangeRequestsListPage() {
        return page.locator("h1").textContent().contains("My Change Requests");
    }
    
    public boolean isImpactCalculationVisible() {
        return impactCalculation.isVisible();
    }
    
    public boolean impactCalculationContains(String text) {
        return impactCalculation.textContent().contains(text);
    }
}