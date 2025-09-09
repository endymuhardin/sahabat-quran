package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.FeedbackQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, UUID> {
    
    List<FeedbackQuestion> findByCampaignOrderByQuestionNumber(FeedbackCampaign campaign);
    
    List<FeedbackQuestion> findByCampaignAndIsRequiredTrue(FeedbackCampaign campaign);
    
    List<FeedbackQuestion> findByQuestionType(FeedbackQuestion.QuestionType questionType);
    
    List<FeedbackQuestion> findByQuestionCategory(String questionCategory);
    
    @Query("SELECT fq FROM FeedbackQuestion fq WHERE fq.campaign.id = :campaignId ORDER BY fq.questionNumber")
    List<FeedbackQuestion> findByCampaignIdOrderByQuestionNumber(@Param("campaignId") UUID campaignId);
    
    @Query("SELECT COUNT(fq) FROM FeedbackQuestion fq WHERE fq.campaign = :campaign AND fq.isRequired = true")
    Long countRequiredQuestionsByCampaign(@Param("campaign") FeedbackCampaign campaign);
}