package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "placement_test_verses")
@Data
public class PlacementTestVerse {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "surah_number", nullable = false)
    private Integer surahNumber;
    
    @Column(name = "surah_name", nullable = false, length = 50)
    private String surahName;
    
    @Column(name = "ayah_start", nullable = false)
    private Integer ayahStart;
    
    @Column(name = "ayah_end", nullable = false)
    private Integer ayahEnd;
    
    @Column(name = "arabic_text", nullable = false, columnDefinition = "TEXT")
    private String arabicText;
    
    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}