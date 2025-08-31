package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "class_preparation_checklist")
@Data
@EqualsAndHashCode(exclude = {"session", "completedBy"})
@ToString(exclude = {"session", "completedBy"})
public class ClassPreparationChecklist {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false)
    private ClassSession session;
    
    @Column(name = "checklist_item", nullable = false, length = 200)
    private String checklistItem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "item_category", length = 50)
    private ItemCategory itemCategory;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private User completedBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    public enum ItemCategory {
        PLANNING, MATERIALS, ASSESSMENT, SETUP
    }
}