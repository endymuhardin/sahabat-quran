package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.GuestStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GuestStudentRepository extends JpaRepository<GuestStudent, UUID> {

    List<GuestStudent> findBySessionId(UUID sessionId);

    long countBySessionId(UUID sessionId);

    List<GuestStudent> findByGuestType(GuestStudent.GuestType guestType);

    @Query("SELECT g FROM GuestStudent g WHERE g.session.id = :sessionId AND g.isPresent = true")
    List<GuestStudent> findPresentGuestsBySession(@Param("sessionId") UUID sessionId);

    @Query("SELECT COUNT(g) FROM GuestStudent g WHERE g.session.id = :sessionId AND g.isPresent = true")
    long countPresentGuestsBySession(@Param("sessionId") UUID sessionId);

    @Query("SELECT g FROM GuestStudent g WHERE g.createdAt >= :startDate AND g.createdAt <= :endDate")
    List<GuestStudent> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    long countByGuestType(GuestStudent.GuestType guestType);
}