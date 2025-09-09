package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.TeacherAttendance;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherAttendanceRepository extends JpaRepository<TeacherAttendance, UUID> {
    
    Optional<TeacherAttendance> findByClassSessionAndScheduledInstructor(ClassSession classSession, User instructor);
    
    List<TeacherAttendance> findByScheduledInstructorAndClassSessionSessionDateBetween(
        User instructor, LocalDate startDate, LocalDate endDate);
    
    List<TeacherAttendance> findByClassSessionSessionDate(LocalDate sessionDate);
    
    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.classSession.sessionDate = :date AND ta.isPresent = false")
    List<TeacherAttendance> findAbsentTeachersForDate(@Param("date") LocalDate date);
    
    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.classSession.sessionDate = :date AND ta.checkInTime IS NULL AND ta.classSession.sessionStatus = 'SCHEDULED'")
    List<TeacherAttendance> findTeachersNotCheckedInForDate(@Param("date") LocalDate date);
    
    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.checkInTime > :lateThreshold")
    List<TeacherAttendance> findLateCheckIns(@Param("lateThreshold") LocalDateTime lateThreshold);
    
    @Query("SELECT COUNT(ta) FROM TeacherAttendance ta WHERE ta.classSession.sessionDate = :date AND ta.isPresent = true")
    Integer countPresentTeachersForDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(ta) FROM TeacherAttendance ta WHERE ta.classSession.sessionDate = :date AND ta.isPresent = false")
    Integer countAbsentTeachersForDate(@Param("date") LocalDate date);
}