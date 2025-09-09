package com.sahabatquran.webapp.functional.util;

import net.datafaker.Faker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Test data utility for Operation Workflow functional tests.
 * 
 * Provides realistic test data for daily semester operations including:
 * - Session execution data
 * - Feedback campaign data  
 * - Substitute teacher assignments
 * - Progress tracking data
 * - Monitoring and alert data
 */
public class OperationWorkflowTestDataUtil {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    // Test users from CLAUDE.md
    public static class TestUsers {
        public static final String SYSTEM_ADMIN_USERNAME = "sysadmin";
        public static final String SYSTEM_ADMIN_PASSWORD = "SysAdmin@YSQ2024";
        
        public static final String ACADEMIC_ADMIN_USERNAME = "academic.admin1";
        public static final String ACADEMIC_ADMIN_PASSWORD = "Welcome@YSQ2024";
        
        public static final String FINANCE_USERNAME = "staff.finance1";
        public static final String FINANCE_PASSWORD = "Welcome@YSQ2024";
        
        public static final String STUDENT_USERNAME = "siswa.ali";
        public static final String STUDENT_PASSWORD = "Welcome@YSQ2024";
        
        public static final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        public static final String INSTRUCTOR_PASSWORD = "Welcome@YSQ2024";
        
        public static final String MANAGEMENT_USERNAME = "management.director";
        public static final String MANAGEMENT_PASSWORD = "Welcome@YSQ2024";
    }
    
    // Session execution test data
    public static class SessionData {
        
        public static String generateCheckInLocation() {
            List<String> locations = Arrays.asList(
                "Ruang Kelas A1", "Ruang Kelas A2", "Ruang Kelas B1", 
                "Ruang Kelas B2", "Ruang Multimedia", "Aula Utama"
            );
            return locations.get(random.nextInt(locations.size()));
        }
        
        public static String generateSessionNotes() {
            List<String> activities = Arrays.asList(
                "Makharijul Huruf", "Tajweed", "Hafalan", "Tilawah", "Tahsin"
            );
            List<String> progressNotes = Arrays.asList(
                "siswa menunjukkan progress baik", "perlu lebih banyak latihan", 
                "sangat antusias dalam belajar", "mulai menguasai materi"
            );
            
            String activity = activities.get(random.nextInt(activities.size()));
            String progress = progressNotes.get(random.nextInt(progressNotes.size()));
            
            return String.format("Materi %s - %s", activity, progress);
        }
        
        public static int generateAttendanceCount(int totalStudents) {
            // Generate realistic attendance (80-95% typically)
            return Math.max(1, totalStudents - random.nextInt(3));
        }
        
