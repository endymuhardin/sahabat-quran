package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Attendance;
import com.sahabatquran.webapp.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    
    Optional<Attendance> findByEnrollmentAndAttendanceDate(Enrollment enrollment, LocalDate attendanceDate);
    
    List<Attendance> findByEnrollmentAndAttendanceDateBetween(Enrollment enrollment, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.isPresent = true")
    List<Attendance> findPresentStudentsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.isPresent = false")
    List<Attendance> findAbsentStudentsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.enrollment = :enrollment AND a.isPresent = true AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Integer countPresentDaysForEnrollment(@Param("enrollment") Enrollment enrollment, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date AND a.isPresent = true")
    Integer countPresentStudentsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date")
    Integer countTotalAttendanceRecordsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT AVG(CASE WHEN a.isPresent = true THEN 1.0 ELSE 0.0 END) * 100 FROM Attendance a WHERE a.attendanceDate = :date")
    Double calculateAttendanceRateForDate(@Param("date") LocalDate date);
}