package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.SystemAlert;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SystemAlertRepository extends JpaRepository<SystemAlert, UUID> {
    
    List<SystemAlert> findByIsResolvedOrderByCreatedAtDesc(Boolean isResolved);
    
    List<SystemAlert> findByIsResolvedAndSeverityOrderByCreatedAtDesc(Boolean isResolved, SystemAlert.Severity severity);
    
    List<SystemAlert> findByTeacherAndIsResolvedOrderByCreatedAtDesc(User teacher, Boolean isResolved);
    
    @Query("SELECT sa FROM SystemAlert sa WHERE sa.createdAt >= :since AND sa.isResolved = false ORDER BY sa.severity DESC, sa.createdAt DESC")
    List<SystemAlert> findRecentUnresolvedAlerts(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(sa) FROM SystemAlert sa WHERE sa.isResolved = false")
    Long countUnresolvedAlerts();
    
    @Query("SELECT COUNT(sa) FROM SystemAlert sa WHERE sa.isResolved = false AND sa.severity = :severity")
    Long countUnresolvedAlertsBySeverity(@Param("severity") SystemAlert.Severity severity);
    
    @Query("SELECT sa FROM SystemAlert sa WHERE sa.alertType = :alertType AND sa.createdAt >= :since")
    List<SystemAlert> findAlertsByTypeAndTime(@Param("alertType") SystemAlert.AlertType alertType, 
                                              @Param("since") LocalDateTime since);
}