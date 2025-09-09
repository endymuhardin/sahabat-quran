package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ParentNotification;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ParentNotificationRepository extends JpaRepository<ParentNotification, UUID> {
    
    List<ParentNotification> findByStudentOrderByCreatedAtDesc(User student);
    
    List<ParentNotification> findByParentOrderByCreatedAtDesc(User parent);
    
    List<ParentNotification> findByIsSentFalse();
    
    List<ParentNotification> findByIsSentTrueAndIsReadFalse();
    
    List<ParentNotification> findByNotificationType(ParentNotification.NotificationType notificationType);
    
    @Query("SELECT pn FROM ParentNotification pn WHERE pn.createdAt >= :since ORDER BY pn.createdAt DESC")
    List<ParentNotification> findRecentNotifications(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(pn) FROM ParentNotification pn WHERE pn.isSent = false")
    Long countUnsentNotifications();
    
    @Query("SELECT COUNT(pn) FROM ParentNotification pn WHERE pn.isSent = true AND pn.isRead = false")
    Long countUnreadNotifications();
    
    @Query("SELECT pn FROM ParentNotification pn WHERE pn.deliveryMethod = :method AND pn.isSent = false")
    List<ParentNotification> findUnsentByDeliveryMethod(@Param("method") ParentNotification.DeliveryMethod method);
}