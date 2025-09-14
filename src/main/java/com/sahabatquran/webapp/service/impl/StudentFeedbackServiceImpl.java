package com.sahabatquran.webapp.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahabatquran.webapp.dto.FeedbackSubmissionDto;
import com.sahabatquran.webapp.dto.StudentFeedbackDto;
import com.sahabatquran.webapp.entity.Enrollment;
import com.sahabatquran.webapp.entity.FeedbackAnswer;
import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.FeedbackQuestion;
import com.sahabatquran.webapp.entity.FeedbackResponse;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.EnrollmentRepository;
import com.sahabatquran.webapp.repository.FeedbackAnswerRepository;
import com.sahabatquran.webapp.repository.FeedbackCampaignRepository;
import com.sahabatquran.webapp.repository.FeedbackQuestionRepository;
import com.sahabatquran.webapp.repository.FeedbackResponseRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.StudentFeedbackService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentFeedbackServiceImpl implements StudentFeedbackService {
    
    private final FeedbackCampaignRepository campaignRepository;
    private final FeedbackResponseRepository responseRepository;
    private final FeedbackQuestionRepository questionRepository;
    private final FeedbackAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    private static final String TOKEN_SALT = "YSQ_FEEDBACK_2024";
    
    @Override
    public List<StudentFeedbackDto.CampaignSummary> getActiveCampaignsForStudent(UUID studentId) {
        log.info("Getting active feedback campaigns for student: {}", studentId);
        
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Get student's current enrollments
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByStudent(studentId);
        
        // Get active campaigns
        LocalDate today = LocalDate.now();
        List<FeedbackCampaign> activeCampaigns = campaignRepository.findByIsActiveTrueOrderByStartDateDesc()
            .stream()
            .filter(campaign -> !campaign.getStartDate().isAfter(today) && 
                               !campaign.getEndDate().isBefore(today))
            .collect(Collectors.toList());
        
        // Map to DTOs and check submission status
        return activeCampaigns.stream()
            .map(campaign -> {
                boolean hasSubmitted = hasStudentSubmittedFeedback(campaign.getId(), studentId);
                
                return StudentFeedbackDto.CampaignSummary.builder()
                    .campaignId(campaign.getId())
                    .campaignName(campaign.getCampaignName())
                    .campaignType(campaign.getCampaignType().toString())
                    .startDate(campaign.getStartDate())
                    .endDate(campaign.getEndDate())
                    .isAnonymous(campaign.getIsAnonymous())
                    .hasSubmitted(hasSubmitted)
                    .daysRemaining(java.time.Period.between(today, campaign.getEndDate()).getDays())
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public StudentFeedbackDto.CampaignDetails getCampaignDetails(UUID campaignId, UUID studentId) {
        log.info("Getting campaign details for campaign: {} and student: {}", campaignId, studentId);
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        // Check if already submitted
        boolean hasSubmitted = hasStudentSubmittedFeedback(campaignId, studentId);
        
        // Get questions
        List<StudentFeedbackDto.QuestionDto> questions = campaign.getQuestions().stream()
            .map(q -> StudentFeedbackDto.QuestionDto.builder()
                .questionId(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType().toString())
                .isRequired(q.getIsRequired())
                .orderIndex(q.getQuestionNumber())
                .options(q.getOptions())
                .build())
            .collect(Collectors.toList());
        
        return StudentFeedbackDto.CampaignDetails.builder()
            .campaignId(campaign.getId())
            .campaignName(campaign.getCampaignName())
            .description(campaign.getCampaignName()) // Using name as description
            .campaignType(campaign.getCampaignType().toString())
            .isAnonymous(campaign.getIsAnonymous())
            .totalQuestions(questions.size())
            .estimatedTime(questions.size() * 2) // 2 minutes per question estimate
            .hasSubmitted(hasSubmitted)
            .questions(questions)
            .build();
    }
    
    @Override
    public boolean hasStudentSubmittedFeedback(UUID campaignId, UUID studentId) {
        String anonymousToken = generateAnonymousToken(studentId, campaignId);
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        Optional<FeedbackResponse> existingResponse = responseRepository
            .findByCampaignAndAnonymousToken(campaign, anonymousToken);
        
        return existingResponse.isPresent() && existingResponse.get().getIsComplete();
    }
    
    @Override
    @Cacheable(value = "anonymousTokens", key = "#studentId + '_' + #campaignId")
    public String getOrCreateAnonymousToken(UUID studentId, UUID campaignId) {
        return generateAnonymousToken(studentId, campaignId);
    }
    
    private String generateAnonymousToken(UUID studentId, UUID campaignId) {
        try {
            String input = studentId.toString() + campaignId.toString() + TOKEN_SALT;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            
            // Convert to hex string and take first 20 characters
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 20);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating anonymous token", e);
        }
    }
    
    @Override
    @Transactional
    public FeedbackSubmissionDto.SubmissionResult submitFeedback(
            UUID campaignId, 
            UUID studentId, 
            FeedbackSubmissionDto.FeedbackData feedbackData) {
        
        log.info("Submitting feedback for campaign: {} by student: {}", campaignId, studentId);
        
        // Check for duplicate submission
        if (hasStudentSubmittedFeedback(campaignId, studentId)) {
            return FeedbackSubmissionDto.SubmissionResult.builder()
                .success(false)
                .message("Anda sudah memberikan feedback untuk campaign ini")
                .isDuplicate(true)
                .build();
        }
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        String anonymousToken = generateAnonymousToken(studentId, campaignId);
        
        // Check if partial response exists (with eager loading of answers)
        Optional<FeedbackResponse> existingPartial = responseRepository
            .findByCampaignAndAnonymousTokenWithAnswers(campaign, anonymousToken);
        
        FeedbackResponse response;
        if (existingPartial.isPresent()) {
            response = existingPartial.get();
            // Update existing answers or add new ones - don't clear to avoid orphaned entities
            for (FeedbackSubmissionDto.AnswerData answerData : feedbackData.getAnswers()) {
                FeedbackAnswer targetAnswer = response.getAnswers().stream()
                    .filter(a -> a.getQuestion().getId().equals(answerData.getQuestionId()))
                    .findFirst()
                    .orElse(null);
                
                if (targetAnswer == null) {
                    // Create new answer only if it doesn't exist
                    targetAnswer = new FeedbackAnswer();
                    targetAnswer.setQuestion(questionRepository.findById(answerData.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found")));
                    response.addAnswer(targetAnswer);
                }
                
                // Update the answer values
                targetAnswer.setTextValue(answerData.getAnswerText());
                targetAnswer.setRatingValue(answerData.getRating());
            }
        } else {
            response = new FeedbackResponse();
            response.setCampaign(campaign);
            response.setAnonymousToken(anonymousToken);
            response.setDeviceInfo(feedbackData.getDeviceInfo());
            
            // Add new answers
            for (FeedbackSubmissionDto.AnswerData answerData : feedbackData.getAnswers()) {
                FeedbackAnswer answer = new FeedbackAnswer();
                answer.setQuestion(questionRepository.findById(answerData.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found")));
                answer.setTextValue(answerData.getAnswerText());
                answer.setRatingValue(answerData.getRating());
                response.addAnswer(answer);
            }
        }
        
        response.markComplete();
        responseRepository.save(response);
        
        // Update campaign response count
        campaign.setCurrentResponses(campaign.getCurrentResponses() + 1);
        campaign.updateResponseRate();
        campaignRepository.save(campaign);
        
        log.info("Feedback submitted successfully with anonymous token: {}", anonymousToken);
        
        return FeedbackSubmissionDto.SubmissionResult.builder()
            .success(true)
            .message("Feedback berhasil dikirim. Terima kasih atas partisipasi Anda!")
            .submissionId(response.getId())
            .anonymousToken(anonymousToken)
            .submittedAt(response.getSubmissionDate())
            .build();
    }
    
    @Override
    @Transactional
    public void savePartialFeedback(
            UUID campaignId,
            UUID studentId,
            FeedbackSubmissionDto.PartialData partialData) {
        
        log.info("Saving partial feedback for campaign: {} by student: {}", campaignId, studentId);
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        String anonymousToken = generateAnonymousToken(studentId, campaignId);
        
        // Use saveOrUpdate pattern to handle concurrent saves properly
        FeedbackResponse response;
        boolean isNewResponse = false;
        
        // First, check if response already exists
        Optional<FeedbackResponse> existingOpt = responseRepository
            .findByCampaignAndAnonymousTokenWithAnswers(campaign, anonymousToken);
        
        if (existingOpt.isPresent()) {
            // Update existing response
            response = existingOpt.get();
            log.debug("Found existing partial response, updating it");
        } else {
            // Create new response
            response = new FeedbackResponse();
            response.setCampaign(campaign);
            response.setAnonymousToken(anonymousToken);
            response.setIsComplete(false);
            isNewResponse = true;
            log.debug("Creating new partial response");
        }
        
        // Update partial answers - avoid duplicates
        for (FeedbackSubmissionDto.AnswerData answerData : partialData.getPartialAnswers()) {
            // Find existing answer for this question
            FeedbackAnswer targetAnswer = response.getAnswers().stream()
                .filter(a -> a.getQuestion().getId().equals(answerData.getQuestionId()))
                .findFirst()
                .orElse(null);
            
            if (targetAnswer == null) {
                // Create new answer only if it doesn't exist
                targetAnswer = new FeedbackAnswer();
                targetAnswer.setQuestion(questionRepository.findById(answerData.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found")));
                response.addAnswer(targetAnswer);
            }
            
            // Update the answer values
            targetAnswer.setTextValue(answerData.getAnswerText());
            targetAnswer.setRatingValue(answerData.getRating());
        }
        
        try {
            responseRepository.save(response);
            log.info("Partial feedback saved successfully (new: {})", isNewResponse);
        } catch (DataIntegrityViolationException e) {
            // This can happen if another thread created the record between our check and save
            if (isNewResponse) {
                log.warn("Concurrent creation detected, fetching existing record");
                // Fetch the existing record created by another thread
                FeedbackResponse concurrentResponse = responseRepository
                    .findByCampaignAndAnonymousTokenWithAnswers(campaign, anonymousToken)
                    .orElseThrow(() -> new RuntimeException("Failed to find concurrent response"));
                
                // Update the existing response with our data - avoid duplicates
                for (FeedbackSubmissionDto.AnswerData answerData : partialData.getPartialAnswers()) {
                    FeedbackAnswer targetAnswer = concurrentResponse.getAnswers().stream()
                        .filter(a -> a.getQuestion().getId().equals(answerData.getQuestionId()))
                        .findFirst()
                        .orElse(null);
                    
                    if (targetAnswer == null) {
                        targetAnswer = new FeedbackAnswer();
                        targetAnswer.setQuestion(questionRepository.findById(answerData.getQuestionId())
                            .orElseThrow(() -> new RuntimeException("Question not found")));
                        concurrentResponse.addAnswer(targetAnswer);
                    }
                    
                    targetAnswer.setTextValue(answerData.getAnswerText());
                    targetAnswer.setRatingValue(answerData.getRating());
                }
                
                responseRepository.save(concurrentResponse);
                log.info("Partial feedback saved successfully after handling concurrent creation");
            } else {
                // This shouldn't happen for updates, re-throw
                throw e;
            }
        }
    }
    
    @Override
    public StudentFeedbackDto.ResumeData resumeFeedback(UUID campaignId, UUID studentId) {
        log.info("Resuming feedback for campaign: {} by student: {}", campaignId, studentId);
        
        String anonymousToken = generateAnonymousToken(studentId, campaignId);
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        Optional<FeedbackResponse> existingResponse = responseRepository
            .findByCampaignAndAnonymousTokenWithAnswers(campaign, anonymousToken);
        
        if (existingResponse.isEmpty() || existingResponse.get().getIsComplete()) {
            return StudentFeedbackDto.ResumeData.builder()
                .canResume(false)
                .build();
        }
        
        FeedbackResponse response = existingResponse.get();
        
        // Map existing answers
        Map<UUID, FeedbackSubmissionDto.AnswerData> savedAnswers = response.getAnswers().stream()
            .collect(Collectors.toMap(
                answer -> answer.getQuestion().getId(),
                answer -> FeedbackSubmissionDto.AnswerData.builder()
                    .questionId(answer.getQuestion().getId())
                    .answerText(answer.getTextValue())
                    .rating(answer.getRatingValue())
                    .build()
            ));
        
        return StudentFeedbackDto.ResumeData.builder()
            .canResume(true)
            .lastSaved(response.getSubmissionDate())
            .completedQuestions(savedAnswers.size())
            .savedAnswers(savedAnswers)
            .build();
    }
    
    @Override
    public FeedbackSubmissionDto.ValidationResult validateSubmission(
            UUID campaignId,
            FeedbackSubmissionDto.FeedbackData feedbackData) {
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        List<String> errors = new ArrayList<>();
        
        // Check if campaign is active
        LocalDate today = LocalDate.now();
        if (today.isBefore(campaign.getStartDate())) {
            errors.add("Campaign belum dimulai");
        }
        if (today.isAfter(campaign.getEndDate())) {
            errors.add("Campaign sudah berakhir");
        }
        
        // Check required questions
        Set<UUID> answeredQuestions = feedbackData.getAnswers().stream()
            .map(FeedbackSubmissionDto.AnswerData::getQuestionId)
            .collect(Collectors.toSet());
        
        for (FeedbackQuestion question : campaign.getQuestions()) {
            if (question.getIsRequired() && !answeredQuestions.contains(question.getId())) {
                errors.add("Pertanyaan wajib belum dijawab: " + question.getQuestionText());
            }
        }
        
        return FeedbackSubmissionDto.ValidationResult.builder()
            .isValid(errors.isEmpty())
            .errors(errors)
            .build();
    }
    
    @Override
    public List<StudentFeedbackDto.SubmissionHistory> getStudentFeedbackHistory(UUID studentId) {
        log.info("Getting feedback history for student: {}", studentId);
        
        // Get all campaigns
        List<FeedbackCampaign> allCampaigns = campaignRepository.findAll();
        
        List<StudentFeedbackDto.SubmissionHistory> history = new ArrayList<>();
        
        for (FeedbackCampaign campaign : allCampaigns) {
            String anonymousToken = generateAnonymousToken(studentId, campaign.getId());
            Optional<FeedbackResponse> response = responseRepository
                .findByCampaignAndAnonymousToken(campaign, anonymousToken);
            
            if (response.isPresent() && response.get().getIsComplete()) {
                history.add(StudentFeedbackDto.SubmissionHistory.builder()
                    .campaignId(campaign.getId())
                    .campaignName(campaign.getCampaignName())
                    .submittedAt(response.get().getSubmissionDate())
                    .campaignType(campaign.getCampaignType().toString())
                    .build());
            }
        }
        
        return history;
    }
    
    @Override
    @Transactional
    public void markCampaignAsViewed(UUID campaignId, UUID studentId) {
        log.info("Marking campaign {} as viewed by student {}", campaignId, studentId);
        // This could be implemented with a separate tracking table if needed
        // For now, just log the action
    }
    
    @Override
    public StudentFeedbackDto.CompletionStatus getCampaignCompletionStatus(UUID campaignId, UUID studentId) {
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        String anonymousToken = generateAnonymousToken(studentId, campaignId);
        Optional<FeedbackResponse> response = responseRepository
            .findByCampaignAndAnonymousToken(campaign, anonymousToken);
        
        if (response.isEmpty()) {
            return StudentFeedbackDto.CompletionStatus.NOT_STARTED;
        } else if (response.get().getIsComplete()) {
            return StudentFeedbackDto.CompletionStatus.COMPLETED;
        } else {
            return StudentFeedbackDto.CompletionStatus.IN_PROGRESS;
        }
    }
    
    @Override
    public boolean recoverSession(String sessionToken, UUID studentId) {
        // Session recovery logic
        // This would typically involve checking a session cache or database
        log.info("Attempting to recover session for student: {}", studentId);
        return true; // Simplified for now
    }
    
    @Override
    public StudentFeedbackDto.SubmissionConfirmation getSubmissionConfirmation(
            UUID campaignId, 
            UUID studentId,
            String anonymousToken) {
        
        FeedbackCampaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        FeedbackResponse response = responseRepository
            .findByCampaignAndAnonymousToken(campaign, anonymousToken)
            .orElseThrow(() -> new RuntimeException("Response not found"));
        
        return StudentFeedbackDto.SubmissionConfirmation.builder()
            .confirmationId(response.getId())
            .campaignName(campaign.getCampaignName())
            .submittedAt(response.getSubmissionDate())
            .message("Terima kasih! Feedback Anda telah berhasil dikirim dan akan sangat membantu kami dalam meningkatkan kualitas pembelajaran.")
            .build();
    }
}