        public static String getCurrentTime() {
            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        
        public static String getTomorrowDate() {
            return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        public static String getDayAfterTomorrowDate() {
            return LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
    
    // Feedback campaign test data
    public static class FeedbackData {
        
        public static String generatePositiveFeedback() {
            List<String> positiveComments = Arrays.asList(
                "Penjelasan tajweed sangat jelas dan sabar",
                "Cara mengajar sangat mudah dipahami",
                "Selalu memberikan motivasi kepada siswa",
                "Metode pembelajaran sangat menarik",
                "Sangat membantu dalam perbaikan bacaan"
            );
            return positiveComments.get(random.nextInt(positiveComments.size()));
        }
        
        public static String generateSuggestionFeedback() {
            List<String> suggestions = Arrays.asList(
                "Mungkin bisa lebih banyak contoh praktik",
                "Akan lebih baik jika ada latihan tambahan",
                "Semoga bisa ditambah waktu untuk tanya jawab",
                "Bisa ditambah materi audio untuk latihan di rumah",
                "Mungkin perlu lebih banyak variasi dalam pembelajaran"
            );
            return suggestions.get(random.nextInt(suggestions.size()));
        }
        
        public static String generateParentComment() {
            List<String> parentComments = Arrays.asList(
                "Thank you for informing us promptly. Please let us know how Ali adjusts to the new teacher.",
                "Terima kasih atas pemberitahuannya. Mohon kabar perkembangan anak kami.",
                "We appreciate the advance notice. Please keep us updated on any changes.",
                "Alhamdulillah, terima kasih sudah memberitahu sebelumnya."
            );
            return parentComments.get(random.nextInt(parentComments.size()));
        }
        
        public static int generateRating() {
            // Generate ratings with realistic distribution (mostly 3-5)
            int[] weights = {1, 2, 5, 8, 10}; // Weight for ratings 1-5
            int totalWeight = Arrays.stream(weights).sum();
            int randomValue = random.nextInt(totalWeight);
            
            int cumulativeWeight = 0;
            for (int i = 0; i < weights.length; i++) {
                cumulativeWeight += weights[i];
                if (randomValue < cumulativeWeight) {
                    return i + 1;
                }
            }
            return 4; // Default fallback
        }
        
        public static String generateAnonymousFeedbackToken() {
            return "parent-notification-token-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
    
    // Substitute teacher test data
    public static class SubstituteData {
        
        public static String generateSubstituteName() {
            List<String> substituteNames = Arrays.asList(
                "Ustadzah Siti", "Ustadz Abdullah", "Ustadzah Fatimah", 
                "Ustadz Ibrahim", "Ustadzah Khadijah", "Ustadz Yusuf"
            );
            return substituteNames.get(random.nextInt(substituteNames.size()));
        }
        
        public static String generateRescheduleReason() {
            List<String> reasons = Arrays.asList(
                "Teacher Illness", "Family Emergency", "Medical Appointment",
                "Traffic Emergency", "Weather Conditions"
            );
            return reasons.get(random.nextInt(reasons.size()));
        }
        
        public static String generateRescheduleNotes() {
            List<String> notes = Arrays.asList(
                "Sakit demam, tidak bisa mengajar besok",
                "Ada keperluan keluarga mendadak",
                "Harus ke rumah sakit untuk periksa",
                "Terjebak macet parah karena banjir",
                "Kondisi cuaca tidak memungkinkan"
            );
            return notes.get(random.nextInt(notes.size()));
        }
        
        public static String generateSubstituteInstructions() {
            List<String> instructions = Arrays.asList(
                "Lanjutkan materi Makharijul Huruf halaman 15-18",
                "Review hafalan Surah Al-Fatiha dengan siswa",
                "Fokus pada perbaikan tajweed untuk siswa yang masih lemah",
                "Lakukan evaluasi singkat untuk mengukur pemahaman",
                "Berikan latihan tambahan untuk siswa yang tertinggal"
            );
            return instructions.get(random.nextInt(instructions.size()));
        }
        
        public static String generateChangeDuration() {
            List<String> durations = Arrays.asList(
                "1 week", "2 weeks", "3 days", "1 session", "Until further notice"
            );
            return durations.get(random.nextInt(durations.size()));
        }
    }
    
    // Weekly progress test data
    public static class ProgressData {
        
        public static int generateProgressScore() {
            // Generate realistic scores with normal distribution around 75-85
            return Math.max(50, Math.min(100, (int) (faker.number().numberBetween(70, 90) + random.nextGaussian() * 10)));
        }
        
        public static String generateGrade(int score) {
            if (score >= 90) return "A";
            else if (score >= 85) return "A-";
            else if (score >= 80) return "B+";
            else if (score >= 75) return "B";
            else if (score >= 70) return "B-";
            else if (score >= 65) return "C+";
            else if (score >= 60) return "C";
            else return "D";
        }
        
        public static String generateMemorizationProgress() {
            List<String> progressTypes = Arrays.asList(
                "Completed Surah Al-Fatiha perfectly",
                "Working on Surah Al-Fatiha - 80% complete",
                "Mastering Surah An-Nas",
                "Reviewing previous surahs",
                "Starting new surah Al-Ikhlas"
            );
            return progressTypes.get(random.nextInt(progressTypes.size()));
        }
        
        public static String generateProgressNotes(String studentName) {
            if (studentName.equals("Omar")) {
                return "Struggling with pronunciation - needs extra practice";
            }
            
            List<String> positiveNotes = Arrays.asList(
                "Showing excellent progress in tajweed",
                "Very enthusiastic and participative",
                "Good improvement in recitation quality",
                "Consistent attendance and effort"
            );
            return positiveNotes.get(random.nextInt(positiveNotes.size()));
        }
        
        public static String generateClassSummary() {
            List<String> summaries = Arrays.asList(
                "Class menunjukkan progress baik dalam tajweed. Perlu lebih banyak latihan praktik untuk beberapa siswa.",
                "Siswa sangat antusias dalam pembelajaran hari ini. Hafalan mulai menunjukkan peningkatan.",
                "Fokus pada perbaikan makharaj huruf. Sebagian besar siswa sudah mulai menguasai.",
                "Pembelajaran berjalan lancar. Perlu perhatian khusus untuk beberapa siswa yang masih tertinggal."
            );
            return summaries.get(random.nextInt(summaries.size()));
        }
        
        public static String generateParentCommunicationNotes() {
            List<String> communications = Arrays.asList(
                "Will contact Omar's parents untuk discuss home practice plan",
                "Perlu komunikasi dengan orang tua Ali untuk mempertahankan progress",
                "Akan menghubungi orang tua Fatimah untuk apresiasi kemajuan",
                "Perlu follow up dengan keluarga untuk latihan di rumah"
            );
            return communications.get(random.nextInt(communications.size()));
        }
        
        public static List<String> generateStudentNames() {
            return Arrays.asList(
                "Ali", "Fatimah", "Omar", "Zainab", "Yusuf", 
                "Khadijah", "Ibrahim", "Aisha"
            );
        }
    }
    
    // Monitoring and alert test data
    public static class MonitoringData {
        
        public static String generateAlertType() {
            List<String> alertTypes = Arrays.asList(
                "Instructor Late", "Low Attendance", "Session Delay", 
                "System Performance", "Weather Alert"
            );
            return alertTypes.get(random.nextInt(alertTypes.size()));
        }
        
        public static String generateAlertMessage(String alertType) {
            Map<String, String> alertMessages = Map.of(
                "Instructor Late", "Ustadz Ahmad belum check-in untuk sesi pagi ini",
                "Low Attendance", "Kehadiran kelas Tahsin 1 di bawah 70%",
                "Session Delay", "Sesi kelas B2 tertunda 15 menit",
                "System Performance", "Database response time meningkat",
                "Weather Alert", "Cuaca buruk mungkin mempengaruhi kehadiran"
            );
            return alertMessages.getOrDefault(alertType, "System alert detected");
        }
        
        public static int generateAttendanceRate() {
            // Generate realistic attendance rates (60-95%)
            return faker.number().numberBetween(60, 95);
        }
        
        public static int generateCompletionRate() {
            // Generate realistic completion rates (75-98%)
            return faker.number().numberBetween(75, 98);
        }
        
        public static String generateSystemStatus() {
            List<String> statuses = Arrays.asList("Healthy", "Warning", "Critical");
            // Weighted towards healthy status
            int[] weights = {8, 2, 1};
            int randomValue = random.nextInt(11);
            if (randomValue < 8) return "Healthy";
            else if (randomValue < 10) return "Warning";
            else return "Critical";
        }
    }
    
    // Analytics test data
    public static class AnalyticsData {
        
        public static int generateTotalFeedbackCount() {
            return faker.number().numberBetween(150, 500);
        }
        
        public static int generateResponseRate() {
            return faker.number().numberBetween(75, 95);
        }
        
        public static String generateAverageRating() {
            double rating = 3.5 + (random.nextDouble() * 1.5); // 3.5 to 5.0 range
            return String.format("%.1f", rating);
        }
        
        public static String generateTopPerformingTeacher() {
            List<String> teachers = Arrays.asList(
                "Ustadz Ahmad", "Ustadzah Siti", "Ustadz Ibrahim", 
                "Ustadzah Fatimah", "Ustadz Abdullah"
            );
            return teachers.get(random.nextInt(teachers.size()));
        }
        
        public static String generateMostCommonTheme() {
            List<String> themes = Arrays.asList(
                "Teaching Quality", "Communication", "Punctuality", 
                "Student Engagement", "Learning Materials"
            );
            return themes.get(random.nextInt(themes.size()));
        }
        
        public static String generateBestPerformingCategory() {
            List<String> categories = Arrays.asList(
                "Teaching Quality", "Communication Skills", "Punctuality", 
                "Fairness", "Student Engagement"
            );
            return categories.get(random.nextInt(categories.size()));
        }
        
        public static String generateWorstPerformingCategory() {
            List<String> categories = Arrays.asList(
                "Time Management", "Technology Usage", "Homework Feedback", 
                "Parent Communication", "Assessment Methods"
            );
            return categories.get(random.nextInt(categories.size()));
        }
    }
    
    // Common utility methods
    public static class CommonData {
        
        public static String generateClassName() {
            List<String> classNames = Arrays.asList(
                "Tahsin 1 - Senin Pagi", "Tahsin 2 - Selasa Sore", 
                "Hafalan 1 - Rabu Pagi", "Tahfidz 1 - Kamis Sore",
                "Tilawah 1 - Jumat Pagi", "Qiroah 1 - Sabtu Pagi"
            );
            return classNames.get(random.nextInt(classNames.size()));
        }
        
        public static String generateSubject() {
            List<String> subjects = Arrays.asList(
                "Tahsin", "Tahfidz", "Tilawah", "Qiroah", "Tajweed"
            );
            return subjects.get(random.nextInt(subjects.size()));
        }
        
        public static String generateTimeSlot() {
            List<String> timeSlots = Arrays.asList(
                "08:00-09:30", "10:00-11:30", "13:00-14:30", 
                "15:00-16:30", "19:00-20:30"
            );
            return timeSlots.get(random.nextInt(timeSlots.size()));
        }
        
        public static String generateSessionId() {
            return "SES-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                   "-" + String.format("%03d", random.nextInt(999));
        }
        
        public static String generateRequestId() {
            return "REQ-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        public static String formatCurrentDateTime() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        public static String formatDate(LocalDate date) {
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        public static String formatTime(LocalTime time) {
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
    
    // Data validation helpers
    public static class ValidationHelpers {
        
        public static boolean isValidEmail(String email) {
            return email != null && email.contains("@") && email.contains(".");
        }
        
        public static boolean isValidPhoneNumber(String phone) {
            return phone != null && phone.matches("^[0-9+\\-\\s()]{10,15}$");
        }
        
        public static boolean isValidRating(int rating) {
            return rating >= 1 && rating <= 5;
        }
        
        public static boolean isValidScore(int score) {
            return score >= 0 && score <= 100;
        }
        
        public static boolean isValidAttendanceRate(int rate) {
            return rate >= 0 && rate <= 100;
        }
    }
}