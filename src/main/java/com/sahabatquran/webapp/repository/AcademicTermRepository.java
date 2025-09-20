package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, UUID> {
    
    Optional<AcademicTerm> findByTermName(String termName);
    
    List<AcademicTerm> findByStatus(AcademicTerm.TermStatus status);
    
    default List<AcademicTerm> findActiveTerms() {
        return findByStatus(AcademicTerm.TermStatus.ACTIVE);
    }
    
    default List<AcademicTerm> findPlanningTerms() {
        return findByStatus(AcademicTerm.TermStatus.PLANNING).stream()
               .sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
               .toList();
    }
    
    @Query("SELECT at FROM AcademicTerm at WHERE :date BETWEEN at.startDate AND at.endDate")
    List<AcademicTerm> findTermsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT at FROM AcademicTerm at WHERE at.preparationDeadline >= :date ORDER BY at.preparationDeadline")
    List<AcademicTerm> findTermsWithUpcomingPreparationDeadline(@Param("date") LocalDate date);
    
    @Query("SELECT at FROM AcademicTerm at ORDER BY at.startDate DESC")
    List<AcademicTerm> findAllOrderByStartDateDesc();

    @Query("SELECT at FROM AcademicTerm at ORDER BY at.termName DESC")
    List<AcademicTerm> findAllByOrderByTermNameDesc();
}