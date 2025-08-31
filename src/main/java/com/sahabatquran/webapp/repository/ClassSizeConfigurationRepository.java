package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassSizeConfiguration;
import com.sahabatquran.webapp.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSizeConfigurationRepository extends JpaRepository<ClassSizeConfiguration, UUID> {
    
    Optional<ClassSizeConfiguration> findByConfigKey(String configKey);
    
    List<ClassSizeConfiguration> findByLevel(Level level);
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc WHERE csc.level IS NULL")
    List<ClassSizeConfiguration> findSystemDefaults();
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc WHERE csc.level IS NOT NULL")
    List<ClassSizeConfiguration> findLevelSpecific();
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc " +
           "WHERE csc.configKey LIKE %:keyPattern%")
    List<ClassSizeConfiguration> findByConfigKeyContaining(@Param("keyPattern") String keyPattern);
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc " +
           "WHERE csc.level.id = :levelId AND csc.configKey LIKE %:keyPattern%")
    List<ClassSizeConfiguration> findByLevelAndConfigKeyContaining(
            @Param("levelId") UUID levelId, 
            @Param("keyPattern") String keyPattern);
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc " +
           "WHERE csc.configKey IN ('default.min', 'default.max')")
    List<ClassSizeConfiguration> findDefaultMinMax();
    
    @Query("SELECT csc FROM ClassSizeConfiguration csc " +
           "WHERE csc.level.id = :levelId AND csc.configKey LIKE CONCAT(:levelPrefix, '%')")
    List<ClassSizeConfiguration> findByLevelPrefix(
            @Param("levelId") UUID levelId, 
            @Param("levelPrefix") String levelPrefix);
}