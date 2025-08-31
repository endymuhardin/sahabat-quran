package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {
    
    Optional<Level> findByName(String name);
    
    List<Level> findByOrderByOrderNumber();
    
    @Query("SELECT l FROM Level l WHERE l.name LIKE %:name%")
    List<Level> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT l FROM Level l " +
           "LEFT JOIN FETCH l.classGroups c " +
           "WHERE c.isActive = true")
    List<Level> findLevelsWithActiveClasses();
    
    @Query("SELECT l FROM Level l " +
           "LEFT JOIN FETCH l.teacherLevelAssignments tla " +
           "WHERE tla.term.id = :termId")
    List<Level> findLevelsWithTeacherAssignments(@Param("termId") UUID termId);
}