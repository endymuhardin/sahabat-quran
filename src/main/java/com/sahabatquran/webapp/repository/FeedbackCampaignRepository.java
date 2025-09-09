package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.FeedbackCampaign;
import com.sahabatquran.webapp.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackCampaignRepository extends JpaRepository<FeedbackCampaign, UUID> {
    
    List<FeedbackCampaign> findByIsActiveTrueOrderByStartDateDesc();
    
    List<FeedbackCampaign> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        LocalDate startCheck, LocalDate endCheck);
    
    List<FeedbackCampaign> findByCampaignType(FeedbackCampaign.CampaignType campaignType);
    
    List<FeedbackCampaign> findByTargetAudience(FeedbackCampaign.TargetAudience targetAudience);
    
    List<FeedbackCampaign> findByTerm(AcademicTerm term);
    
    @Query("SELECT fc FROM FeedbackCampaign fc WHERE fc.endDate < :date AND fc.isActive = true")
    List<FeedbackCampaign> findExpiredCampaigns(@Param("date") LocalDate date);
    
    @Query("SELECT fc FROM FeedbackCampaign fc WHERE fc.startDate <= :date AND fc.endDate >= :date AND fc.isActive = true")
    List<FeedbackCampaign> findActiveCampaignsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT fc FROM FeedbackCampaign fc WHERE fc.currentResponses >= fc.minResponsesRequired AND fc.isActive = true")
    List<FeedbackCampaign> findCampaignsWithSufficientResponses();
    
    // Method for monitoring service
    default List<FeedbackCampaign> findActiveCampaigns() {
        return findActiveCampaignsForDate(LocalDate.now());
    }
}