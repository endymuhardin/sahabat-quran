package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.SubstituteTeacher;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubstituteTeacherRepository extends JpaRepository<SubstituteTeacher, UUID> {
    
    Optional<SubstituteTeacher> findByTeacher(User teacher);
    
    List<SubstituteTeacher> findByIsAvailableTrue();
    
    List<SubstituteTeacher> findByIsAvailableTrueAndEmergencyAvailableTrue();
    
    List<SubstituteTeacher> findByEmergencyAvailableTrue();
    
    @Query("SELECT st FROM SubstituteTeacher st WHERE st.isAvailable = true AND st.rating >= :minRating ORDER BY st.rating DESC")
    List<SubstituteTeacher> findAvailableSubstitutesByMinRating(@Param("minRating") java.math.BigDecimal minRating);
    
    default List<SubstituteTeacher> findAvailableSubstitutes() {
        return findByIsAvailableTrue();
    }
    
    @Query("SELECT st FROM SubstituteTeacher st WHERE st.lastSubstitutionDate IS NULL OR st.lastSubstitutionDate < :beforeDate")
    List<SubstituteTeacher> findSubstitutesNotRecentlyUsed(@Param("beforeDate") LocalDate beforeDate);
    
    @Query("SELECT AVG(st.rating) FROM SubstituteTeacher st WHERE st.rating IS NOT NULL")
    java.math.BigDecimal findAverageRating();
}