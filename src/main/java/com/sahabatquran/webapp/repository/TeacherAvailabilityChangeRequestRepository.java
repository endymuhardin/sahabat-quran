package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.TeacherAvailabilityChangeRequest;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherAvailabilityChangeRequestRepository extends JpaRepository<TeacherAvailabilityChangeRequest, UUID> {
    
    List<TeacherAvailabilityChangeRequest> findByTeacherAndTermOrderBySubmittedAtDesc(User teacher, AcademicTerm term);
    
    List<TeacherAvailabilityChangeRequest> findByTeacherOrderBySubmittedAtDesc(User teacher);
    
    List<TeacherAvailabilityChangeRequest> findByStatusOrderBySubmittedAtAsc(TeacherAvailabilityChangeRequest.RequestStatus status);
    
    List<TeacherAvailabilityChangeRequest> findByTermAndStatusOrderBySubmittedAtAsc(AcademicTerm term, TeacherAvailabilityChangeRequest.RequestStatus status);
    
    @Query("SELECT cr FROM TeacherAvailabilityChangeRequest cr WHERE cr.teacher.id = :teacherId AND cr.term.id = :termId AND cr.status = 'PENDING'")
    Optional<TeacherAvailabilityChangeRequest> findPendingRequestByTeacherAndTerm(@Param("teacherId") UUID teacherId, @Param("termId") UUID termId);
    
    @Query("SELECT COUNT(cr) FROM TeacherAvailabilityChangeRequest cr WHERE cr.status = 'PENDING'")
    long countPendingRequests();
    
    @Query("SELECT cr FROM TeacherAvailabilityChangeRequest cr WHERE cr.term.id = :termId ORDER BY cr.submittedAt DESC")
    List<TeacherAvailabilityChangeRequest> findByTermOrderBySubmittedAtDesc(@Param("termId") UUID termId);
}