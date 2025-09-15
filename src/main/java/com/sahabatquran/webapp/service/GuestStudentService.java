package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.GuestStudent;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.GuestStudentRepository;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GuestStudentService {

    private final GuestStudentRepository guestStudentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;

    public GuestStudent addGuestStudent(UUID sessionId, String guestName, String reason, GuestStudent.GuestType guestType, String addedByUsername) {
        log.info("Adding guest student {} to session {} by {}", guestName, sessionId, addedByUsername);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        
        User addedBy = userRepository.findByUsername(addedByUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + addedByUsername));
        
        GuestStudent guestStudent = new GuestStudent();
        guestStudent.setSession(session);
        guestStudent.setGuestName(guestName);
        guestStudent.setReason(reason);
        guestStudent.setGuestType(guestType);
        guestStudent.setAddedBy(addedBy);
        guestStudent.setIsPresent(true);
        
        GuestStudent saved = guestStudentRepository.save(guestStudent);
        
        // Notify admin about guest students
        notifyAdminAboutGuestStudent(saved);
        
        log.info("Guest student added successfully: {}", saved.getId());
        return saved;
    }
    
    public List<GuestStudent> getGuestStudentsBySession(UUID sessionId) {
        return guestStudentRepository.findBySessionId(sessionId);
    }
    
    public int countGuestStudentsBySession(UUID sessionId) {
        return (int) guestStudentRepository.countBySessionId(sessionId);
    }
    
    private void notifyAdminAboutGuestStudent(GuestStudent guestStudent) {
        // In a real implementation, this would send a notification to admin
        log.info("Admin notified about guest student: {} in session {}", 
            guestStudent.getGuestName(), guestStudent.getSession().getId());
    }
}