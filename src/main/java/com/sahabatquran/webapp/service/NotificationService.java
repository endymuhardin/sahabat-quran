package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.ParentNotification;
import com.sahabatquran.webapp.entity.SubstituteAssignment;
import com.sahabatquran.webapp.repository.ParentNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    
    private final ParentNotificationRepository parentNotificationRepository;
    // Add SMS/Email service dependencies here
    
    /**
     * Send notification to substitute teacher about assignment
     */
    public void sendSubstituteAssignmentNotification(SubstituteAssignment assignment) {
        log.info("Sending substitute assignment notification to teacher: {}", 
            assignment.getSubstituteTeacher().getFullName());
        
        // In a real implementation, you would send SMS/WhatsApp/Email here
        // For now, just log the notification
        
        String message = String.format(
            "Assalamualaikum Ustadz/Ustadzah %s. Anda diminta menggantikan mengajar kelas %s pada %s. " +
            "Alasan: %s. Silakan konfirmasi penerimaan melalui aplikasi. Terima kasih.",
            assignment.getSubstituteTeacher().getFullName(),
            assignment.getClassSession().getClassGroup().getName(),
            assignment.getClassSession().getSessionDate(),
            assignment.getReason()
        );
        
        log.info("SMS notification would be sent to {}: {}", 
            assignment.getSubstituteTeacher().getPhoneNumber(), message);
    }
    
    /**
     * Send confirmation notification after substitute accepts
     */
    public void sendSubstituteConfirmationNotification(SubstituteAssignment assignment) {
        log.info("Sending substitute confirmation notification for assignment: {}", assignment.getId());
        
        String message = String.format(
            "Terima kasih Ustadz/Ustadzah %s telah menyetujui penggantian mengajar kelas %s pada %s. " +
            "Lokasi: %s. Instruksi khusus: %s",
            assignment.getSubstituteTeacher().getFullName(),
            assignment.getClassSession().getClassGroup().getName(),
            assignment.getClassSession().getSessionDate(),
            assignment.getClassSession().getClassGroup().getLocation(),
            assignment.getSpecialInstructions() != null ? assignment.getSpecialInstructions() : "Tidak ada"
        );
        
        log.info("Confirmation notification: {}", message);
    }
    
    /**
     * Process pending notifications (to be called by scheduled job)
     */
    @Transactional
    public void processPendingNotifications() {
        log.info("Processing pending notifications");
        
        // Get all unsent notifications
        parentNotificationRepository.findByIsSentFalse()
            .forEach(this::sendNotification);
    }
    
    private void sendNotification(ParentNotification notification) {
        try {
            switch (notification.getDeliveryMethod()) {
                case SMS:
                    sendSMS(notification);
                    break;
                case EMAIL:
                    sendEmail(notification);
                    break;
                case WHATSAPP:
                    sendWhatsApp(notification);
                    break;
                case IN_APP:
                    sendInAppNotification(notification);
                    break;
            }
            
            notification.markAsSent();
            parentNotificationRepository.save(notification);
            
        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
        }
    }
    
    private void sendSMS(ParentNotification notification) {
        // Implement SMS sending logic here
        log.info("Sending SMS to {}: {}", notification.getRecipientContact(), notification.getMessage());
    }
    
    private void sendEmail(ParentNotification notification) {
        // Implement email sending logic here
        log.info("Sending email to {}: {}", notification.getRecipientContact(), notification.getSubject());
    }
    
    private void sendWhatsApp(ParentNotification notification) {
        // Implement WhatsApp sending logic here
        log.info("Sending WhatsApp to {}: {}", notification.getRecipientContact(), notification.getMessage());
    }
    
    private void sendInAppNotification(ParentNotification notification) {
        // Implement in-app notification logic here
        log.info("Sending in-app notification to {}: {}", notification.getStudent().getFullName(), notification.getSubject());
    }
}