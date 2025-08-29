package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.StudentRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, UUID> {
    
    Page<StudentRegistration> findByRegistrationStatusOrderByCreatedAtDesc(
            StudentRegistration.RegistrationStatus status, Pageable pageable);
    
    Page<StudentRegistration> findByRegistrationStatusInOrderByCreatedAtDesc(
            List<StudentRegistration.RegistrationStatus> statuses, Pageable pageable);
    
    @Query("SELECT sr FROM StudentRegistration sr WHERE sr.registrationStatus = :status AND sr.program.id = :programId ORDER BY sr.createdAt DESC")
    Page<StudentRegistration> findByStatusAndProgram(
            @Param("status") StudentRegistration.RegistrationStatus status, 
            @Param("programId") UUID programId, 
            Pageable pageable);
    
    @Query("SELECT sr FROM StudentRegistration sr WHERE sr.placementTestStatus = :status ORDER BY sr.submittedAt ASC")
    List<StudentRegistration> findByPlacementTestStatus(@Param("status") StudentRegistration.PlacementTestStatus status);
    
    @Query("SELECT sr FROM StudentRegistration sr WHERE sr.placementTestStatus = :status AND sr.placementVerse.difficultyLevel = :level ORDER BY sr.submittedAt ASC")
    List<StudentRegistration> findByPlacementTestStatusAndLevel(
            @Param("status") StudentRegistration.PlacementTestStatus status, 
            @Param("level") Integer level);
    
    Optional<StudentRegistration> findByEmailAndRegistrationStatusNot(String email, StudentRegistration.RegistrationStatus status);
    
    Optional<StudentRegistration> findByPhoneNumberAndRegistrationStatusNot(String phoneNumber, StudentRegistration.RegistrationStatus status);
    
    @Query("SELECT COUNT(sr) FROM StudentRegistration sr WHERE sr.registrationStatus = :status")
    long countByRegistrationStatus(@Param("status") StudentRegistration.RegistrationStatus status);
    
    @Query("SELECT COUNT(sr) FROM StudentRegistration sr WHERE sr.placementTestStatus = :status")
    long countByPlacementTestStatus(@Param("status") StudentRegistration.PlacementTestStatus status);
    
    @Query("SELECT COUNT(sr) FROM StudentRegistration sr WHERE sr.createdAt >= :startDate AND sr.createdAt < :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sr FROM StudentRegistration sr WHERE " +
           "(:fullName IS NULL OR :fullName = '' OR LOWER(sr.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:email IS NULL OR :email = '' OR LOWER(sr.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:phoneNumber IS NULL OR :phoneNumber = '' OR sr.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
           "(:status IS NULL OR sr.registrationStatus = :status) AND " +
           "(:programId IS NULL OR sr.program.id = :programId) " +
           "ORDER BY sr.createdAt DESC")
    Page<StudentRegistration> searchRegistrations(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("status") StudentRegistration.RegistrationStatus status,
            @Param("programId") UUID programId,
            Pageable pageable);
    
    boolean existsByEmailAndRegistrationStatusNot(String email, StudentRegistration.RegistrationStatus status);
    
    boolean existsByPhoneNumberAndRegistrationStatusNot(String phoneNumber, StudentRegistration.RegistrationStatus status);
    
    // Teacher assignment methods
    List<StudentRegistration> findByAssignedTeacherIdOrderByAssignedAtDesc(UUID teacherId);
}