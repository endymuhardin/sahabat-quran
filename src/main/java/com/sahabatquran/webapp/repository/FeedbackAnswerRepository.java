package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.FeedbackAnswer;
import com.sahabatquran.webapp.entity.FeedbackQuestion;
import com.sahabatquran.webapp.entity.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer, UUID> {
    
    Optional<FeedbackAnswer> findByResponseAndQuestion(FeedbackResponse response, FeedbackQuestion question);
    
    List<FeedbackAnswer> findByResponse(FeedbackResponse response);
    
    List<FeedbackAnswer> findByQuestion(FeedbackQuestion question);
    
    @Query("SELECT fa FROM FeedbackAnswer fa WHERE fa.question.campaign.id = :campaignId")
    List<FeedbackAnswer> findByCampaignId(@Param("campaignId") UUID campaignId);
    
    @Query("SELECT AVG(CAST(fa.ratingValue AS double)) FROM FeedbackAnswer fa WHERE fa.question = :question AND fa.ratingValue IS NOT NULL")
    Double calculateAverageRatingForQuestion(@Param("question") FeedbackQuestion question);
    
    @Query("SELECT COUNT(fa) FROM FeedbackAnswer fa WHERE fa.question = :question AND fa.booleanValue = true")
    Long countPositiveResponsesForQuestion(@Param("question") FeedbackQuestion question);
    
    @Query("SELECT fa.selectedOption, COUNT(fa) FROM FeedbackAnswer fa WHERE fa.question = :question AND fa.selectedOption IS NOT NULL GROUP BY fa.selectedOption")
    List<Object[]> countResponsesByOptionForQuestion(@Param("question") FeedbackQuestion question);
}