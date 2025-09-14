package com.sahabatquran.webapp.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahabatquran.webapp.dto.FeedbackCampaignDto;
import com.sahabatquran.webapp.dto.FeedbackSubmissionDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.FeedbackAnswer;
import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.FeedbackQuestion;
import com.sahabatquran.webapp.entity.FeedbackResponse;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.ClassGroupRepository;
import com.sahabatquran.webapp.repository.FeedbackAnswerRepository;
import com.sahabatquran.webapp.repository.FeedbackCampaignRepository;
import com.sahabatquran.webapp.repository.FeedbackQuestionRepository;
import com.sahabatquran.webapp.repository.FeedbackResponseRepository;
import com.sahabatquran.webapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FeedbackService {
    
    private final FeedbackCampaignRepository feedbackCampaignRepository;
    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final FeedbackResponseRepository feedbackResponseRepository;
    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicTermRepository academicTermRepository;
    
    /**
     * Create a new feedback campaign
     */
    public FeedbackCampaignDto createCampaign(FeedbackCampaignDto campaignDto, UUID createdBy) {
        log.info("Creating feedback campaign: {}", campaignDto.getCampaignName());
        
        FeedbackCampaign campaign = new FeedbackCampaign();
        campaign.setCampaignName(campaignDto.getCampaignName());
        campaign.setCampaignType(FeedbackCampaign.CampaignType.valueOf(campaignDto.getCampaignType()));
        campaign.setTargetAudience(FeedbackCampaign.TargetAudience.valueOf(campaignDto.getTargetAudience()));
        campaign.setStartDate(campaignDto.getStartDate());
        campaign.setEndDate(campaignDto.getEndDate());
        campaign.setIsAnonymous(campaignDto.getIsAnonymous());
        campaign.setMinResponsesRequired(campaignDto.getMinResponsesRequired());
        
        if (campaignDto.getTermId() != null) {
            AcademicTerm term = academicTermRepository.findById(campaignDto.getTermId())
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));
            campaign.setTerm(term);
        }
        
        User creator = userRepository.findById(createdBy)
            .orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        campaign.setCreatedBy(creator);
        
        campaign = feedbackCampaignRepository.save(campaign);
        
        // Add questions if provided
        if (campaignDto.getQuestions() != null) {
            for (FeedbackCampaignDto.FeedbackQuestionDto questionDto : campaignDto.getQuestions()) {
                FeedbackQuestion question = new FeedbackQuestion();
                question.setCampaign(campaign);
                question.setQuestionNumber(questionDto.getQuestionNumber());
                question.setQuestionText(questionDto.getQuestionText());
                question.setQuestionType(FeedbackQuestion.QuestionType.valueOf(questionDto.getQuestionType()));
                question.setQuestionCategory(questionDto.getQuestionCategory());
                question.setIsRequired(questionDto.getIsRequired());
                question.setOptions(questionDto.getOptions());
                question.setMinValue(questionDto.getMinValue());
                question.setMaxValue(questionDto.getMaxValue());
                
                feedbackQuestionRepository.save(question);
                campaign.addQuestion(question);
            }
        }
        
        log.info("Feedback campaign created successfully: {}", campaign.getId());
        return mapToCampaignDto(campaign);
    }
    
    /**
     * Get active feedback campaigns for a target audience
     */
    @Transactional(readOnly = true)
    public List<FeedbackCampaignDto> getActiveCampaigns(String targetAudience) {
        LocalDate today = LocalDate.now();
        List<FeedbackCampaign> campaigns = feedbackCampaignRepository
            .findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
        
        return campaigns.stream()
            .filter(campaign -> campaign.getTargetAudience().name().equals(targetAudience) 
                || campaign.getTargetAudience() == FeedbackCampaign.TargetAudience.BOTH)
            .map(this::mapToCampaignDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Start anonymous feedback session
     */
    @Transactional(readOnly = true)
    public FeedbackCampaignDto startFeedbackSession(UUID campaignId, UUID targetTeacherId, UUID classGroupId) {
        FeedbackCampaign campaign = feedbackCampaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        
        FeedbackCampaignDto dto = mapToCampaignDto(campaign);
        
        // Generate anonymous token for this session
        String anonymousToken = generateAnonymousToken();
        
        // Create response record (not saved yet, just for token generation)
        FeedbackResponse response = new FeedbackResponse();
        response.setCampaign(campaign);
        response.setAnonymousToken(anonymousToken);
        
        if (targetTeacherId != null) {
            User teacher = userRepository.findById(targetTeacherId).orElse(null);
            response.setTargetTeacher(teacher);
        }
        
        if (classGroupId != null) {
            ClassGroup classGroup = classGroupRepository.findById(classGroupId).orElse(null);
            response.setClassGroup(classGroup);
        }
        
        // Don't save yet - will be saved when first answer is submitted
        
        return dto;
    }
    
    /**
     * Submit feedback responses
     */
    public FeedbackSubmissionDto submitFeedback(FeedbackSubmissionDto submissionDto) {
        log.info("Processing feedback submission for campaign: {}", submissionDto.getCampaignId());
        
        FeedbackCampaign campaign = feedbackCampaignRepository.findById(submissionDto.getCampaignId())
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        
        // Find or create response
        FeedbackResponse response = feedbackResponseRepository
            .findByCampaignAndAnonymousToken(campaign, submissionDto.getAnonymousToken())
            .orElseGet(() -> {
                FeedbackResponse newResponse = new FeedbackResponse();
                newResponse.setCampaign(campaign);
                newResponse.setAnonymousToken(submissionDto.getAnonymousToken());
                newResponse.setDeviceInfo(submissionDto.getDeviceInfo());
                
                if (submissionDto.getTargetTeacherId() != null) {
                    User teacher = userRepository.findById(submissionDto.getTargetTeacherId()).orElse(null);
                    newResponse.setTargetTeacher(teacher);
                }
                
                if (submissionDto.getClassGroupId() != null) {
                    ClassGroup classGroup = classGroupRepository.findById(submissionDto.getClassGroupId()).orElse(null);
                    newResponse.setClassGroup(classGroup);
                }
                
                return feedbackResponseRepository.save(newResponse);
            });
        
        // Process answers
        for (FeedbackSubmissionDto.AnswerDto answerDto : submissionDto.getAnswers()) {
            FeedbackQuestion question = feedbackQuestionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
            
            // Find or create answer
            FeedbackAnswer answer = feedbackAnswerRepository
                .findByResponseAndQuestion(response, question)
                .orElseGet(() -> {
                    FeedbackAnswer newAnswer = new FeedbackAnswer();
                    newAnswer.setResponse(response);
                    newAnswer.setQuestion(question);
                    return newAnswer;
                });
            
            // Set answer value based on question type
            answer.setRatingValue(answerDto.getRatingValue());
            answer.setBooleanValue(answerDto.getBooleanValue());
            answer.setTextValue(answerDto.getTextValue());
            answer.setSelectedOption(answerDto.getSelectedOption());
            answer.setScaleValue(answerDto.getScaleValue());
            
            feedbackAnswerRepository.save(answer);
            response.addAnswer(answer);
        }
        
        // Mark as complete if all required questions are answered
        boolean allRequiredAnswered = checkAllRequiredQuestionsAnswered(response);
        if (allRequiredAnswered) {
            response.markComplete();
            campaign.setCurrentResponses(campaign.getCurrentResponses() + 1);
            campaign.updateResponseRate();
            feedbackCampaignRepository.save(campaign);
        }
        
        feedbackResponseRepository.save(response);
        
        submissionDto.setResponseId(response.getId());
        submissionDto.setIsComplete(response.getIsComplete());
        submissionDto.setSubmissionStatus(allRequiredAnswered ? "COMPLETED" : "IN_PROGRESS");
        
        log.info("Feedback submission processed successfully: {}", response.getId());
        return submissionDto;
    }
    
    /**
     * Get campaign statistics
     */
    @Transactional(readOnly = true)
    public FeedbackCampaignDto getCampaignStatistics(UUID campaignId) {
        FeedbackCampaign campaign = feedbackCampaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        
        FeedbackCampaignDto dto = mapToCampaignDto(campaign);
        
        // Calculate additional statistics
        long completedResponses = feedbackResponseRepository.countByCampaignAndIsComplete(campaign, true);
        dto.setCurrentResponses((int) completedResponses);
        
        if (dto.getMinResponsesRequired() != null && dto.getMinResponsesRequired() > 0) {
            dto.setResponseRate(
                java.math.BigDecimal.valueOf(completedResponses)
                    .divide(java.math.BigDecimal.valueOf(dto.getMinResponsesRequired()), 2, java.math.BigDecimal.ROUND_HALF_UP)
                    .multiply(java.math.BigDecimal.valueOf(100))
            );
        }
        
        return dto;
    }
    
    /**
     * Get feedback responses for analysis (admin only)
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getCampaignResponses(UUID campaignId, boolean onlyCompleted) {
        FeedbackCampaign campaign = feedbackCampaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        
        if (onlyCompleted) {
            return feedbackResponseRepository.findByCampaignAndIsComplete(campaign, true);
        } else {
            return feedbackResponseRepository.findByCampaign(campaign);
        }
    }
    
    private FeedbackCampaignDto mapToCampaignDto(FeedbackCampaign campaign) {
        FeedbackCampaignDto dto = new FeedbackCampaignDto();
        dto.setCampaignId(campaign.getId());
        dto.setCampaignName(campaign.getCampaignName());
        dto.setCampaignType(campaign.getCampaignType().name());
        dto.setTargetAudience(campaign.getTargetAudience().name());
        dto.setTermId(campaign.getTerm() != null ? campaign.getTerm().getId() : null);
        dto.setTermName(campaign.getTerm() != null ? campaign.getTerm().getTermName() : null);
        dto.setStartDate(campaign.getStartDate());
        dto.setEndDate(campaign.getEndDate());
        dto.setIsAnonymous(campaign.getIsAnonymous());
        dto.setIsActive(campaign.getIsActive());
        dto.setMinResponsesRequired(campaign.getMinResponsesRequired());
        dto.setCurrentResponses(campaign.getCurrentResponses());
        dto.setResponseRate(campaign.getResponseRate());
        
        // Calculate status and days remaining
        LocalDate today = LocalDate.now();
        if (today.isBefore(campaign.getStartDate())) {
            dto.setStatus("UPCOMING");
            dto.setDaysRemaining(Period.between(today, campaign.getStartDate()).getDays());
        } else if (today.isAfter(campaign.getEndDate())) {
            dto.setStatus("COMPLETED");
            dto.setDaysRemaining(0);
        } else {
            dto.setStatus("ACTIVE");
            dto.setDaysRemaining(Period.between(today, campaign.getEndDate()).getDays());
        }
        
        // Map questions
        if (campaign.getQuestions() != null && !campaign.getQuestions().isEmpty()) {
            List<FeedbackCampaignDto.FeedbackQuestionDto> questionDtos = campaign.getQuestions().stream()
                .map(this::mapToQuestionDto)
                .collect(Collectors.toList());
            dto.setQuestions(questionDtos);
        }
        
        return dto;
    }
    
    private FeedbackCampaignDto.FeedbackQuestionDto mapToQuestionDto(FeedbackQuestion question) {
        FeedbackCampaignDto.FeedbackQuestionDto dto = new FeedbackCampaignDto.FeedbackQuestionDto();
        dto.setQuestionId(question.getId());
        dto.setQuestionNumber(question.getQuestionNumber());
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType().name());
        dto.setQuestionCategory(question.getQuestionCategory());
        dto.setIsRequired(question.getIsRequired());
        dto.setOptions(question.getOptions());
        dto.setMinValue(question.getMinValue());
        dto.setMaxValue(question.getMaxValue());
        return dto;
    }
    
    private boolean checkAllRequiredQuestionsAnswered(FeedbackResponse response) {
        List<FeedbackQuestion> requiredQuestions = feedbackQuestionRepository
            .findByCampaignAndIsRequiredTrue(response.getCampaign());
        
        List<UUID> answeredQuestionIds = response.getAnswers().stream()
            .map(answer -> answer.getQuestion().getId())
            .collect(Collectors.toList());
        
        return requiredQuestions.stream()
            .allMatch(question -> answeredQuestionIds.contains(question.getId()));
    }
    
    private String generateAnonymousToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}