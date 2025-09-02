package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentRegistration;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class StudentRegistrationResponse {
    
    private UUID id;
    
    // Personal Information
    private String fullName;
    private StudentRegistration.Gender gender;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String phoneNumber;
    private String email;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    
    // Educational Information
    private String educationLevel;
    private String schoolName;
    private String quranReadingExperience;
    private Boolean previousTahsinExperience;
    private String previousTahsinDetails;
    
    // Program Selection
    private ProgramInfo program;
    private String registrationReason;
    private String learningGoals;
    
    // Schedule Preferences
    private List<SessionPreferenceInfo> sessionPreferences;
    
    // Placement Test Information
    private PlacementTestInfo placementTest;
    
    // Registration Status
    private StudentRegistration.RegistrationStatus registrationStatus;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewedByName;
    
    // Teacher Assignment Fields
    private UUID assignedTeacherId;
    private String assignedTeacherName;
    private LocalDateTime assignedAt;
    private UUID assignedById;
    private String assignedByName;
    private StudentRegistration.TeacherReviewStatus teacherReviewStatus;
    private String teacherRemarks;
    private ProgramInfo recommendedLevel;
    private LocalDateTime teacherEvaluatedAt;
    private String reviewNotes;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class ProgramInfo {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private Integer levelOrder;
    }
    
    @Data
    public static class SessionPreferenceInfo {
        private UUID id;
        private SessionInfo session;
        private Integer priority;
        private List<String> preferredDays;
    }
    
    @Data
    public static class SessionInfo {
        private UUID id;
        private String code;
        private String name;
        private String startTime;
        private String endTime;
    }
    
    @Data
    public static class PlacementTestInfo {
        private UUID verseId;
        private String surahName;
        private Integer surahNumber;
        private Integer ayahStart;
        private Integer ayahEnd;
        private String arabicText;
        private Integer difficultyLevel;
        private String recordingDriveLink;
        private StudentRegistration.PlacementTestStatus status;
        private Integer result;
        private String notes;
        private String evaluatedByName;
        private LocalDateTime evaluatedAt;
    }
}