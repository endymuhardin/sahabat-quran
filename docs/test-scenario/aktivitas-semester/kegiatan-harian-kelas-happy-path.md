# Skenario Pengujian: Kegiatan Harian Kelas - Happy Path

## Informasi Umum
- **Kategori**: Aktivitas Semester Harian
- **Modul**: Daily Class Session Management
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 8 skenario utama (Instructor, Student, Academic Admin)

---

## AKH-HP-001: Instructor - Session Check-in dan Pelaksanaan Kelas

### Informasi Skenario
- **ID Skenario**: AKH-HP-001 (Aktivitas Kelas Harian - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-20 menit
- **Playwright Test**: `SessionExecutionTest.instructorSessionExecution()`

### Prasyarat
- Instructor account tersedia: `ustadz.ahmad` / `Welcome@YSQ2024`
- Class session sudah dijadwalkan untuk hari ini
- Preparation status = 'READY'
- Student roster tersedia dengan minimal 5 siswa

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Session Data:
Class Group: Tahsin 1 - Senin Pagi
Session Date: Today
Scheduled Time: 08:00-10:00
Total Students: 8 siswa
Session Number: 5 (Week 5)
Preparation Status: READY
```

### Langkah Pengujian

#### Bagian 1: Teacher Check-in
1. **Login sebagai Instructor**
   - Aksi: Login dengan credentials instructor
   - Verifikasi: 
     - Dashboard instructor tampil dengan benar
     - Menu "My Classes" tersedia
     - Welcome message menampilkan nama ustadz

2. **Navigate ke My Classes**
   - Aksi: Klik menu "My Classes" atau "Kelas Saya"
   - Verifikasi:
     - List class assignments tampil
     - Today's session terlihat dengan status "READY"
     - Tombol "Check-in" tersedia dan enabled

3. **Teacher Self Check-in**
   - Aksi: Klik tombol "Check-in" untuk session hari ini
   - Verifikasi:
     - Modal check-in muncul dengan informasi session
     - Arrival time otomatis terisi dengan waktu sekarang
     - Field "Location Confirmation" tersedia
     - Tombol "Confirm Check-in" aktif

4. **Confirm Check-in**
   - Aksi: Konfirm check-in dengan klik "Confirm Check-in"
   - Verifikasi:
     - Success message "Check-in berhasil" tampil
     - Session status berubah ke "IN_PROGRESS"
     - Timer session mulai berjalan
     - Tombol "Start Session" muncul

#### Bagian 2: Session Execution
5. **Start Class Session**
   - Aksi: Klik tombol "Start Session"
   - Verifikasi:
     - Session dashboard terbuka
     - Student attendance interface tampil
     - List siswa dengan status "Not Marked" tampil
     - Session objectives dan materials terlihat

6. **Mark Student Attendance**
   - Aksi: Mark attendance untuk setiap siswa (6 hadir, 2 izin)
   - Verifikasi:
     - Attendance status berubah per siswa
     - Real-time attendance counter update (6/8 hadir)
     - Tombol untuk add notes tersedia per siswa
     - Save attendance otomatis aktif

7. **Add Session Notes**
   - Aksi: Tambahkan session notes di field "Session Summary"
   - Data: "Materi Makharijul Huruf - siswa menunjukkan progress baik"
   - Verifikasi:
     - Text tersimpan secara otomatis (auto-save)
     - Character counter tampil
     - Format text options tersedia

8. **Record Learning Objectives Achievement**
   - Aksi: Mark learning objectives yang tercapai
   - Verifikasi:
     - Checklist objectives dapat di-toggle
     - Progress indicator menunjukkan % completion
     - Additional notes field untuk objectives

#### Bagian 3: Session Completion
9. **End Session dan Check-out**
   - Aksi: Klik tombol "End Session"
   - Verifikasi:
     - End session modal muncul
     - Session summary review tampil
     - Attendance summary tampil (6/8 hadir)
     - Departure time otomatis terisi

10. **Final Session Submission**
    - Aksi: Klik "Submit Session" untuk complete
    - Verifikasi:
      - Session status berubah ke "COMPLETED"
      - Success message tampil
      - Redirect ke My Classes dashboard
      - Session card menampilkan "Completed" status

### Hasil Diharapkan
- Teacher berhasil check-in dan check-out
- Session executed dengan attendance tercatat
- Session summary dan objectives recorded
- Session status = "COMPLETED"
- Teacher attendance record tersimpan di database

### Kriteria Sukses
- ✅ Check-in process lancar tanpa error
- ✅ Attendance marking berfungsi untuk semua siswa  
- ✅ Session notes dan objectives tersimpan
- ✅ Check-out process completed successfully
- ✅ Database records accurate (teacher_attendance, session_data)

---

## AKH-HP-002: Student - Submit Anonymous Feedback

### Informasi Skenario
- **ID Skenario**: AKH-HP-002 (Aktivitas Kelas Harian - Happy Path - 002)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentFeedbackTest.anonymousFeedbackSubmission()`

### Prasyarat
- Student account tersedia: `siswa.ali` / `Welcome@YSQ2024`
- Student enrolled in active class
- Feedback campaign active untuk student tersebut
- Template "Teacher Evaluation" tersedia

### Data Test
```
Student Login:
Username: siswa.ali
Password: Welcome@YSQ2024

Feedback Campaign:
Type: Teacher Evaluation
Target: Ustadz Ahmad - Tahsin 1 Class
Duration: 7 days remaining
Questions: 12 questions total
Anonymity: Full anonymity maintained
```

### Langkah Pengujian

#### Bagian 1: Access Feedback System
1. **Student Login**
   - Aksi: Login sebagai student
   - Verifikasi:
     - Student dashboard tampil
     - Notification badge untuk feedback tampil
     - Menu "Feedback" tersedia

2. **View Available Feedback**
   - Aksi: Klik menu "Feedback" atau notification
   - Verifikasi:
     - Active feedback campaigns tampil
     - "Teacher Evaluation" campaign terlihat
     - Anonymous badge/indicator tampil
     - "Start Feedback" button tersedia

3. **Start Feedback Session**
   - Aksi: Klik "Start Feedback" untuk teacher evaluation
   - Verifikasi:
     - Feedback form terbuka
     - Anonymity notice tampil jelas
     - Progress indicator (1/12) tampil
     - Question categories terlihat

#### Bagian 2: Complete Feedback Questions
4. **Answer Teaching Quality Questions (Q1-3)**
   - Aksi: Jawab pertanyaan kualitas pengajaran
   - Data: 
     - Q1: "Bagaimana kualitas pengajaran ustadz?" → Rating 4/5
     - Q2: "Apakah materi disampaikan dengan jelas?" → Rating 5/5
     - Q3: "Contoh mudah dipahami?" → Yes
   - Verifikasi:
     - Rating stars/scale berfungsi
     - Progress bar update (3/12)
     - Auto-save indicator active

5. **Answer Communication Questions (Q4-6)**
   - Aksi: Jawab pertanyaan komunikasi
   - Data:
     - Q4: "Kemampuan menjawab pertanyaan?" → Rating 4/5
     - Q5: "Bersikap sabar dan ramah?" → Rating 5/5
     - Q6: "Mudah berkomunikasi?" → Rating 4/5
   - Verifikasi:
     - Consistent rating interface
     - Progress update (6/12)

6. **Answer Punctuality Questions (Q7-8)**
   - Aksi: Jawab pertanyaan ketepatan waktu
   - Data:
     - Q7: "Hadir tepat waktu?" → Rating 4/5
     - Q8: "Siap dengan materi?" → Rating 5/5
   - Verifikasi: Progress update (8/12)

7. **Answer Fairness Questions (Q9-10)**
   - Aksi: Jawab pertanyaan keadilan
   - Data:
     - Q9: "Berlaku adil kepada semua siswa?" → Rating 5/5
     - Q10: "Suasana kelas kondusif?" → Rating 4/5
   - Verifikasi: Progress update (10/12)

#### Bagian 3: Complete Open-ended Questions
8. **Provide Constructive Feedback (Q11-12)**
   - Aksi: Isi open-ended questions
   - Data:
     - Q11: "Yang paling disukai?" → "Penjelasan tajwid sangat jelas dan sabar"
     - Q12: "Saran untuk perbaikan?" → "Mungkin bisa lebih banyak contoh praktik"
   - Verifikasi:
     - Text area berfungsi dengan baik
     - Character limit indicator tampil
     - Progress bar completion (12/12)

#### Bagian 4: Submit Feedback
9. **Review Feedback Before Submit**
   - Aksi: Review answers pada summary page
   - Verifikasi:
     - All answers displayed correctly
     - Anonymous submission reminder tampil
     - "Submit Feedback" button enabled
     - "Edit Answers" option tersedia

10. **Final Submission**
    - Aksi: Klik "Submit Feedback"
    - Verifikasi:
      - Confirmation modal tampil
      - "Feedback submitted anonymously" message
      - Thank you message tampil
      - Redirect ke dashboard
      - Campaign status berubah ke "Completed"

### Hasil Diharapkan
- Student berhasil submit feedback secara anonymous
- Semua 12 questions terjawab dengan complete
- Feedback data tersimpan dengan anonymous token
- Student tidak dapat re-submit untuk campaign sama

### Kriteria Sukses
- ✅ Anonymous feedback process lancar
- ✅ All question types berfungsi (rating, yes/no, text)
- ✅ Progress tracking accurate
- ✅ Data submitted dengan anonymity maintained
- ✅ UI feedback clear dan user-friendly

---

## AKH-HP-003: Academic Admin - Monitor Real-time Class Activities

### Informasi Skenario
- **ID Skenario**: AKH-HP-003 (Aktivitas Kelas Harian - Happy Path - 003)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `SessionMonitoringTest.adminRealTimeMonitoring()`

### Prasyarat
- Admin staff account tersedia: `academic.admin1` / `Welcome@YSQ2024`
- Multiple active sessions running
- Teacher attendance data available
- Student feedback campaigns active

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Real-time Data Expected:
Active Sessions: 5 sessions ongoing
Teacher Check-ins: 4/5 teachers checked in
Student Attendance: Average 85% across all classes
Pending Feedback: 3 campaigns with responses
System Alerts: Any issues to monitor
```

### Langkah Pengujian

#### Bagian 1: Access Real-time Dashboard
1. **Academic Admin Login**
   - Aksi: Login sebagai academic admin
   - Verifikasi:
     - Admin dashboard with real-time widgets
     - "Live Session Monitor" card tersedia
     - Quick stats tampil (active sessions, attendance)

2. **Open Session Monitoring Dashboard**
   - Aksi: Navigate ke "Session Monitoring" atau "Live Classes"
   - Verifikasi:
     - Real-time session grid tampil
     - Session status indicators (IN_PROGRESS, READY, COMPLETED)
     - Teacher check-in status visible
     - Auto-refresh indicator active

#### Bagian 2: Monitor Active Sessions
3. **View Session Details**
   - Aksi: Click pada active session untuk detail
   - Verifikasi:
     - Session info popup atau panel terbuka
     - Teacher attendance status (checked-in time)
     - Student attendance live count
     - Session progress indicators

4. **Monitor Attendance Patterns**
   - Aksi: Review attendance summary across sessions
   - Verifikasi:
     - Attendance rates per class visible
     - Real-time updates as teachers mark attendance
     - Alert indicators untuk low attendance

5. **Check System Alerts**
   - Aksi: Review alert panel atau notifications
   - Verifikasi:
     - Late teacher check-ins flagged
     - Sessions not started alerts
     - Any technical issues highlighted

#### Bagian 3: Review Feedback Analytics
6. **Access Feedback Dashboard**
   - Aksi: Navigate ke feedback analytics section
   - Verifikasi:
     - Active campaigns dengan response rates
     - Real-time feedback statistics
     - Recent feedback summary/highlights

7. **Generate Quick Reports**
   - Aksi: Generate today's activity report
   - Verifikasi:
     - Report generation successful
     - PDF/Excel download options
     - Email distribution options

### Hasil Diharapkan
- Real-time monitoring dashboard functional
- Accurate session dan attendance data
- Effective alert system untuk issues
- Quick reporting capabilities available

### Kriteria Sukses
- ✅ Real-time data updates correctly
- ✅ Session monitoring comprehensive
- ✅ Alert system responsive to issues
- ✅ Reporting functionality works
- ✅ UI responsive and informative

---

## AKH-HP-004: Instructor - Handle Session Reschedule Request

### Informasi Skenario
- **ID Skenario**: AKH-HP-004 (Aktivitas Kelas Harian - Happy Path - 004)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `SessionRescheduleTest.instructorRescheduleRequest()`

### Prasyarat
- Instructor account tersedia: `ustadz.ahmad` / `Welcome@YSQ2024`
- Scheduled session for tomorrow exists
- Reschedule policies configured
- Student notification system active

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Session to Reschedule:
Class: Tahsin 1 - Selasa Pagi  
Original Date: Tomorrow (Selasa)
Original Time: 08:00-10:00
Students: 8 enrolled students
Reason: Teacher illness (auto-approved category)
Proposed New Date: Day after tomorrow (Rabu)
Proposed New Time: 08:00-10:00
```

### Langkah Pengujian

#### Bagian 1: Request Session Reschedule
1. **Access My Classes**
   - Aksi: Login dan navigate ke "My Classes"
   - Verifikasi:
     - Upcoming sessions tampil
     - Tomorrow's session visible dengan status "SCHEDULED"
     - "Reschedule" option tersedia per session

2. **Initiate Reschedule Request**
   - Aksi: Klik "Reschedule" untuk tomorrow's session
   - Verifikasi:
     - Reschedule modal terbuka
     - Session details tampil correctly
     - Reason dropdown dengan predefined options
     - Date/time picker tersedia

3. **Select Reschedule Reason**
   - Aksi: Pilih reason "Teacher Illness" dari dropdown
   - Verifikasi:
     - Reason selected
     - Auto-approval indicator tampil (if applicable)
     - Additional notes field available

4. **Choose New Date and Time**
   - Aksi: Set new date (day after tomorrow) dan time (08:00)
   - Verifikasi:
     - Date picker berfungsi
     - Time slot availability check
     - Conflict detection (if any)
     - Student impact assessment tampil

#### Bagian 2: Submit Reschedule Request
5. **Add Detailed Notes**
   - Aksi: Tambah notes "Sakit demam, tidak bisa mengajar besok"
   - Verifikasi:
     - Text field accepts input
     - Character limit indicator
     - Optional field clearly marked

6. **Review Impact Assessment**
   - Aksi: Review impact summary
   - Verifikasi:
     - Affected students count (8 students)
     - Parent notification requirement
     - Auto-approval status (for illness)

7. **Submit Reschedule Request**
   - Aksi: Klik "Submit Reschedule Request"
   - Verifikasi:
     - Request submitted successfully
     - Confirmation message tampil
     - Request ID generated
     - Status changed to "RESCHEDULE_PENDING" atau "APPROVED"

#### Bagian 3: Confirm Notifications Sent
8. **Check Request Status**
   - Aksi: Refresh My Classes dan check status
   - Verifikasi:
     - Original session status = "RESCHEDULED"
     - New session created with "SCHEDULED" status
     - Reschedule log entry visible

9. **Verify Student Notifications**
   - Aksi: Check notifications sent indicator
   - Verifikasi:
     - "Students Notified" status = true
     - Notification timestamp recorded
     - Parent notification status available

### Hasil Diharapkan
- Reschedule request processed successfully
- Original session cancelled, new session created
- Students dan parents notified automatically
- Reschedule reason dan impact documented

### Kriteria Sukses
- ✅ Reschedule workflow intuitive dan efficient
- ✅ Auto-approval working untuk valid reasons
- ✅ Session created correctly dengan new date/time
- ✅ Notification system triggers properly
- ✅ Audit trail maintained for reschedule

---

## AKH-HP-005: Academic Admin - Assign Substitute Teacher

### Informasi Skenario
- **ID Skenario**: AKH-HP-005 (Aktivitas Kelas Harian - Happy Path - 005)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `SubstituteAssignmentTest.emergencySubstituteAssignment()`

### Prasyarat
- Admin staff account: `academic.admin1` / `Welcome@YSQ2024`
- Teacher called in sick for today's session
- Substitute teachers available in pool
- Session needs immediate coverage

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Emergency Situation:
Original Teacher: Ustadz Ahmad (called in sick)
Session: Tahsin 1 - Hari ini 10:00-12:00
Students: 8 siswa enrolled
Notice Period: 2 hours before session
Available Substitutes: 3 qualified teachers
Substitute Selected: Ustadzah Siti (Tahsin specialist)
Assignment Type: EMERGENCY
```

### Langkah Pengujian

#### Bagian 1: Handle Emergency Request
1. **Access Emergency Dashboard**
   - Aksi: Login dan check emergency alerts/dashboard
   - Verifikasi:
     - Alert notification for sick teacher
     - Session requiring immediate attention highlighted
     - "Find Substitute" action button available

2. **View Session Details**
   - Aksi: Click pada session yang membutuhkan substitute
   - Verifikasi:
     - Session info lengkap (time, students, level)
     - Original teacher info
     - Urgency indicator (2 hours remaining)
     - "Assign Substitute" button enabled

#### Bagian 2: Find Available Substitute
3. **Open Substitute Teacher Pool**
   - Aksi: Klik "Assign Substitute"
   - Verifikasi:
     - Substitute pool interface terbuka
     - Available teachers filtered by qualifications
     - Compatibility indicators (level expertise)
     - Availability status real-time

4. **Filter Suitable Candidates**
   - Aksi: Filter by "Tahsin 1" specialization
   - Verifikasi:
     - Filtered results show qualified teachers
     - Rating dan experience visible
     - Last assignment date shown
     - Emergency availability indicator

5. **Select Substitute Teacher**
   - Aksi: Select "Ustadzah Siti" dari available options
   - Verifikasi:
     - Teacher profile details
     - Qualifications summary
     - Availability confirmation for today
     - Contact information accessible

#### Bagian 3: Complete Assignment Process
6. **Configure Assignment Details**
   - Aksi: Set assignment type dan compensation
   - Data: Type = "EMERGENCY", Rate = Standard hourly rate
   - Verifikasi:
     - Assignment type options available
     - Compensation calculation automatic
     - Special instructions field available

7. **Provide Session Materials**
   - Aksi: Attach lesson plan dan materials for substitute
   - Verifikasi:
     - File upload functionality
     - Material sharing options
     - Quick notes untuk substitute teacher

8. **Send Assignment Notification**
   - Aksi: Klik "Assign and Notify Substitute"
   - Verifikasi:
     - Assignment confirmed
     - SMS/notification sent ke substitute
     - Assignment status updated
     - Timeline shows steps completed

#### Bagian 4: Confirm Assignment Accepted
9. **Monitor Assignment Status**
   - Aksi: Wait for substitute confirmation (simulated)
   - Verifikasi:
     - Status updates in real-time
     - "Accepted by Substitute" status received
     - Session assignment transferred successfully

10. **Notify Students and Parents**
    - Aksi: Send notification about teacher change
    - Verifikasi:
      - Student/parent notification composed
      - Teacher introduction included
      - Session details confirmed unchanged
      - Notification delivery confirmed

### Hasil Diharapkan
- Emergency substitute assignment completed within time limit
- Qualified substitute teacher assigned dan confirmed
- Students/parents notified about teacher change
- Session coverage secured dengan minimal disruption

### Kriteria Sukses
- ✅ Emergency response time under 30 minutes
- ✅ Qualified substitute found dan assigned
- ✅ Assignment acceptance confirmed
- ✅ Stakeholder notifications sent successfully
- ✅ Session continuity maintained

---

## AKH-HP-006: Teacher - Weekly Progress Recording

### Informasi Skenario
- **ID Skenario**: AKH-HP-006 (Aktivitas Kelas Harian - Happy Path - 006)
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-18 menit
- **Playwright Test**: `WeeklyProgressTest.teacherProgressRecording()`

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Class dengan 8 enrolled students
- Week 5 of semester (multiple sessions completed)
- Student assessment data dari previous sessions

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Class Progress Data:
Class: Tahsin 1 - Senin Pagi
Week Number: 5
Total Students: 8
Sessions This Week: 2 completed
Assessment Period: Weekly (Week 5)
Progress Categories: Recitation, Memorization, Tajweed, Participation
```

### Langkah Pengujian

#### Bagian 1: Access Weekly Progress Interface
1. **Navigate to Progress Recording**
   - Aksi: Go to "My Classes" → Select class → "Weekly Progress"
   - Verifikasi:
     - Progress recording interface opens
     - Week 5 selected by default
     - Student list tampil (8 students)
     - Progress categories visible

2. **Review Session Summary**
   - Aksi: Check completed sessions for the week
   - Verifikasi:
     - 2 sessions completed this week
     - Session dates dan attendance visible
     - Learning objectives achieved summary

#### Bagian 2: Record Individual Student Progress
3. **Student 1 - Ali (High Performer)**
   - Aksi: Record progress untuk Ali
   - Data:
     - Recitation Score: 85/100 (B+)
     - Memorization Progress: "Completed Surah Al-Fatiha perfectly"
     - Tajweed Score: 90/100 (A-)
     - Participation Grade: A (very active)
   - Verifikasi:
     - Score inputs accept values correctly
     - Grade dropdowns work
     - Text areas for detailed notes

4. **Student 2 - Fatima (Good Progress)**
   - Aksi: Record progress untuk Fatima
   - Data:
     - Recitation Score: 78/100 (B)
     - Memorization Progress: "Working on Surah Al-Fatiha - 80% complete"
     - Tajweed Score: 75/100 (B)
     - Participation Grade: B+ (good participation)
   - Verifikasi: Consistent input interface

5. **Student 3 - Omar (Needs Support)**
   - Aksi: Record progress untuk Omar
   - Data:
     - Recitation Score: 65/100 (C+)
     - Memorization Progress: "Struggling with pronunciation - needs extra practice"
     - Tajweed Score: 60/100 (C)
     - Participation Grade: C (quiet, needs encouragement)
   - Verifikasi: Lower scores accepted, notes field for concerns

#### Bagian 3: Add Teacher Observations
6. **Weekly Class Summary**
   - Aksi: Add overall class performance summary
   - Data: "Class menunjukkan progress baik dalam tajweed. Perlu lebih banyak latihan praktik untuk beberapa siswa."
   - Verifikasi:
     - Summary field accepts longer text
     - Auto-save functionality working

7. **Identify Students Needing Extra Support**
   - Aksi: Flag students requiring additional attention
   - Data: Mark Omar untuk extra support
   - Verifikasi:
     - Support flag functionality
     - Reason for support field available

8. **Parent Communication Notes**
   - Aksi: Add notes untuk parent communication
   - Data: "Will contact Omar's parents untuk discuss home practice plan"
   - Verifikasi:
     - Parent communication tracking
     - Follow-up reminder options

#### Bagian 4: Submit Weekly Progress
9. **Review Progress Summary**
   - Aksi: Review all entered data before submission
   - Verifikasi:
     - All 8 students have progress recorded
     - Summary statistics calculated correctly
     - Missing data highlighted if any

10. **Submit Progress Report**
    - Aksi: Click "Submit Weekly Progress"
    - Verifikasi:
      - Progress submitted successfully
      - Confirmation message shown
      - Data saved to database
      - Progress becomes read-only
      - Parents notification triggered (if configured)

### Hasil Diharapkan
- Weekly progress recorded untuk all 8 students
- Individual assessment scores documented
- Teacher observations dan recommendations noted
- Parent communication plans established

### Kriteria Sukses
- ✅ Progress interface user-friendly dan comprehensive
- ✅ All student data recorded accurately
- ✅ Teacher notes dan observations captured
- ✅ Submission process smooth dan confirmed
- ✅ Data persistence verified

---

## AKH-HP-007: Academic Admin - Generate Feedback Analytics Report

### Informasi Skenario
- **ID Skenario**: AKH-HP-007 (Aktivitas Kelas Harian - Happy Path - 007)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN (Quality Assurance)
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `FeedbackAnalyticsTest.adminGenerateReport()`

### Prasyarat
- Admin staff account: `academic.admin1` / `Welcome@YSQ2024`
- Completed feedback campaigns dengan sufficient responses
- Multiple teachers dengan feedback data
- Facility feedback data available

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Analytics Data Expected:
Teacher Evaluation Campaign: 45 responses (78% response rate)
Facility Assessment Campaign: 38 responses (66% response rate)  
Overall Experience Campaign: 52 responses (90% response rate)
Period: Last 30 days
Teachers Evaluated: 5 teachers
```

### Langkah Pengujian

#### Bagian 1: Access Analytics Dashboard
1. **Navigate to Feedback Analytics**
   - Aksi: Go to "Feedback" → "Analytics Dashboard"
   - Verifikasi:
     - Analytics main dashboard loads
     - Summary statistics visible
     - Campaign list dengan response rates
     - Filter options available

2. **Select Analysis Period**
   - Aksi: Set filter untuk "Last 30 days"
   - Verifikasi:
     - Date range picker works
     - Data refreshes based on selection
     - Response counts update accordingly

#### Bagian 2: Analyze Teacher Performance Data
3. **Teacher Evaluation Summary**
   - Aksi: Click pada "Teacher Evaluation" campaign
   - Verifikasi:
     - Detailed analytics page opens
     - Overall teacher ratings visible (avg 4.2/5.0)
     - Category breakdown (Teaching Quality, Communication, etc.)
     - Individual teacher performance summary

4. **Review Category Analysis**
   - Aksi: Drill down into specific categories
   - Verifikasi:
     - Teaching Quality: 4.3/5.0 average
     - Communication: 4.5/5.0 average  
     - Punctuality: 4.1/5.0 average
     - Fairness: 4.4/5.0 average
     - Charts dan graphs display correctly

5. **Identify Top Performers dan Areas for Improvement**
   - Aksi: Review teacher-specific performance
   - Verifikasi:
     - Top-rated teachers highlighted
     - Teachers needing support identified
     - Specific improvement areas noted

#### Bagian 3: Facility Assessment Analysis
6. **Facility Feedback Summary**
   - Aksi: Switch to facility assessment analytics
   - Verifikasi:
     - Facility ratings summary (avg 3.8/5.0)
     - Category breakdown (Cleanliness, Resources, Safety)
     - Priority issues identified

7. **Review Action Items**
   - Aksi: Check auto-generated action items
   - Verifikasi:
     - Urgent facilities issues flagged
     - Improvement priorities listed
     - Department assignments suggested

#### Bagian 4: Generate Comprehensive Report
8. **Configure Report Parameters**
   - Aksi: Set up comprehensive report generation
   - Data:
     - Report Type: Monthly Summary
     - Include: All campaigns
     - Audience: Management dan Teachers
     - Anonymity: Maintain student anonymity
   - Verifikasi: Report configuration options work

9. **Generate dan Download Report**
   - Aksi: Click "Generate Report"
   - Verifikasi:
     - Report generation progress indicator
     - PDF report created successfully
     - Download link provided
     - Email distribution options available

10. **Verify Report Content**
    - Aksi: Open generated PDF report
    - Verifikasi:
      - Executive summary included
      - Teacher performance insights
      - Facility improvement recommendations
      - Action items dengan priorities
      - Anonymity maintained for student feedback

### Hasil Diharapkan
- Comprehensive analytics report generated successfully
- Teacher performance insights clear dan actionable
- Facility improvement priorities identified
- Action items created dengan assignments

### Kriteria Sukses
- ✅ Analytics dashboard comprehensive dan intuitive
- ✅ Data visualization clear dan meaningful
- ✅ Report generation successful
- ✅ Action items generated appropriately
- ✅ Anonymity maintained throughout analysis

---

## AKH-HP-008: Parent - Respond to Teacher Change Notification

### Informasi Skenario
- **ID Skenario**: AKH-HP-008 (Aktivitas Kelas Harian - Happy Path - 008)
- **Prioritas**: Rendah
- **Role**: PARENT
- **Estimasi Waktu**: 5-7 menit
- **Playwright Test**: `ParentNotificationTest.teacherChangeResponse()`

### Prasyarat
- Parent account setup atau anonymous feedback access
- Child enrolled in class dengan teacher change
- Notification sent about substitute teacher
- Feedback system accessible to parents

### Data Test
```
Parent/Student Info:
Child Name: Ali Rahman
Class: Tahsin 1 - Senin Pagi
Original Teacher: Ustadz Ahmad (sick)
Substitute Teacher: Ustadzah Siti
Change Duration: 1 week
Notification Method: SMS + Email
Feedback Access: Anonymous link provided
```

### Langkah Pengujian

#### Bagian 1: Receive dan Review Notification
1. **Access Notification**
   - Aksi: Open SMS/email notification about teacher change
   - Verifikasi:
     - Clear explanation of teacher change
     - Substitute teacher information provided
     - Duration of substitution mentioned
     - Feedback link included

2. **Click Feedback Link**
   - Aksi: Click anonymous feedback link from notification
   - Verifikasi:
     - Anonymous feedback form opens
     - No login required
     - Clear instructions provided
     - Child's class context shown (without login details)

#### Bagian 2: Provide Feedback
3. **Rate Notification Process**
   - Aksi: Answer questions about communication
   - Data:
     - "How satisfied with notification timing?" → 4/5
     - "Information provided clear?" → Yes
     - "Advance notice sufficient?" → Yes
   - Verifikasi: Rating interface works properly

4. **Provide Additional Comments**
   - Aksi: Add optional feedback
   - Data: "Thank you for informing us promptly. Please let us know how Ali adjusts to the new teacher."
   - Verifikasi:
     - Text field accepts input
     - Character limit appropriate
     - Optional nature clear

#### Bagian 3: Submit Feedback
5. **Submit Parent Feedback**
   - Aksi: Click "Submit Feedback"
   - Verifikasi:
     - Submission successful
     - Thank you message displayed
     - Confirmation of anonymity
     - No personally identifiable info retained

### Hasil Diharapkan
- Parent feedback collected successfully
- Communication effectiveness measured
- Parent satisfaction with notification process recorded
- Input for improving future communications

### Kriteria Sukses
- ✅ Notification received dan understood clearly
- ✅ Feedback process simple dan anonymous
- ✅ Parent concerns captured appropriately
- ✅ Communication feedback system functional

---

## Summary Skenario Happy Path

### Total Coverage
- **8 Skenario Utama** covering daily class activities
- **5 User Roles**: Instructor, Student, Academic Admin, Parent, Quality Assurance
- **Key Workflows**: Session execution, feedback, monitoring, substitution, progress tracking

### Success Metrics
- ✅ All teacher check-in/check-out processes
- ✅ Student feedback collection dan anonymity
- ✅ Real-time monitoring capabilities
- ✅ Emergency substitution workflows  
- ✅ Progress recording dan reporting
- ✅ Analytics dan action item generation
- ✅ Parent communication effectiveness

### Next Phase
- **Alternate Path Scenarios**: Error handling, validation, edge cases
- **Integration Testing**: Cross-role workflow validation
- **Performance Testing**: High-load scenarios