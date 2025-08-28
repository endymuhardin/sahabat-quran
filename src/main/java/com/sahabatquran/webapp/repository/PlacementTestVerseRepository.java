package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.PlacementTestVerse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlacementTestVerseRepository extends JpaRepository<PlacementTestVerse, UUID> {
    
    List<PlacementTestVerse> findByIsActiveTrueOrderByDifficultyLevelAscSurahNumberAscAyahStartAsc();
    
    List<PlacementTestVerse> findByDifficultyLevelAndIsActiveTrueOrderBySurahNumberAscAyahStartAsc(Integer difficultyLevel);
    
    @Query("SELECT p FROM PlacementTestVerse p WHERE p.isActive = true AND p.difficultyLevel BETWEEN :minLevel AND :maxLevel ORDER BY p.difficultyLevel, p.surahNumber, p.ayahStart")
    List<PlacementTestVerse> findByDifficultyLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);
    
    @Query(value = "SELECT * FROM placement_test_verses WHERE is_active = true AND difficulty_level = :level ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<PlacementTestVerse> findRandomByDifficultyLevel(@Param("level") Integer difficultyLevel);
    
    @Query(value = "SELECT * FROM placement_test_verses WHERE is_active = true ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<PlacementTestVerse> findRandomVerse();
    
    List<PlacementTestVerse> findBySurahNumberAndIsActiveTrueOrderByAyahStartAsc(Integer surahNumber);
    
    @Query("SELECT DISTINCT p.difficultyLevel FROM PlacementTestVerse p WHERE p.isActive = true ORDER BY p.difficultyLevel")
    List<Integer> findDistinctDifficultyLevels();
}