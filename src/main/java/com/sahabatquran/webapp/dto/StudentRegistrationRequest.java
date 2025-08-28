package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentRegistration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class StudentRegistrationRequest {
    
    // Personal Information
    @NotBlank(message = "Nama lengkap harus diisi")
    @Size(max = 150, message = "Nama lengkap maksimal 150 karakter")
    private String fullName;
    
    @NotNull(message = "Jenis kelamin harus dipilih")
    private StudentRegistration.Gender gender;
    
    @NotNull(message = "Tanggal lahir harus diisi")
    @Past(message = "Tanggal lahir harus di masa lalu")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Tempat lahir harus diisi")
    @Size(max = 100, message = "Tempat lahir maksimal 100 karakter")
    private String placeOfBirth;
    
    @NotBlank(message = "Nomor telepon harus diisi")
    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Format nomor telepon tidak valid")
    private String phoneNumber;
    
    @NotBlank(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    private String email;
    
    @NotBlank(message = "Alamat harus diisi")
    @Size(max = 500, message = "Alamat maksimal 500 karakter")
    private String address;
    
    @NotBlank(message = "Nama kontak darurat harus diisi")
    @Size(max = 150, message = "Nama kontak darurat maksimal 150 karakter")
    private String emergencyContactName;
    
    @NotBlank(message = "Nomor telepon kontak darurat harus diisi")
    @Size(max = 20, message = "Nomor telepon kontak darurat maksimal 20 karakter")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Format nomor telepon kontak darurat tidak valid")
    private String emergencyContactPhone;
    
    @NotBlank(message = "Hubungan kontak darurat harus diisi")
    @Size(max = 50, message = "Hubungan kontak darurat maksimal 50 karakter")
    private String emergencyContactRelation;
    
    // Educational Information
    @NotBlank(message = "Tingkat pendidikan harus diisi")
    @Size(max = 50, message = "Tingkat pendidikan maksimal 50 karakter")
    private String educationLevel;
    
    @Size(max = 150, message = "Nama sekolah maksimal 150 karakter")
    private String schoolName;
    
    @Size(max = 1000, message = "Pengalaman membaca Al-Quran maksimal 1000 karakter")
    private String quranReadingExperience;
    
    @NotNull(message = "Pengalaman tahsin sebelumnya harus dipilih")
    private Boolean previousTahsinExperience = false;
    
    @Size(max = 1000, message = "Detail pengalaman tahsin maksimal 1000 karakter")
    private String previousTahsinDetails;
    
    // Program Selection
    @NotNull(message = "Program harus dipilih")
    private UUID programId;
    
    @Size(max = 1000, message = "Alasan pendaftaran maksimal 1000 karakter")
    private String registrationReason;
    
    @Size(max = 1000, message = "Tujuan pembelajaran maksimal 1000 karakter")
    private String learningGoals;
    
    // Schedule Preferences
    @NotEmpty(message = "Minimal satu preferensi jadwal harus dipilih")
    @Size(max = 3, message = "Maksimal 3 preferensi jadwal")
    @Valid
    private List<SessionPreferenceRequest> sessionPreferences;
    
    // Placement Test
    private String recordingDriveLink;
    
    @Data
    public static class SessionPreferenceRequest {
        
        @NotNull(message = "Sesi harus dipilih")
        private UUID sessionId;
        
        @NotNull(message = "Prioritas harus diisi")
        @Min(value = 1, message = "Prioritas minimal 1")
        @Max(value = 3, message = "Prioritas maksimal 3")
        private Integer priority;
        
        @NotEmpty(message = "Hari harus dipilih")
        @Size(max = 7, message = "Maksimal 7 hari")
        private List<DayOfWeek> preferredDays;
    }
    
    public enum DayOfWeek {
        MONDAY("Senin"),
        TUESDAY("Selasa"),
        WEDNESDAY("Rabu"),
        THURSDAY("Kamis"),
        FRIDAY("Jumat"),
        SATURDAY("Sabtu"),
        SUNDAY("Minggu");
        
        private final String displayName;
        
        DayOfWeek(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}