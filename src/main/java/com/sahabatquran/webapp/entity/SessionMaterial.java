package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_materials")
@Data
@EqualsAndHashCode(exclude = {"session", "uploadedBy", "uploadDate"})
@ToString(exclude = {"session", "uploadedBy"})
public class SessionMaterial {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false)
    private ClassSession session;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 50)
    private MaterialType materialType;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Column(name = "material_title", nullable = false, length = 200)
    private String materialTitle;
    
    @Column(name = "material_description", columnDefinition = "TEXT")
    private String materialDescription;
    
    @Column(name = "file_size")
    private Long fileSize; // in bytes
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @Column(name = "is_shared_with_students")
    private Boolean isSharedWithStudents = false;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    
    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
    
    public enum MaterialType {
        AUDIO, TEXT, VIDEO, WORKSHEET, PRESENTATION, OTHER
    }
}