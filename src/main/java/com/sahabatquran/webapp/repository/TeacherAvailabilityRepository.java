package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.TeacherAvailability;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, UUID> {
    
    List<TeacherAvailability> findByTeacher(User teacher);
    
    List<TeacherAvailability> findByTerm(AcademicTerm term);
    
    List<TeacherAvailability> findByTeacherAndTerm(User teacher, AcademicTerm term);
    
    Optional<TeacherAvailability> findByTeacherAndTermAndDayOfWeekAndSession(
            User teacher, AcademicTerm term, TeacherAvailability.DayOfWeek dayOfWeek, Session session);
    
    @Query("SELECT ta FROM TeacherAvailability ta WHERE ta.term.id = :termId AND ta.isAvailable = true")
    List<TeacherAvailability> findAvailableSlotsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT ta FROM TeacherAvailability ta " +
           "WHERE ta.teacher.id = :teacherId AND ta.term.id = :termId AND ta.isAvailable = true")
    List<TeacherAvailability> findAvailableSlotsByTeacherAndTerm(
            @Param("teacherId") UUID teacherId, @Param("termId") UUID termId);
    
    @Query("SELECT ta FROM TeacherAvailability ta " +
           "WHERE ta.dayOfWeek = :dayOfWeek AND ta.session = :session AND ta.isAvailable = true")
    List<TeacherAvailability> findAvailableTeachersByDayAndSession(
            @Param("dayOfWeek") TeacherAvailability.DayOfWeek dayOfWeek, 
            @Param("session") Session session);
    
    @Query("SELECT COUNT(ta) FROM TeacherAvailability ta " +
           "WHERE ta.term.id = :termId AND ta.isAvailable = true")
    Long countAvailableSlotsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT ta.teacher, COUNT(ta) as availableSlots FROM TeacherAvailability ta " +
           "WHERE ta.term.id = :termId AND ta.isAvailable = true " +
           "GROUP BY ta.teacher ORDER BY availableSlots DESC")
    List<Object[]> findTeacherAvailabilitySummary(@Param("termId") UUID termId);
    
    @Query("SELECT DISTINCT ta.teacher FROM TeacherAvailability ta WHERE ta.term.id = :termId")
    List<User> findTeachersWhoSubmittedAvailability(@Param("termId") UUID termId);
}