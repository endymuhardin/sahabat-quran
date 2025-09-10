package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.FeedbackSubmissionDto;
import com.sahabatquran.webapp.dto.StudentFeedbackDto;
import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.FeedbackResponse;
import com.sahabatquran.webapp.entity.User;

import java.util.List;
import java.util.UUID;

public interface StudentFeedbackService {
    
    /**
     * Get active feedback campaigns for a student
     */
    List<StudentFeedbackDto.CampaignSummary> getActiveCampaignsForStudent(UUID studentId);
    
    /**
     * Get feedback campaign details with questions
     */
    StudentFeedbackDto.CampaignDetails getCampaignDetails(UUID campaignId, UUID studentId);
    
    /**
     * Check if student has already submitted feedback for a campaign
     */
    boolean hasStudentSubmittedFeedback(UUID campaignId, UUID studentId);
    
    /**
     * Get or create anonymous token for student-campaign combination
     */
    String getOrCreateAnonymousToken(UUID studentId, UUID campaignId);
    
    /**
     * Submit feedback response
     */
    FeedbackSubmissionDto.SubmissionResult submitFeedback(
        UUID campaignId, 
        UUID studentId, 
        FeedbackSubmissionDto.FeedbackData feedbackData
    );
    
    /**
     * Save partial feedback (auto-save functionality)
     */
    void savePartialFeedback(
        UUID campaignId,
        UUID studentId,
        FeedbackSubmissionDto.PartialData partialData
    );
    
    /**
     * Resume incomplete feedback submission
     */
    StudentFeedbackDto.ResumeData resumeFeedback(UUID campaignId, UUID studentId);
    
    /**
     * Validate feedback submission for completeness
     */
    FeedbackSubmissionDto.ValidationResult validateSubmission(
        UUID campaignId,
        FeedbackSubmissionDto.FeedbackData feedbackData
    );
    
    /**
     * Get student's submitted feedback history
     */
    List<StudentFeedbackDto.SubmissionHistory> getStudentFeedbackHistory(UUID studentId);
    
    /**
     * Mark campaign as viewed by student (for notification tracking)
     */
    void markCampaignAsViewed(UUID campaignId, UUID studentId);
    
    /**
     * Get campaign completion status for student
     */
    StudentFeedbackDto.CompletionStatus getCampaignCompletionStatus(UUID campaignId, UUID studentId);
    
    /**
     * Handle session expiry and recovery
     */
    boolean recoverSession(String sessionToken, UUID studentId);
    
    /**
     * Generate feedback submission certificate/confirmation
     */
    StudentFeedbackDto.SubmissionConfirmation getSubmissionConfirmation(
        UUID campaignId, 
        UUID studentId,
        String anonymousToken
    );
}