package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.FeedbackResponse;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, UUID> {
    
    Optional<FeedbackResponse> findByCampaignAndAnonymousToken(FeedbackCampaign campaign, String anonymousToken);
    
    @Query("SELECT fr FROM FeedbackResponse fr LEFT JOIN FETCH fr.answers WHERE fr.campaign = :campaign AND fr.anonymousToken = :anonymousToken")
    Optional<FeedbackResponse> findByCampaignAndAnonymousTokenWithAnswers(@Param("campaign") FeedbackCampaign campaign, @Param("anonymousToken") String anonymousToken);
    
    List<FeedbackResponse> findByCampaign(FeedbackCampaign campaign);
    
    List<FeedbackResponse> findByCampaignAndIsComplete(FeedbackCampaign campaign, Boolean isComplete);
    
    List<FeedbackResponse> findByTargetTeacher(User targetTeacher);
    
    Long countByCampaign(FeedbackCampaign campaign);
    
    Long countByCampaignAndIsComplete(FeedbackCampaign campaign, Boolean isComplete);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.campaign = :campaign AND fr.submissionDate BETWEEN :startDate AND :endDate")
    List<FeedbackResponse> findByCampaignAndDateRange(@Param("campaign") FeedbackCampaign campaign,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.targetTeacher = :teacher AND fr.isComplete = true ORDER BY fr.submissionDate DESC")
    List<FeedbackResponse> findCompletedResponsesByTeacher(@Param("teacher") User teacher);
    
    @Query("SELECT COUNT(DISTINCT fr.anonymousToken) FROM FeedbackResponse fr WHERE fr.campaign = :campaign")
    Long countUniqueResponsesByCampaign(@Param("campaign") FeedbackCampaign campaign);
}