package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    
    List<Session> findByIsActiveTrueOrderByStartTime();
    
    Optional<Session> findByCodeAndIsActiveTrue(String code);
    
    @Query("SELECT s FROM Session s WHERE s.isActive = true AND s.startTime BETWEEN :startTime AND :endTime ORDER BY s.startTime")
    List<Session> findActiveByTimeRange(LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT s FROM Session s WHERE s.isActive = true AND s.startTime >= :morningStart AND s.startTime < :afternoonStart ORDER BY s.startTime")
    List<Session> findMorningSessions(LocalTime morningStart, LocalTime afternoonStart);
    
    @Query("SELECT s FROM Session s WHERE s.isActive = true AND s.startTime >= :afternoonStart ORDER BY s.startTime")
    List<Session> findAfternoonSessions(LocalTime afternoonStart);
    
    boolean existsByCodeAndIsActiveTrue(String code);
}