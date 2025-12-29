package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weekly_progress")
@Data
@EqualsAndHashCode(exclude = {"enrollment", "teacher"})
@ToString(exclude = {"enrollment", "teacher"})
public class WeeklyProgress {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_enrollment", nullable = false)
    private Enrollment enrollment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher", nullable = false)
    private User teacher;
    
    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;
    
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
    
    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;
    
    @Column(name = "recitation_score", precision = 5, scale = 2)
    private BigDecimal recitationScore;
    
    @Column(name = "recitation_grade", length = 5)
    private String recitationGrade;
    
    @Column(name = "memorization_progress", columnDefinition = "TEXT")
    private String memorizationProgress;
    
    @Column(name = "memorization_score", precision = 5, scale = 2)
    private BigDecimal memorizationScore;
    
    @Column(name = "tajweed_score", precision = 5, scale = 2)
    private BigDecimal tajweedScore;
    
    @Column(name = "tajweed_grade", length = 5)
    private String tajweedGrade;
    
    @Column(name = "participation_grade", length = 5)
    private String participationGrade;
    
    @Column(name = "attendance_count")
    private Integer attendanceCount;
    
    @Column(name = "sessions_attended")
    private Integer sessionsAttended;
    
    @Column(name = "sessions_total")
    private Integer sessionsTotal;
    
    @Column(name = "teacher_notes", columnDefinition = "TEXT")
    private String teacherNotes;
    
    @Column(name = "areas_of_improvement", columnDefinition = "TEXT")
    private String areasOfImprovement;
    
    @Column(name = "needs_support")
    private Boolean needsSupport = false;
    
    @Column(name = "support_reason", columnDefinition = "TEXT")
    private String supportReason;
    
    @Column(name = "parent_communication_notes", columnDefinition = "TEXT")
    private String parentCommunicationNotes;
    
    @Column(name = "created_at")
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
    
    public BigDecimal getAttendanceRate() {
        if (sessionsTotal == null || sessionsTotal == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(sessionsAttended)
            .divide(BigDecimal.valueOf(sessionsTotal), 2, BigDecimal.ROUND_HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
    
    public String getOverallGrade() {
        // Calculate overall grade based on all scores
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        
        if (recitationScore != null) {
            total = total.add(recitationScore);
            count++;
        }
        if (memorizationScore != null) {
            total = total.add(memorizationScore);
            count++;
        }
        if (tajweedScore != null) {
            total = total.add(tajweedScore);
            count++;
        }
        
        if (count == 0) return "N/A";
        
        BigDecimal average = total.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
        
        if (average.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (average.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (average.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (average.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }
}