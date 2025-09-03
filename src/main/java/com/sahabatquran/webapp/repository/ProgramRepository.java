package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    
    @Query("SELECT p FROM Program p WHERE p.isActive = true ORDER BY p.level.orderNumber ASC")
    List<Program> findByIsActiveTrue();
    
    Optional<Program> findByCodeAndIsActiveTrue(String code);
    
    @Query("SELECT p FROM Program p WHERE p.isActive = true AND p.level.orderNumber <= :maxLevel ORDER BY p.level.orderNumber")
    List<Program> findActiveByMaxLevelOrder(Integer maxLevel);
    
    boolean existsByCodeAndIsActiveTrue(String code);
}